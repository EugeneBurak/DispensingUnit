import dispenser.InitializedDispenser;
import dispenser.creator.crt531.CreatorCRT531;
import rabbitmq.RabbitMQClient;
import dispenser.DispenserResponce;
import dispenser.Glory_MiniMech_MM010_NRC_pac.Glory_MiniMech_MM010_NRC;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException {
        RabbitMQClient.getInstance();
        DispenserResponce.getInstance();
        InitializedDispenser initializedDispenser = new InitializedDispenser();
        initializedDispenser.initializedDispenser("creator_crt_531");
        while (true)    {
            try {                                       //для разгрузки ЦП и паузы между пингами
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Monitoring - command thread sleep - not OK!");
            }
        }
	// write your code here
    }
}
