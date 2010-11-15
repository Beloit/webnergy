package com.webofthings.webplogg.meter;

import com.webofthings.webplogg.meter.plogg.PloggMeterData;
import com.webofthings.webplogg.meter.plogg.PloggManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class PloggExample {

	static LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<Object>(
			10);
	static List<String> idScan;
	static ArrayList<PloggMeterData> logList = new ArrayList<PloggMeterData>();
	static String ploggID = "0021ED00000448E8";
	
	
	public static void main(String[] args) throws InterruptedException,
			IOException {
		String portName = "COM46";
		if (args.length > 0) {
			portName = args[0];
		}
		

		PloggManager manager = (PloggManager) PloggManager.getInstance();
		
		 idScan = manager.scanPlogg();
		
		 for(int index=0;index<idScan.size();index++){
		 System.out.println(idScan.get(index));
	
		 }
				
		System.out.println(manager.getDongleInfo());

		System.out.println(manager.getWatt(ploggID));
//		System.out.println(manager.getCumulativeWattGen("0021ED0000034A8B"));
//		System.out.println(manager.getCumulativeWattCon(ploggID));
//		System.out.println(manager.getDateTime(ploggID));
//		System.out.println(manager.getFrequency(ploggID));
//		System.out.println(manager.getRMSVoltage(ploggID));
		// System.out.println(manager.getRMSCurrent(ploggID));
		// System.out.println(manager.getPloggOnTime(ploggID));
		// System.out.println(manager.getReactivePower(ploggID));
		// System.out.println(manager.getAccReactivePwrGen(ploggID));
		// //System.out.println(manager.getAccReactivePwrCon(ploggID));
		// //System.out.println(manager.getPhaseAngle(ploggID));
		// System.out.println(manager.getEquipmentOnTime(ploggID));
		// manager.setTariffCost(ploggID, "", "");
		// System.out.println(manager.getTariff0Cost(ploggID));
		// // //System.out.println(manager.getTariff1Cost(ploggID));
		// System.out.println(manager.getMaxRMSVoltage(ploggID));
		// System.out.println(manager.getMaxRMSVoltageTime(ploggID));
		// double current=manager.getMaxRMSWattage("0021ED0000044515");
		// Date currentTime=manager.getMaxRMSCurrentTime("0021ED0000044515");
		// Date currentTime=manager.getMaxRMSWattageTime(ploggID);
		// manager.setDeviceName(ploggID, "hello");
		// System.out.println(manager.getDeviceName(ploggID));
		// manager.setRTCDate("0021ED0000044515", "10", "03", "04");
		// manager.logInterval(ploggID, "10");
		// manager.setTimerInfo(ploggID, "1000", "1010");

		// //System.out.println(manager.getTimerInfo(ploggID, "0"));
		// //System.out.println(manager.getTimerStatus(ploggID));
		// manager.enableTimers(ploggID);
		// System.out.println(manager.getTimerStatus(ploggID));
		// if(current==-1){
		// System.out.println("No highest current was recorded.");
		// }
		// else{
		// System.out.println(current);
		// }
		// if(!(currentTime==null)){
		// System.out.println(currentTime);
		// }
		// else{
		// System.out.println("No highest current was recorded.");
		// }
		// logList=manager.getLogValues(ploggID);
		// manager.getLogValues(ploggID);
		// manager.getLogFile(ploggID, "log");
		// manager.getLogValues(ploggID);
		// manager.off(ploggID);
		// manager.off(ploggID);
		// manager.on(ploggID);
		// manager.off(ploggID);
		// System.out.println(manager.ploggInfo(ploggID).getWatt());
		// System.out.println(manager.printPloggInfo(ploggID));
		manager.closeConnection();

	}

}
