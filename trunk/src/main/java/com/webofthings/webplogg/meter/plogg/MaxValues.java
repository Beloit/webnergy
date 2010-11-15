package com.webofthings.webplogg.meter.plogg;
import com.webofthings.webplogg.meter.dongle.Telegesis;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is the MaxValues class, it provides all the methods required to get the
 * maximum value on the plogg.
 * 
 * 
 * @author Yuan Peng
 * @version 1.0, 10/02/2010
 */
public class MaxValues {
	Command comm;
	Telegesis tele;
	LinkedBlockingQueue<Object> queue;

	private double maxRMSVoltage = 0.0;
	private Date maxRMSVoltageTime = null;
	private double maxRMSCurrent = 0.0;
	private Date maxRMSCurrentTime = null;
	private double maxRMSWattage = 0.0;
	private Date maxRMSWattageTime = null;

	/**
	 * Creates a MaxValues object and initilizes variables passed in as params.
	 * 
	 * @param comm
	 *            the command object
	 * @param tele
	 *            the plog connection object
	 * @param queue
	 *            the queue store the result from plogg
	 */
	public MaxValues(Command comm, Telegesis tele,
			LinkedBlockingQueue<Object> queue) {
		this.comm = comm;
		this.tele = tele;
		this.queue = queue;
	}

	/**
	 * reset the maximum values of plogg
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void resetMaxValues(String ploggID) {
		tele.write(comm.max(ploggID) + "1\r");
		tele.read(false);
	}

	/**
	 * get the highest value of RMS voltage
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the highest value of RMS voltage
	 */
	public synchronized double getMaxRMSVoltage(String ploggID) {
		tele.write(comm.max(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		if (result.indexOf("Highest RMS voltage was") > 0) {
			String maxRMSVoltageString = result.substring(result
					.indexOf("Highest RMS voltage was"));
			maxRMSVoltage = Double.valueOf(maxRMSVoltageString.substring(24,
					maxRMSVoltageString.indexOf("at") - 3));
		}
		return maxRMSVoltage;
	}

	/**
	 * get the time at which there is the highest value of RMS voltage
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the time at which there is the highest value of RMS voltage
	 */
	public synchronized Date getMaxRMSVoltageTime(String ploggID) {
		tele.write(comm.max(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		if (result.indexOf("Highest RMS voltage was") > 0) {
			int yearIndex = result.indexOf("at", result
					.indexOf("Highest RMS voltage was"));
			String year = result.substring(yearIndex + 3, yearIndex + 7).trim();
			String month = result.substring(yearIndex + 8, yearIndex + 11)
					.trim();
			String date = result.substring(yearIndex + 12, yearIndex + 14);
			String time = result.substring(yearIndex + 15, yearIndex + 23);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
			try {
				maxRMSVoltageTime = sdf.parse(year + "-" + month + "-" + date
						+ " " + time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return maxRMSVoltageTime;
	}

	/**
	 * get the highest value of RMS current
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the highest value of RMS current
	 */
	public synchronized double getMaxRMSCurrent(String ploggID) {
		tele.write(comm.max(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		if (result.indexOf("Highest RMS current was") > 0) {
			String maxRMSCurrentString = result.substring(result
					.indexOf("Highest RMS current was"));
			maxRMSCurrent = Double.valueOf(maxRMSCurrentString.substring(24,
					maxRMSCurrentString.indexOf("at") - 3));
		}
		return maxRMSCurrent;
	}

	/**
	 * get the time at which there is the highest value of RMS current
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the time at which there is the highest value of RMS current
	 */
	public synchronized Date getMaxRMSCurrentTime(String ploggID) {
		tele.write(comm.max(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		if (result.indexOf("Highest RMS current was") > 0) {
			int yearIndex = result.indexOf("at", result
					.indexOf("Highest RMS current was"));
			String year = result.substring(yearIndex + 3, yearIndex + 7).trim();
			String month = result.substring(yearIndex + 8, yearIndex + 11)
					.trim();
			String date = result.substring(yearIndex + 12, yearIndex + 14);
			String time = result.substring(yearIndex + 15, yearIndex + 23);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
			try {
				maxRMSCurrentTime = sdf.parse(year + "-" + month + "-" + date
						+ " " + time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return maxRMSCurrentTime;
	}

	/**
	 * get the highest value of RMS wattage
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the highest value of RMS wattage
	 */
	public synchronized double getMaxRMSWattage(String ploggID) {
		tele.write(comm.max(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		if (result.indexOf("Highest wattage was") > 0) {
			String maxRMSWattageString = result.substring(result
					.indexOf("Highest wattage was"));
			maxRMSWattage = Double.valueOf(maxRMSWattageString.substring(20,
					maxRMSWattageString.indexOf("W at") - 1));

		}
		return maxRMSWattage;
	}

	/**
	 * get the time at which there is the highest value of RMS wattage
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the time at which there is the highest value of RMS wattage
	 */
	public synchronized Date getMaxRMSWattageTime(String ploggID) {
		tele.write(comm.max(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		if (result.indexOf("Highest wattage was") > 0) {
			int yearIndex = result.indexOf("at ", result
					.indexOf("Highest wattage was"));
			String year = result.substring(yearIndex + 3, yearIndex + 7).trim();
			String month = result.substring(yearIndex + 8, yearIndex + 11)
					.trim();
			String date = result.substring(yearIndex + 12, yearIndex + 14);
			String time = result.substring(yearIndex + 15, yearIndex + 23);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
			try {
				maxRMSWattageTime = sdf.parse(year + "-" + month + "-" + date
						+ " " + time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return maxRMSWattageTime;
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
