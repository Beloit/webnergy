package com.webofthings.webplogg.meter.plogg;

/**
 * This is the command class, it provides all the command required for operating
 * on the plogg.
 * 
 * 
 * @author Yuan Peng
 * @version 1.0, 10/02/2010
 */
public class Command {
	String ploggID = "0021ED000004451C";

	/**
	 * command to get metered data information
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String info(String ploggID) {
		return "AT+UCAST:" + ploggID + "=SV\r";
	}

	/**
	 * command to get maximum values
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String max(String ploggID) {
		return "AT+UCAST:" + ploggID + "=SM";
	}

	/**
	 * command to get log values
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String log(String ploggID) {
		return "AT+UCAST:" + ploggID + "=SD";
	}

	/**
	 * command to set interval of log entry
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String logInterval(String ploggID) {
		return "AT+UCAST:" + ploggID + "=Si";
	}

	/**
	 * command to delete log values
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String deleteLog(String ploggID) {
		return "AT+UCAST:" + ploggID + "=Sx\r";
	}

	/**
	 * command to get timer information
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String timer(String ploggID) {
		return "AT+UCAST:" + ploggID + "=SO";
	}

	/**
	 * command to enable timer
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String enableTimer(String ploggID) {
		return "AT+UCAST:" + ploggID + "=SE 1\r";
	}

	/**
	 * command to disable timer
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String diableTimer(String ploggID) {
		return "AT+UCAST:" + ploggID + "=SE 0\r";
	}

	/**
	 * command to get device name
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String device(String ploggID) {
		return "AT+UCAST:" + ploggID + "=SN";
	}

	/**
	 * command to get tariff cost information
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String tariff(String ploggID) {
		return "AT+UCAST:" + ploggID + "=SS";
	}

	/**
	 * command to date of plogg
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String setDate(String ploggID) {
		return "AT+UCAST:" + ploggID + "=rtd";
	}

	/**
	 * command to set time of plogg
	 * 
	 * @param ploggID
	 *            the ID of the plogg
	 * @return the command
	 */
	public String setTime(String ploggID) {
		return "AT+UCAST:" + ploggID + "=rtt";
	}

	/**
	 * command to scan the ploggs in the network
	 * 
	 * @return the command
	 */
	public String scan() {
		return "AT+SN\r\n";
	}

	/**
	 * command to set PAN ID
	 * 
	 * @return the command
	 */
	public String setPANID() {
		return "ATS01=31f4\r";
	}

	/**
	 * command to set channel mask
	 * 
	 * @return the command
	 */
	public String setChannelMask() {
		return "ATS01=0001\r";
	}

	/**
	 * command to establish PAN
	 * 
	 * @return the command
	 */
	public String establishPAN() {
		return "AT+EN\r";
	}

	/**
	 * command to get dongle information
	 * 
	 * @return the command
	 */
	public String getDongleInfo() {
		return "ATI\r";
	}

        public String resetModule() {
            return "ATZ\r";
        }

        public String getNetworkInfo() {
            return "AT+N\r";
        }
}
