package com.bkyziol.hexapod.movement;

import jssc.SerialPort;
import jssc.SerialPortException;

public class ServoController {

	private static final String PORT_NAME = "/dev/ttyAMA0";
	private static SerialPort serialPort;

	static {
		openSerialPort(PORT_NAME);
	}

	public static void openSerialPort(String portName) {
		serialPort = new SerialPort(portName);
		try {
			if (!serialPort.isOpened()) {
				serialPort.openPort();
				serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
				// serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
				serialPort.writeByte((byte) 0xAA);
				clearErrors();
				setHeadSpeed(0);
				setLegsSpeed(0);
				System.out.println("Connection to the servo controller is open");
			}
		} catch (SerialPortException e) {
			System.out.println("Error while opening connection to the servo controller: " + e);
		}
	}

	public static void closePort() {
		try {
			if (serialPort.isOpened()) {
				serialPort.closePort();
				System.out.println("Connection to the servo controller is closed");
			}
		} catch (SerialPortException e) {
			System.out.println("Error while closing connection to the servo controller: " + e);
		}
	}

	public static void setTarget(int channel, int target ){
		try {
			byte[] command = {(byte) 0x84, (byte) channel, (byte) (target & 0x7F), (byte) (target >> 7 & 0x7F)};
			serialPort.writeBytes(command);
		} catch (Exception e) {
			System.out.println("Error while setting the servo position");
		}
	}

	public static void clearErrors() {
		try {
			serialPort.writeByte((byte) 0xA1);
		} catch (Exception e) {
			System.out.println("Error while cleaning the servo controller errors");
		} 
	}

	public static void setHeadSpeed(int speed) {
		try {
			for (int channel = 19; channel <= 20; channel++) {
				byte[] command = {(byte) 0x87, (byte) channel, (byte)(speed & 0x7F), (byte)(speed >> 7 & 0x7F)};
				serialPort.writeBytes(command);
			}
		} catch (Exception e) {
			System.out.println("Error while setting the servo speed of camera");
		}
	}

	public static void setLegsSpeed(int speed){
		try {
			for (int channel = 0; channel <= 17; channel++) {
				byte[] command = {(byte) 0x87, (byte) channel, (byte)(speed & 0x7F), (byte)(speed >> 7 & 0x7F)};
				serialPort.writeBytes(command);
			}
		} catch (Exception e) {
			System.out.println("Error while setting the servo speed of hexapod");
		}
	}
}