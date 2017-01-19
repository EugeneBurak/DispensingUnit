package commands;

import dispenser.InitializedDispenser;
import dispenser.creator.crt531.CreatorCRT531;
import rabbitmq.RabbitMQClient;
import dispenser.Glory_MiniMech_MM010_NRC_pac.Glory_MiniMech_MM010_NRC;


import javax.json.JsonObject;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
/**
 * Created by java_dev on 30.09.16.
 */
public class CommandManager {

    public void sendCommand(JsonObject command) throws IOException, TimeoutException {    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        System.out.println("---------------------- COMMAND MANAGER ----------------------");
        System.out.println(command.getString("device") + " : " + command.getString("command"));

//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//
//        JsonObject state = Json.createObjectBuilder().add("command", command.getString("command")).add("start_time",date.getTime()/1000).build();
//
//        RabbitMQClient.getInstance().sendState(state.toString());

//        System.out.println(command.getString("command")+ " : " + dateFormat.format(date));

        switch (command.getString("device")){
            case "dispenser":{
                switch(command.getString("command")){
                    case "extradite" : {
//                        Glory_MiniMech_MM010_NRC.getInstance().dispenseBill(command.getString("dispense_bill"));
//                        CreatorCRT531.getInstance().dispenseBill(command.getString("dispense_bill"));
                        InitializedDispenser.getCurrentDispenser().dispenseBill(command.getString("dispense_bill"));

                        break;
                    }
                    case "status" : {
//                        Glory_MiniMech_MM010_NRC.getInstance().getDispenserStatus();
//                        CreatorCRT531.getInstance().setDispenserStatus("11");
//                        CreatorCRT531.getInstance().getDispenserStatus();
                        InitializedDispenser.getCurrentDispenser().setDispenserStatus("11");
                        InitializedDispenser.getCurrentDispenser().getDispenserStatus();

                        break;
                    }

                    default:{
                        System.out.println("Some command what i don't know!");
                        break;
                    }
                }
                break;
            }
            default : {
                System.out.println("default command");
                break;
            }
        }
        RabbitMQClient.getInstance().purgeQueue(2);
    }
}
