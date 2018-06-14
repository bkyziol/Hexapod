package com.bkyziol.hexapod.movement;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

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
				serialPort.setParams(
						SerialPort.BAUDRATE_9600,
						SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE
					);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
				// serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
				serialPort.writeByte((byte) 0xAA);
				clearErrors();
				setHeadSpeed(0);
				setBodySpeed(0);
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

	public static void setTarget(int channel, int target) {
		try {
			byte[] command = { (byte) 0x84, (byte) channel, (byte) (target & 0x7F), (byte) (target >> 7 & 0x7F) };
			serialPort.writeBytes(command);
		} catch (SerialPortException e) {
			System.out.println("Error while setting the servo " + channel + " position: " + e);
		}
	}

	public static void clearErrors() {
		try {
			serialPort.writeByte((byte) 0xA1);
		} catch (SerialPortException e) {
			System.out.println("Error while cleaning the servo controller errors: " + e);
		}
	}

	public static void setHeadSpeed(int speed) {
		try {
			for (int channel = 19; channel <= 20; channel++) {
				byte[] command = { (byte) 0x87, (byte) channel, (byte) (speed & 0x7F), (byte) (speed >> 7 & 0x7F) };
				serialPort.writeBytes(command);
			}
		} catch (SerialPortException e) {
			System.out.println("Error while setting the servo speed of hexapod head: " + e);
		}
	}

	public static void setBodySpeed(int speed) {
		try {
			for (int channel = 0; channel <= 17; channel++) {
				byte[] command = { (byte) 0x87, (byte) channel, (byte) (speed & 0x7F), (byte) (speed >> 7 & 0x7F) };
				serialPort.writeBytes(command);
			}
		} catch (SerialPortException e) {
			System.out.println("Error while setting the servo speed of hexapod body: " + e);
		}
	}

	public static int getMovingState() {
		byte command = (byte) 0x93;
		byte[] response = new byte[2];
//		String response;
//		response[0] = 2;
//		int ret = -1;
		try {
			serialPort.writeString("");
			serialPort.writeByte(command);
			System.out.println("request: " + command);
//			try {
//				Thread.sleep(100);
				response = serialPort.readBytes(2, 100);
//				response = serialPort.readString();
				System.out.println("response: " + response[0] + ", " + response[1]);
			} catch (SerialPortTimeoutException e) {
				System.out.println("timeout");
//			} catch (InterruptedException e) {
//				System.out.println("InterruptedException");
//			}
//			if (response[0] >= 0) {
//				ret = response[0];
//			}
		} catch (SerialPortException e) {
			System.out.println("Error while getting moving state: " + e);
		}
//		System.out.println("getMovingState: " + ret);
//		return ret;
		return 0;
	}
}