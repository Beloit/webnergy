/**
 * This is the Plogg package, it contains the class that provide the method 
 * for control of plogg
 */
package com.webofthings.webplogg.meter.plogg;

import com.webofthings.webplogg.meter.SmartMeter;
import com.webofthings.webplogg.meter.ConsumptionData;
import com.webofthings.webplogg.meter.SmartMeterManager;
import com.webofthings.webplogg.meter.config.Configurator;
import com.webofthings.webplogg.meter.dongle.Telegesis;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the PloggManager class, it provides all the methods required on the
 * plogg. It is a interface to manage plogg.
 * 
 * 
 * @author Yuan Peng and <a href="http://www.guinard.org">Dominique Guinard</a>
 * @version 1.0, 10/02/2010
 */
public class PloggManager extends SmartMeterManager implements Runnable {

	private static SmartMeterManager UNIQUE_MANAGER = null;
	static LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<Object>(
			10);
	private static String MY_TYPE = "Plogg";
	Command comm = new Command();
	Telegesis tele;
	PloggMeterData meteredData = new PloggMeterData();
	TariffCosts tariffCosts;
	MaxValues maxValues;
	LoggedValues loggedValues;
	Timer timer;
	private String deviceName = "";
	private int syncInterval = 10000;

	/**
	 * Creates a ploggManager object and initilizes variables passed in as
	 * params.
	 *
	 * @param portName
	 *            The port to be open.
	 */
	private PloggManager(String portName) {
		try {
			tele = new Telegesis(portName, queue);
			tariffCosts = new TariffCosts(comm, tele, queue);
			maxValues = new MaxValues(comm, tele, queue);
			loggedValues = new LoggedValues(comm, tele, queue);
			timer = new Timer(comm, tele, queue);
			tele.connect();
			syncInterval = Integer.parseInt(Configurator.getInstance().getProperty("PLOGG_SYNC_INTERVAL"));
		} catch (Exception ex) {
			Logger.getLogger(PloggManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * get plogg metered data
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the MeteredData object
	 */
	public synchronized ConsumptionData ploggInfo(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		ConsumptionData meteredDataFull = new PloggMeterData(result);
		final String msg = "Received plogg information from plogg " + ploggID + "; Power consumption: " + meteredDataFull.getWatt();
		Logger.getLogger(PloggManager.class.getName()).log(Level.INFO, msg);
		return meteredDataFull;
	}

	/**
	 * get dongle information
	 *
	 * @return the string contain the information of dongle
	 */
	public synchronized String getDongleInfo() {
		tele.write(comm.getDongleInfo());
		tele.read(true);
		String result = getResult();
		return result.substring(result.indexOf("ATI") + 5, result.indexOf("OK"));
	}

	/**
	 * close the connection to the port. so close the connection to the dongle
	 * and plogg
	 */
	public void closeConnection() {
		tele.closeConnection();
	}

	/**
	 * find all the plogg in the network
	 *
	 * @return the ID of each plogg in the network
	 */
	public synchronized List<String> scanPlogg() {
		tele.write(comm.scan());
		tele.read(true);
		String result = getResult();
		if (result.indexOf("ERROR:91") > 0) {
			setupPAN();
			result = getResult();
		}

		ArrayList<String> scan = new ArrayList<String>();
		while (result.indexOf("FFD:") > 0) {
			String id = result.substring(result.indexOf("FFD:") + 4);
			String idString = id.substring(0, 16).trim();
			scan.add(idString);
			result = id;
		}

		return scan;

	}

	/**
	 * This method sets up the Zigbee PAN (Personal Area Network).
	 */
	public synchronized void setupPAN() {
		tele.write(comm.setPANID());
		tele.read(true);
		tele.write(comm.setChannelMask());
		tele.read(true);
		tele.write(comm.resetModule());
		tele.read(true);
		tele.write(comm.establishPAN());
		tele.read(true);
		tele.write(comm.getNetworkInfo());
		tele.read(true);
	}

	/**
	 * get watt
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the watt
	 */
	public synchronized double getWatt(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setWatt(result);
		return meteredData.getWatt();
	}

	/**
	 * get cumulative watt (Gen)
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return cumulative watt (Gen)
	 */
	public synchronized double getCumulativeWattGen(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setCumulativeWattGen(result);
		return meteredData.getCumulativeWattGenerated();
	}

	/**
	 * get cumulative watt (Con)
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return cumulative watt (Con)
	 */
	public synchronized double getCumulativeWattCon(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setCumulativeWattCon(result);
		return meteredData.getCumulativeWattConsumed();
	}

	/**
	 * get the date and time
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the date and time
	 */
	public synchronized Date getDateTime(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setDateTime(result);
		return meteredData.getDateTime();
	}

	/**
	 * get the frequency
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the frequency
	 */
	public synchronized double getFrequency(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setFrequency(result);
		return meteredData.getFrequency();
	}

	/**
	 * get the RMS voltage
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the RMS voltage
	 */
	public synchronized double getRMSVoltage(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setRMSVoltage(result);
		return meteredData.getRMSVoltage();
	}

	/**
	 * get the RMS current
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the RMS current
	 */
	public synchronized double getRMSCurrent(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setRMSCurrent(result);
		return meteredData.getRMSCurrent();
	}

	/**
	 * get the time about how long the plogg is on
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the time about how long the plogg is on
	 */
	public synchronized String getPloggOnTime(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setUpTime(result);
		return meteredData.getUpTime();
	}

	/**
	 * get the reactive power
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the reactive power
	 */
	public synchronized double getReactivePower(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setReactivePower(result);
		return meteredData.getReactivePower();
	}

	/**
	 * get acc reactive power (Gen)
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return acc reactive power (Gen)
	 */
	public synchronized double getAccReactivePwrGen(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setAccReactivePwrGen(result);
		return meteredData.getAccReactivePowerGenerated();
	}

	/**
	 * get acc reactive power (Con)
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return acc reactive power (Con)
	 */
	public synchronized double getAccReactivePwrCon(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setAccReactivePwrCon(result);
		return meteredData.getAccReactivePowerConsumed();
	}

	/**
	 * get phase angle
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return phase angle
	 */
	public synchronized double getPhaseAngle(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setPhaseAngle(result);
		return meteredData.getPhaseAngle();
	}

	/**
	 * get the time about how long the equipment is on
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the time about how long the equipment is on
	 */
	public synchronized String getEquipmentOnTime(String ploggID) {
		tele.write(comm.info(ploggID));
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		meteredData.setEquipmentOnTime(result);
		return meteredData.getEquipmentOnTime();
	}

	/**
	 * get the tariff 0 cost
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the tariff 0 cost
	 */
	public synchronized double getTariff0Cost(String ploggID) {
		return tariffCosts.getTariff0Cost(ploggID);
	}

	/**
	 * get the tariff 1 cost
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the tariff 1 cost
	 */
	public synchronized double getTariff1Cost(String ploggID) {
		return tariffCosts.getTariff1Cost(ploggID);
	}

	/**
	 * set the tariff cost
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @param tariff
	 *            set the cost of which tariff
	 * @param cost
	 *            the cost of the tariff
	 */
	public synchronized void setTariffCost(String ploggID, String tariff,
			String cost) {
		tariffCosts.setTariffCost(ploggID, tariff, cost);
	}

	/**
	 * get the highest value of RMS voltage
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the highest value of RMS voltage
	 */
	public synchronized double getMaxRMSVoltage(String ploggID) {
		return maxValues.getMaxRMSVoltage(ploggID);
	}

	/**
	 * get the time at which there is the highest value of RMS voltage
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the time at which there is the highest value of RMS voltage
	 */
	public synchronized Date getMaxRMSVoltageTime(String ploggID) {
		return maxValues.getMaxRMSVoltageTime(ploggID);
	}

	/**
	 * get the highest value of RMS current
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the highest value of RMS current
	 */
	public synchronized double getMaxRMSCurrent(String ploggID) {
		return maxValues.getMaxRMSCurrent(ploggID);
	}

	/**
	 * get the time at which there is the highest value of RMS current
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the time at which there is the highest value of RMS current
	 */
	public synchronized Date getMaxRMSCurrentTime(String ploggID) {
		return maxValues.getMaxRMSCurrentTime(ploggID);
	}

	/**
	 * get the highest value of RMS wattage
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the highest value of RMS wattage
	 */
	public synchronized double getMaxRMSWattage(String ploggID) {
		return maxValues.getMaxRMSWattage(ploggID);
	}

	/**
	 * get the time at which there is the highest value of RMS wattage
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the time at which there is the highest value of RMS wattage
	 */
	public synchronized Date getMaxRMSWattageTime(String ploggID) {
		return maxValues.getMaxRMSWattageTime(ploggID);
	}

	/**
	 * reset the maximum value
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void resetMaxValues(String ploggID) {
		maxValues.resetMaxValues(ploggID);
	}

	/**
	 * get log information, the information of each log entry contained in a
	 * MeteredData object
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the list that store the MeteredData objects
	 */
	public synchronized List<PloggMeterData> getLogValues(String ploggID) {
		return loggedValues.getLoggedValues(ploggID);
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
	public synchronized void getLogFile(String ploggID, String logName)
	throws IOException {
		loggedValues.getLoggedFile(ploggID, logName);
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
		loggedValues.logInterval(ploggID, interval);
	}

	/**
	 * get the status of the timers, enabled or disabled
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return true for enabled, false for disabled
	 */
	public synchronized boolean getTimerStatus(String ploggID) {
		return timer.getTimerStatus(ploggID);
	}

	/**
	 * enable the timers
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void enableTimers(String ploggID) {
		timer.enableTimers(ploggID);
	}

	/**
	 * disable the timers
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void disableTimers(String ploggID) {
		timer.disableTimers(ploggID);
	}

	/**
	 * get the information of the timer
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the information of the timer
	 */
	public synchronized String getTimerInfo(String ploggID) {
		return timer.getTimerInfo(ploggID);
	}

	/**
	 * set the information of the timer
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @param on
	 *            the timer is on from what time
	 * @param off
	 *            the timer is off at what time
	 */
	public synchronized void setTimerInfo(String ploggID, String on, String off) {
		timer.setTimerInfo(ploggID, on, off);
	}

	/**
	 * close the device on the plogg
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void off(String ploggID) {
		loggedValues.logInterval(ploggID, "10");
		timer.setTimerInfo(ploggID, "0000", "0000");
		timer.enableTimers(ploggID);
		// timer.disableTimers(ploggID);
		// timer.enableTimers(ploggID);
	}

	/**
	 * open the device on the plogg
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void on(String ploggID) {
		loggedValues.logInterval(ploggID, "10");
		timer.setTimerInfo(ploggID, "0000", "0000");
		timer.disableTimers(ploggID);

	}

	/**
	 * get the name of the plogg/device
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the name of the plogg/device
	 */
	public synchronized String getDeviceName(String ploggID) {
		if (ploggID == null) return null;
		
		tele.write(comm.device(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		String deviceNameString = result.substring(result.indexOf("Unit friendly name"));
		deviceName = deviceNameString.substring(
				deviceNameString.indexOf("=") + 2, deviceNameString.indexOf("~"));
		return deviceName;
	}

	/**
	 * set the name of the plogg/device
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @param name
	 *            the name of the plogg/device
	 */
	public synchronized void setDeviceName(String ploggID, String name) {
		tele.write(comm.device(ploggID) + " " + name + "\r");
		tele.read(false);
	}

	/**
	 * set the date of the plogg
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @param year
	 * @param month
	 * @param day
	 */
	public synchronized void setRTCDate(String ploggID, String year,
			String month, String day) {
		tele.write(comm.setDate(ploggID) + year + "." + month + "." + day
				+ "\r");
		tele.read(false);
	}

	/**
	 * set the time of the plogg
	 *
	 * @param ploggID
	 *            the ID of the plogg
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public synchronized void setRTCTime(String ploggID, String hour,
			String minute, String second) {
		tele.write(comm.setTime(ploggID) + hour + "." + minute + "." + second
				+ "\r");
		tele.read(false);
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
			result = result.substring(result.indexOf("UCAST:", result.indexOf("UCAST:" + ploggID + "=") + 23));

		}
		return parsedResult;
	}

	@Override
	public ConsumptionData getDataFromMeter(String id) {
		return super.getSmartMeter(id).getLatestConsumption();
	}
	
	FutureTask<String> timedDiscovery = null;

	class TimedDiscoveryRunnable implements Callable {
		
		private String ploggID = null;
		
		public TimedDiscoveryRunnable(String ploggID) {
			this.ploggID = ploggID;
		}		
		
		public String call() {
			return getDeviceName(ploggID);
		}	
	}
	
	@Override
	public void discoverSmartMeters() {
		List<String> ploggsIds = scanPlogg();
		for (String currentId : ploggsIds) {
			/* Find out information for each Plogg */

			String name = null;
			
			timedDiscovery = new FutureTask<String>(new TimedDiscoveryRunnable(currentId));

			new Thread(timedDiscovery).start();
			
			try {
				/* Wait at most for 10 seconds to get an answer from the Plogg */
				name = timedDiscovery.get(10L, TimeUnit.SECONDS);
				System.out.println("Plogg name: " + name);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				System.err.println("No answer from Plogg " + currentId);
			}
			
			/* if the timers are on it means the device is actually not powered */
			//boolean status = !timer.getTimerStatus(currentId);
			boolean status = true;
			super.addSmartMeter(new SmartMeter(currentId, MY_TYPE, name, status));
		}

	}

	@Override
	public void turnOffPower(String smartMeterId) {
		off(smartMeterId);
		super.getSmartMeter(smartMeterId).setStatus(false);
	}

	@Override
	public void turnOnPower(String smartMeterId) {
		on(smartMeterId);
		super.getSmartMeter(smartMeterId).setStatus(true);
	}

	public static SmartMeterManager getInstance() {
		if (UNIQUE_MANAGER == null) {
			return UNIQUE_MANAGER =
				new PloggManager(Configurator.getInstance().
						getProperty("PLOGG_DONGLE_PORT"));
		} else {
			return UNIQUE_MANAGER;
		}
	}

	private void syncMeters() {
		for (String currentId : this.getManagedSmartMeters().keySet()) {
			Logger.getLogger(PloggManager.class.getName()).log(Level.INFO, "Syncing Meter:" + currentId);
			SmartMeter currentMeter = super.getSmartMeter(currentId);
			currentMeter.setLatestConsumption(this.ploggInfo(currentId));
		}
	}

	/**
	 * This starts polling the SmartMeters on a regular basis.
	 */
	public void run() {
		for (;;) {
			try {
				syncMeters();
				Thread.sleep(syncInterval);
			} catch (InterruptedException ex) {
				Logger.getLogger(PloggManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}
}
