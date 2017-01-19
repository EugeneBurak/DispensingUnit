package dispenser.creator.crt531;

import dispenser.ComPortForDispenser;
import dispenser.DispenserInterface;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by java_dev on 10.01.17.
 */
public class CreatorCRT531 implements DispenserInterface {

    private static CreatorCRT531 creatorChina = null;
    static String comPortForDispenserName = "/dev/ttyS0";
    private static ComPortForDispenser comPortForDispenser;

    private static int[] creatorHighStatusChecking = {0x02, 0x41, 0x50, 0x03, 0x10};          //High Status Checking
    private static int[] creatorStatusChecking = {0x02, 0x52, 0x46, 0x03, 0x15};          //Status checking
    private static int[] creatorDispense = {0x02, 0x44, 0x43, 0x03, 0x06};          //Dispensing card
    private static int[] creatorENQ = {0x05};           //Command ENQ

    private static boolean firstSet = false;
    private static ArrayList<Integer> responceFromDevice = new ArrayList<>();

    private static volatile String statusDispenser = "";

    private CreatorCRT531()    {
        comPortForDispenser = new ComPortForDispenser(comPortForDispenserName);
        comPortForDispenser.initCom(9600, 8, 1, 0);
        if (comPortForDispenser.statusCOM()) {
            System.out.println(" ----- START OK ----- ");
        } else {
            System.out.println("--- Initialization Com Port = false ---");
        }
    }

    public static CreatorCRT531 getInstance() {
        if (creatorChina == null)    {
            creatorChina = new CreatorCRT531();
            return creatorChina;
        }   else {
            return creatorChina;
        }
    }

       /*
    commands [3]:
    0x00 - Didn't do anything
    0x01 - Wrong XOR or command format - TO REPEAT THE ANSWER!!!
    0x45 - UPPER DISPENSE - dispenser with one bill box
    0x56 - UPPER and LOWER DISPENSE - dispenser with two bill box
    0x57 - dispenser three-box
     */
    /*
    Errors [5]:
    0x00 - No Errors
    0x01 - Wrong XOR or command format
    0x02 - Command which I don't know!
    0x11 - Bad
     */
    /*
    Bill cassette status [6]:
    0 - No bill
    1 - OK
    2 - The bill ends
    3 - I don't know...
     */

    @Override
    public void dispenseBill(String command)    {
        comPortForDispenser.sendCommand(creatorDispense, 100);
        comPortForDispenser.sendCommand(creatorENQ, 100);
    }

    @Override
    public void startDispenser() {
        comPortForDispenser.sendCommand(creatorHighStatusChecking, 100);
        comPortForDispenser.sendCommand(creatorENQ, 100);
        System.out.println(" ----- Сreator High Status Checking ----- ");
    }

    @Override
    public void restartDispenser() {
        //NOP
    }

    @Override
    public void reinitializeDispenser() {
        //NOP
    }

    @Override
    public String getDispenserStatus() {
        System.out.println(" >>> creatorStatusChecking >>> ");
        comPortForDispenser.sendCommand(creatorStatusChecking, 100);
        System.out.println("Dispenser status - " + statusDispenser);
        System.out.println(" >>> creatorENQ >>> ");
        comPortForDispenser.sendCommand(creatorENQ, 100);
        System.out.println("Dispenser status - " + statusDispenser);
        return statusDispenser;
    }

    @Override
    public void setDispenserStatus(String status) {
        statusDispenser = status;
    }

    @Override
    public void fromComPort(int[] fromComPort) {
        try {
//            System.out.println(" ----- from Com Port ----- " + Arrays.toString(fromComPort));

            if ((fromComPort.length == 1 && !firstSet) || (fromComPort.length > 1 && fromComPort[0] == 0x02 && fromComPort[fromComPort.length - 2] == 0x03))    {
                analysisOfResponse(fromComPort);
            }
            if (fromComPort.length > 1 && fromComPort[0] == 0x02 && !firstSet)    {
                firstSet = true;
            }
            if (fromComPort.length >= 1 && firstSet)    {
                for (int aFromComPort : fromComPort) {
                    responceFromDevice.add(aFromComPort);
                }
            }
            if (responceFromDevice.size() > 1 && responceFromDevice.get(0) == 0x02 && responceFromDevice.get(responceFromDevice.size() - 2) == 0x03)  {
                int[] bufferResponceFromDispenser = new int[responceFromDevice.size()];
                for (int ii = 0; ii < bufferResponceFromDispenser.length; ii++) {
                    bufferResponceFromDispenser[ii] = responceFromDevice.get(ii);
                }
                analysisOfResponse(bufferResponceFromDispenser);
                firstSet = false;
                responceFromDevice.clear();
            }
            if (responceFromDevice.size() > 11) {          //Сообщение склеино НЕ верно
                firstSet = false;
                responceFromDevice.clear();
            }

        } catch (Exception exc) {
            System.out.println("");
            System.out.println("                                        Ошибка в классе CreatorCRT531 метод fromComPort");
            System.out.println("");
            exc.printStackTrace();
        }

    }

    private void analysisOfResponse(int[] responceFromDispenser) {

        System.out.println(" ----- analysis Of Response ----- " + Arrays.toString(responceFromDispenser));

        if (responceFromDispenser.length == 1 && responceFromDispenser[0] == 0x06)  {
            System.out.println("No Errors!");
            statusDispenser = "No Errors!";
        }
        if (responceFromDispenser.length == 9 && responceFromDispenser[3] == 48)   {           //[2, 83, 70, 48, 48, 49, 48, 3, 21]
            System.out.println("High Status Checking - OK.");
            statusDispenser = "High Status Checking - OK.";
        }
        if (responceFromDispenser.length == 8 /*&& responceFromDispenser[3] == 48*/ && responceFromDispenser[4] == 48 && responceFromDispenser[5] == 48)   {
            System.out.println("Stacker is full.");
            statusDispenser = "Stacker is full.";
        }
        if (responceFromDispenser.length == 8 /*&& responceFromDispenser[3] == 48*/ && responceFromDispenser[4] == 49 && responceFromDispenser[5] == 48)   {
            System.out.println("Stacker is Pre-empty.");
            statusDispenser = "Stacker is Pre-empty.";
        }
        if (responceFromDispenser.length == 8 /*&& responceFromDispenser[3] == 48*/ && responceFromDispenser[4] == 49 && responceFromDispenser[5] == 56)   {
            System.out.println("Stacker is empty!");
            statusDispenser = "Stacker is empty!";
        }
        if (responceFromDispenser.length == 8 && (responceFromDispenser[5] == 49 || responceFromDispenser[5] == 57))   {
            System.out.println("The card was left!");
            statusDispenser = "The card was left!";
        }
    }
}
