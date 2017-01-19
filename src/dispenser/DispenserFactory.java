package dispenser;

import dispenser.creator.crt531.CreatorCRT531;

/**
 * Created by java_dev  - Eugene Burak - on 30.09.16.
 */

class DispenserFactory {
    static DispenserInterface createDispenser(String dispenserName) {
        switch (dispenserName)  {
            case "creator_crt_531":{
                return CreatorCRT531.getInstance();
            }
            case "glory_puloon":{
                return null;
            }
            default: {
                System.out.println("Dispenser - default command");
                return null;
            }
        }
    }
}
