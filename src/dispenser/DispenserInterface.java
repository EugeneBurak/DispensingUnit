package dispenser;

/**
 * Created by java_dev  - Eugene Burak - on 30.09.16.
 */
public interface DispenserInterface {
    void dispenseBill(String command);
    void startDispenser();
    void restartDispenser();
    void reinitializeDispenser();
    String getDispenserStatus();
    void setDispenserStatus(String status);
    void fromComPort(int[] fromComPort);
}
