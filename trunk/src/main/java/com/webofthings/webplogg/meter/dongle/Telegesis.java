/**
 * This is the dongle package, it contains the class that provide the method 
 * for connection of dongle and plogg
 */
package com.webofthings.webplogg.meter.dongle;

import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import gnu.io.*;

/**
 * This is the Telegesis class, it provides the methods to connect and
 * disconnect to the dongle and plogg.
 * 
 * 
 * @author Yuan Peng
 * @version 1.0, 10/02/2010
 */
public class Telegesis implements SerialPortEventListener {
	Enumeration<?> portList;
	CommPortIdentifier portId;
	SerialPort serialPort;
	OutputStream outputStream;
	static boolean outputBufferEmptyFlag = false;
	InputStream inputStream;
	boolean portFound = false;
	boolean connectionOpen = false;
	volatile boolean isReadDongle = false;
	String result = "";
	String portName;
	LinkedBlockingQueue<Object> queue;
	BufferedReader in = null;

	/**
	 * Creates a Telegesis object and initilizes variables passed in as params.
	 * 
	 * @param portName
	 *            the name of the port
	 * @param queue
	 *            the queue that store all the result sent from plogg
	 */
	public Telegesis(String portName, LinkedBlockingQueue<Object> queue) {
		this.portName = portName;
		this.queue = queue;
	}


        public void connect() throws Exception {

		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(),
					2000);

			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				inputStream = serialPort.getInputStream();
				outputStream = serialPort.getOutputStream();

				outputStream.write("ATI\r\n".getBytes());

			}
		}
	}


	/**
	 * connect to plogg
	 */
	public void connection() {
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();

			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				if (portId.getName().equals(portName)) {
					portFound = true;
					try {
						serialPort = (SerialPort) portId.open("Write", 2000);
					} catch (PortInUseException e) {
						System.out.println("Port in use.");
						continue;
					}

					setConnectionParameters();

					try {
						outputStream = serialPort.getOutputStream();
						inputStream = serialPort.getInputStream();
					} catch (IOException e) {
					}

					try {
						serialPort.notifyOnOutputEmpty(true);
						connectionOpen = true;
					} catch (Exception e) {
						System.out.println("Error setting event notification");
						System.out.println(e.toString());
						System.exit(-1);
					}
				}
			}
		}

		if (!portFound) {
			System.out.println("port " + portName + " not found.");

		}

	}

	/**
	 * close connection to the plogg
	 */
	public void closeConnection() {

		// If port is alread closed just return.
		if (!connectionOpen) {
			return;
		}

		// Check to make sure serialPort has reference to avoid a NPE.
		if (serialPort != null) {
			try {

				// close the i/o streams.
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {
				System.err.println(e);
			}

			// Close the port.
			serialPort.close();
		}
		connectionOpen = false;
	}

	/**
	 * set the connection parameters to connect to the dongle
	 */
	public synchronized void setConnectionParameters() {

		try {
			serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
		}

	}

	/**
	 * write the command to plogg
	 * 
	 * @param messageString
	 */
	public synchronized void write(String messageString) {
		System.out.println("\r\n Writing \"" + messageString + "\" to "
				+ serialPort.getName());

		queue.clear();
		result = "";

		try {
			outputStream.write(messageString.getBytes());
		} catch (IOException e) {
		}

		try {
			Thread.sleep(2000); // Be sure data is transferred before closing
		} catch (Exception e) {
		}
	}

	/**
	 * read the result from plogg/dongle. If read from dongle, sleep for 8
	 * second to guarantee the command is done. For example, for scanning all
	 * the ploggs in the network, or establishing the PAN.
	 * 
	 * @param isReadDongle
	 *            whether read information from donge or from plogg
	 */
	public synchronized void read(boolean isReadDongle) {
		this.isReadDongle = isReadDongle;
		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
		}

		serialPort.notifyOnDataAvailable(true);
		if (isReadDongle) {
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public synchronized void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {

		case SerialPortEvent.BI:

		case SerialPortEvent.OE:

		case SerialPortEvent.FE:

		case SerialPortEvent.PE:

		case SerialPortEvent.CD:

		case SerialPortEvent.CTS:

		case SerialPortEvent.DSR:

		case SerialPortEvent.RI:

		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;

		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[20];
			in = new BufferedReader(new InputStreamReader(inputStream));
			result = "";
			try {
				String input = null;
				if (isReadDongle) {
					while (inputStream.available() > 0) {
						int tmp = 0;
						tmp = inputStream.read(readBuffer);
						if (tmp > 0) {
							result = result + new String(readBuffer);
						}
					}
					isReadDongle = false;
				} else {
					while ((input = in.readLine()).indexOf("~~>") < 0) {
						result = result + input;
					}
					result = result + input;
				}
				try {
					queue.put(result);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
			}

			break;
		}

	}

}
