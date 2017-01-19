package rabbitmq;

import com.rabbitmq.client.*;
import commands.CommandManager;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeoutException;

/**
 * Created by java_dev - Eugene Burak -  on 30.09.16.
 */
public class RabbitMQClient {
    private final static String QUEUE_QUESTION_NAME = "interface";          //incoming messages
    private final static String QUEUE_ANSWER_NAME = "hardware";         //outgoing messages
    private final static String QUEUE_MONITORING_STATE = "Monitoring";          //timestamps - worthless piece
    private final static String QUEUE_PING_STATE = "ping";          //incoming&response  ping - worthless piece


    private Channel channel;
    private Consumer consumer;

    private static CommandManager commandManager = new CommandManager();


    private static RabbitMQClient rabbitMQClient = null;

    private RabbitMQClient() throws IOException, TimeoutException {

        ConnectionFactory connectionFactory = new ConnectionFactory();

        connectionFactory.setHost("localhost");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("java");
        connectionFactory.setPassword("fsrtKf4D5Algb");

        Connection connection = connectionFactory.newConnection();

        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_QUESTION_NAME,true,false,false,null).getQueue();
        channel.queueDeclare(QUEUE_ANSWER_NAME,true,false,false,null).getQueue();
        channel.queueDeclare(QUEUE_MONITORING_STATE,true,false,false,null);
        channel.queueDeclare(QUEUE_PING_STATE,true,false,false,null);           //

        channel.basicQos(1);

        getConsumer();

        channel.basicConsume(QUEUE_QUESTION_NAME, true, consumer);
    }

    private void getConsumer(){
        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                //switch case commands list
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                JsonReader jsonReader = Json.createReader(new StringReader(message));
                JsonObject command = jsonReader.readObject();
                jsonReader.close();
                System.out.println(" [x] Received JSON '" + command.toString() + "'");
                try {
                    commandManager.sendCommand(command);
                }
                catch (Exception e){
                    getConsumer();
                }
            }
        };
    }

    public static RabbitMQClient getInstance() throws IOException, TimeoutException {
        if(rabbitMQClient==null){
            rabbitMQClient =  new RabbitMQClient();
            return rabbitMQClient;
        }
        else
            return rabbitMQClient;
    }

    public void purgeQueue(int queueID){
        try {
            switch (queueID) {
                case 1: {
                    channel.queuePurge(QUEUE_ANSWER_NAME);
                    break;
                }
                case 2: {
                    channel.queuePurge(QUEUE_MONITORING_STATE);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot purge queue!!!");
        }
    }

    public void sendMessage(String message){            //work messages
        try {
            channel.basicPublish("", QUEUE_ANSWER_NAME, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("");
        System.out.println("");
        System.out.println(" [x] Sent '" + message + "'");
        System.out.println("");
        System.out.println("");
    }

    public void sendState(String state){            //timestamps
        try {
            channel.basicPublish("", QUEUE_MONITORING_STATE, null, state.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" [x] Sent '" + state + "'");
    }

    public void sendPing(String ping){              //response to ping
        try {
            channel.basicPublish("", QUEUE_PING_STATE, null, ping.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" [x] Sent '" + ping + "'");
    }

}
