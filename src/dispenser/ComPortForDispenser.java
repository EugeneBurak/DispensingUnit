package dispenser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import dispenser.Glory_MiniMech_MM010_NRC_pac.Glory_MiniMech_MM010_NRC;
import dispenser.creator.crt531.CreatorCRT531;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by java_dev  - Eugene Burak - on 30.09.16.
 */
public class ComPortForDispenser {

    private static String portName;
    private static SerialPort serialPort;
    public ComPortForDispenser(String comPortName){
        portName = comPortName;
    }

    public boolean initCom(int baudRate, int dataBits, int stopBits, int parity){
        serialPort = new SerialPort(portName);
        try{
            serialPort.openPort();
            serialPort.setParams(baudRate, dataBits, stopBits, parity);
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
            serialPort.addEventListener(new SerialPortListener());
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private static class SerialPortListener implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {
                int eventValue = event.getEventValue();
                if (eventValue != 0) {
                    try {
                        int[] incomingData = serialPort.readIntArray();//

//                        System.out.println(">>>>>>>>>>>>>>>>>> incomingData >>>>>>>>>>>>>>>>>> - " + Arrays.toString(incomingData));

                        try {
                            notifyObservers(incomingData);
                        } catch (Exception e) {
                            System.out.println("Error in transfer of data to notifyObservers(incomingData)");
                            e.printStackTrace();
                        }
                    } catch (SerialPortException e) {
                        System.out.println("Error in reading int array");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    boolean closeCOM() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            System.out.println("exception : " + e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean statusCOM() {
        if (serialPort.isOpened()) {
            System.out.println(portName + " is opened");
            return true;
        } else {
            System.out.println(portName + " is not opened");
            return false;
        }
    }

    public boolean sendCommand(int[] command, int delay) {
        try {
            serialPort.writeIntArray(command);
        } catch (SerialPortException e) {
            //e.printStackTrace();
            return false;
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("error in send cmd_sleep " + e.toString());
            return false;
        }
        return true;
    }

    private static void notifyObservers(int[] receivingBuffer) throws IOException, TimeoutException {
        int array[] = receivingBuffer;

//        Glory_MiniMech_MM010_NRC.getInstance().fromComPort(array);
//        CreatorCRT531.getInstance().fromComPort(array);
        InitializedDispenser.getCurrentDispenser().fromComPort(array);

    }
}

