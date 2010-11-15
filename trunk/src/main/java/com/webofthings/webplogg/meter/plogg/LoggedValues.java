package com.webofthings.webplogg.meter.plogg;

import com.webofthings.webplogg.meter.dongle.Telegesis;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is the LoggedValues class, it provides all the methods required to get
 * the information of log entries
 * 
 * 
 * @author Yuan Peng
 * @version 1.0, 10/02/2010
 */
public class LoggedValues {
	Command comm;
	Telegesis tele;
	LinkedBlockingQueue<Object> queue;
	private static String logfile;

	/**
	 * Creates a LoggedValues object and initilizes variables passed in as
	 * params.
	 * 
	 * @param comm
	 *            the command object
	 * @param tele
	 *            the plog connection object
	 * @param queue
	 *            the queue store the result from plogg
	 */
	public LoggedValues(Command comm, Telegesis tele,
			LinkedBlockingQueue<Object> queue) {
		this.comm = comm;
		this.tele = tele;
		this.queue = queue;
	}

	/**
	 * delete log values
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void deleteLoggedValues(String ploggID) {
		tele.write(comm.deleteLog(ploggID));
		tele.read(false);
	}

	/**
	 * reset the log values
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void resetLoggedValues(String ploggID) {
		tele.write(comm.log(ploggID) + " 1\r");
		tele.read(false);
	}

	/**
	 * set the interval of log entry
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @param interval
	 *            the interval of log entry
	 */
	public synchronized void logInterval(String ploggID, String interval) {
		tele.write(comm.logInterval(ploggID) + " " + interval + "\r");
		tele.read(false);
	}

	/**
	 * get log information, the information of each log entry contained in a
	 * MeteredData object
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the list that store the MeteredData objects
	 */
	public synchronized ArrayList<PloggMeterData> getLoggedValues(String ploggID) {
		tele.write(comm.log(ploggID) + "\r");
		// queue.clear();
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		ArrayList<PloggMeterData> logList = new ArrayList<PloggMeterData>();
		if (result.indexOf("Log entry") > 0) {
			int start = result.indexOf("(", result.indexOf("Log contains"));
			int end = result.indexOf("of", result.indexOf("Log contains"));
			int count = Integer.parseInt(result.substring(start + 1, end - 1));
			result = result + "~~~~Log entry";
			for (int i = 0; i < count; i++) {
				int startEntry = result.indexOf("~~~~Log entry");
				int endEntry = result.indexOf("~~~~Log entry", startEntry + 4);
				String entry = result.substring(startEntry, endEntry + 1);
				PloggMeterData meteredData = new PloggMeterData();
				meteredData.setAccReactivePwrCon(entry);
				meteredData.setAccReactivePwrGen(entry);
				meteredData.setCumulativeWattCon(entry);
				meteredData.setCumulativeWattGen(entry);
				meteredData.setDateTime(entry);
				meteredData.setEquipmentOnTime(entry);
				meteredData.setFrequency(entry);
				meteredData.setPhaseAngle(entry);
				meteredData.setUpTime(entry);
				meteredData.setReactivePower(entry);
				meteredData.setRMSCurrent(entry);
				meteredData.setRMSVoltage(entry);
				meteredData.setWatt(entry);
				// System.out.println(meteredData.getWatt());
				logList.add(meteredData);
				result = result.substring(endEntry);
			}

		}
		return logList;

	}

	/**
	 * get the file which store the log information
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @param logName
	 *            the name of log file
	 * @throws IOException
	 */
	public synchronized void getLoggedFile(String ploggID, String logName)
			throws IOException {
		tele.write(comm.log(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		// System.out.println("string len: " + result.length());
		result = getParsingResult(result, ploggID);
		logfile = logName + ".txt";
		PrintWriter outFile = new PrintWriter(new FileWriter(logfile, true),
				true);
		outFile.println(result);
		outFile.close();
	}

	/**
	 * get the result from the plogg
	 * 
	 * @return the result from the plogg
	 */
	private String getResult() {

		while (true) {
			try {
				Object result = queue.take();
				return result.toString();
			} catch (InterruptedException e) {
				System.out.println("interrupted!");
			}
		}
	}

	/**
	 * parse the result, remove the repeated message head
	 * 
	 * @param result
	 *            the result from the plogg
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the parsed result
	 */
	private synchronized String getParsingResult(String result, String ploggID) {
		String parsedResult = "";
		result = result + "UCAST:";
		result = result.substring(result.indexOf("ACK:00"));
		while (result.indexOf("UCAST:" + ploggID + "=") >= 0
				&& parsedResult.indexOf("~~>") < 0) {

			parsedResult = parsedResult
					+ result.substring(
							result.indexOf("UCAST:" + ploggID + "=") + 23,
							result.indexOf("UCAST:", result.indexOf("UCAST:"
									+ ploggID + "=") + 23));
			result = result.substring(result.indexOf("UCAST:", result
					.indexOf("UCAST:" + ploggID + "=") + 23));

		}
		return parsedResult;
	}
}
