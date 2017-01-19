package dispenser;

import rabbitmq.RabbitMQClient;
import org.json.simple.JSONArray;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by java_dev  - Eugene Burak - on 03.10.16.
 */
public class DispenserResponce {
    private static DispenserResponce dispenserResponce = null;

    private DispenserResponce()    {}

    public static DispenserResponce getInstance() {
        if (dispenserResponce == null)    {
            dispenserResponce = new DispenserResponce();
            return dispenserResponce;
        }   else {
            return dispenserResponce;
        }
    }

    public void dispenserResponce (int[] responce)  {
        JSONArray responceResult = new JSONArray();
        /*
        for (int i = 0; i < responce.length; i++)   {
            responceResult.add(responce[i]);
        }
         */
        for (int aResponce : responce) {
            responceResult.add(aResponce);
        }
        JsonObject commandToJson = Json.createObjectBuilder().add("command", "bill_rejected_from_dispenser").add("currency", responceResult.toJSONString()).build();
        try {
            RabbitMQClient.getInstance().sendMessage(commandToJson.toString());
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
