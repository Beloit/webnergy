package com.webofthings.webplogg.meter.plogg;

import com.webofthings.webplogg.meter.dongle.Telegesis;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is the Timer class, it provides all the methods required to get/set the
 * information of timer
 * 
 * 
 * @author Yuan Peng
 * @version 1.0, 10/02/2010
 */
public class Timer {
	Command comm;
	Telegesis tele;
	LinkedBlockingQueue<Object> queue;
	private boolean timerEnable = false;
	private String timerInfo = "";

	/**
	 * Creates a Timer object and initilizes variables passed in as params.
	 * 
	 * @param comm
	 *            the command object
	 * @param tele
	 *            the plog connection object
	 * @param queue
	 *            the queue store the result from plogg
	 */
	public Timer(Command comm, Telegesis tele,
			LinkedBlockingQueue<Object> queue) {
		this.comm = comm;
		this.tele = tele;
		this.queue = queue;
	}

	/**
	 * get the status of the timer, enabled or disabled
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return true for enabled, false for disabled
	 */
	public synchronized boolean getTimerStatus(String ploggID) {
		tele.write(comm.timer(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		if (result.indexOf("Timers are enabled") > 0) {
			timerEnable = true;
		} else {
			timerEnable = false;
		}
		return timerEnable;
	}

	/**
	 * enable the timer
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void enableTimers(String ploggID) {
		tele.write(comm.enableTimer(ploggID));
		tele.read(false);
	}

	/**
	 * disable the timer
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 */
	public synchronized void disableTimers(String ploggID) {
		tele.write(comm.diableTimer(ploggID));
		tele.read(false);
	}

	/**
	 * get the information of the timer
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the information of the timer
	 */
	public synchronized String getTimerInfo(String ploggID) {
		tele.write(comm.timer(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		String timerString = result.substring(result.indexOf("Timer 0"));
		timerInfo = timerString.substring(timerString.indexOf("=") + 2,
				timerString.indexOf("~"));

		return timerInfo;
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
		tele.write(comm.timer(ploggID) + " 0" + " " + on + "-" + off + "\r");
		tele.read(false);
		tele.write(comm.timer(ploggID) + " 1" + " " + on + "-" + off + "\r");
		tele.read(false);
		tele.write(comm.timer(ploggID) + " 2" + " " + on + "-" + off + "\r");
		tele.read(false);
		tele.write(comm.timer(ploggID) + " 3" + " " + on + "-" + off + "\r");
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
			result = result.substring(result.indexOf("UCAST:", result
					.indexOf("UCAST:" + ploggID + "=") + 23));

		}
		return parsedResult;
	}

}
