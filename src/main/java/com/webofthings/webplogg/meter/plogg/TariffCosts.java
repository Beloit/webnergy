package com.webofthings.webplogg.meter.plogg;
import com.webofthings.webplogg.meter.dongle.Telegesis;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is the TariffCosts class, it provides all the methods required to
 * get/set the information of tariff cost
 * 
 * 
 * @author Yuan Peng
 * @version 1.0, 10/02/2010
 */
public class TariffCosts {
	Command comm;
	Telegesis tele;
	LinkedBlockingQueue<Object> queue;

	private double tariff0Cost = 0.0;
	private double tariff1Cost = 0.0;

	/**
	 * Creates a TariffCosts object and initilizes variables passed in as
	 * params.
	 * 
	 * @param comm
	 *            the command object
	 * @param tele
	 *            the plog connection object
	 * @param queue
	 *            the queue store the result from plogg
	 */
	public TariffCosts(Command comm, Telegesis tele,
			LinkedBlockingQueue<Object> queue) {
		this.comm = comm;
		this.tele = tele;
		this.queue = queue;
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
		tele.write(comm.tariff(ploggID) + tariff + cost + "\r");
		tele.read(false);
	}

	/**
	 * get the tariff 0 cost
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the cost of tariff 0
	 */
	public synchronized double getTariff0Cost(String ploggID) {
		tele.write(comm.tariff(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		if (result.indexOf("Tarrif 0 Cost ") > 0) {
			String tariff0CostString = result.substring(result
					.indexOf("Tarrif 0 Cost"));
			tariff0Cost = Double.valueOf(tariff0CostString.substring(
					tariff0CostString.indexOf("=") + 2, tariff0CostString
							.indexOf("pence/Kwh") - 1));
		}
		return tariff0Cost;
	}

	/**
	 * get the tariff 1 cost
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the cost fo tariff 1
	 */
	public synchronized double getTariff1Cost(String ploggID) {
		tele.write(comm.tariff(ploggID) + "\r");
		tele.read(false);
		String result = getResult();
		result = getParsingResult(result, ploggID);
		if (result.indexOf("Tarrif 1 Cost ") > 0) {
			String tariff1CostString = result.substring(result
					.indexOf("Tarrif 1 Cost"));
			tariff1Cost = Double.valueOf(tariff1CostString.substring(
					tariff1CostString.indexOf("=") + 2, tariff1CostString
							.indexOf("pence/Kwh") - 1));
		}
		return tariff1Cost;
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
