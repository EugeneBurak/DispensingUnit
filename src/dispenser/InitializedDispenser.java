package dispenser;

/**
 * Created by java_dev  - Eugene Burak - on 30.09.16.
 */
public class InitializedDispenser {

    public static DispenserInterface getCurrentDispenser() {
        return currentDispenser;
    }

    private static DispenserInterface currentDispenser;

    public void initializedDispenser(String dispenserName)  {
        currentDispenser = DispenserFactory.createDispenser(dispenserName);
        currentDispenser.startDispenser();
    }
}


