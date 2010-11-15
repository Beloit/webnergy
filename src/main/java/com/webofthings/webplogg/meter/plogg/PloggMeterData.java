package com.webofthings.webplogg.meter.plogg;

import com.webofthings.webplogg.meter.ConsumptionData;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the PloggMeteredData class, is provides the data model
 * of what a Plogg can measure.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>, Yuan Peng
 * @version 1.0, 10/02/2010
 */
@XmlRootElement(name = "SmartMeterData")
public class PloggMeterData extends ConsumptionData {
    private String equipmentOnTime = "";

    /**
     * Creates a MeteredData object.
     */
    public PloggMeterData() {
    }

    /**
     * Creates a MeteredData object and initilizes variables passed in as
     * params.
     *
     * @param result
     *            the metered data result to be parsed
     */
    public PloggMeterData(String result) {
        setWatt(result);
        setCumulativeWattGen(result);
        setCumulativeWattCon(result);
        setDateTime(result);
        setFrequency(result);
        setRMSVoltage(result);
        setRMSCurrent(result);
        setReactivePower(result);
        setAccReactivePwrCon(result);
        setAccReactivePwrGen(result);
        setPhaseAngle(result);
        setUpTime(result);
        setEquipmentOnTime(result);
    }

    /**
     * parse the result to get watt
     *
     * @param result
     *            the result that contain the information of watt
     */
    public synchronized void setWatt(String result) {

        if (result.indexOf("Watts (-Gen +Con)") > 0) {
            String wattString = result.substring(result.indexOf("Watts (-Gen +Con)"));
            super.setWatt(Double.valueOf(wattString.substring(
                    wattString.indexOf("=") + 2, wattString.indexOf("~") - 3)));
        }
    }

    /**
     * parse the result to get cumulative watt (Gen)
     *
     * @param result
     *            the result that contain the information of cumulative watt
     *            (Gen)
     */
    public synchronized void setCumulativeWattGen(String result) {
        if (result.indexOf("Cumulative Watts (Gen)") > 0) {
            String cumuWattGenString = result.substring(result.indexOf("Cumulative Watts (Gen)"));
            super.setCumulativeWattGenerated(Double.valueOf(cumuWattGenString.substring(
                    cumuWattGenString.indexOf("=") + 2, cumuWattGenString.indexOf("~") - 5)));
        }
    }

    /**
     * parse the result to get the cumulative watt (Con)
     *
     * @param result
     *            the result that contain the information of cumulative watt
     *            (Con)
     */
    public synchronized void setCumulativeWattCon(String result) {

        if (result.indexOf("Cumulative Watts (Con)") > 0) {
            String cumuWattConString = result.substring(result.indexOf("Cumulative Watts (Con)"));
            super.setCumulativeWattConsumed(Double.valueOf(cumuWattConString.substring(
                    cumuWattConString.indexOf("=") + 2, cumuWattConString.indexOf("~") - 5)));
        }

    }

    /**
     * parse the result to get date and time
     *
     * @param result
     *            the result that contain the information of date and time
     */
    public synchronized void setDateTime(String result) {

        int timeEntry = result.indexOf("Time entry");

        if (timeEntry > 0) {
            int yearIndex = result.indexOf("=", timeEntry);

            String year = result.substring(yearIndex + 2, yearIndex + 6).trim();
            String month = result.substring(yearIndex + 7, yearIndex + 10).trim();
            String date = result.substring(yearIndex + 11, yearIndex + 13);
            String time = result.substring(yearIndex + 14, yearIndex + 22);
            super.setDateTime(year + "-" + month + "-" + date + " " + time);
        }
    }

    /**
     * parse the result to get frequency
     *
     * @param result
     *            the result that contain the information of frequency
     */
    public synchronized void setFrequency(String result) {

        if (result.indexOf("Frequency") > 0) {
            String frequencyString = result.substring(result.indexOf("Frequency"));
            super.setFrequency(Double.valueOf(frequencyString.substring(
                    frequencyString.indexOf("=") + 2, frequencyString.indexOf("~") - 4)));
        }

    }

    /**
     * parse the result to get RMS voltage
     *
     * @param result
     *            the result that contain the information of RMS voltage
     */
    public synchronized void setRMSVoltage(String result) {

        if (result.indexOf("RMS Voltage") > 0) {
            String RMSVoltageString = result.substring(result.indexOf("RMS Voltage"));
            super.setRMSVoltage(Double.valueOf(RMSVoltageString.substring(
                    RMSVoltageString.indexOf("=") + 2, RMSVoltageString.indexOf("~") - 3)));
        }
    }

    /**
     * Parse the result to get RMS current
     *
     * @param result
     *            the result that contain the information of RMS current
     */
    public synchronized void setRMSCurrent(String result) {

        if (result.indexOf("RMS Current") > 0) {
            String RMSCurrentString = result.substring(result.indexOf("RMS Current"));
            super.setRMSCurrent(Double.parseDouble(RMSCurrentString.substring(
                    RMSCurrentString.indexOf("=") + 2, RMSCurrentString.indexOf("~") - 3)));
        }
    }

    /**
     * parse the result to get the time about how long the plogg is on
     *
     * @param result
     *            the result that contain the information about how long the
     *            plogg is on
     */
    public synchronized void setUpTime(String result) {
        if (result.indexOf("Plogg on time") > 0) {
            String ploggOnTimeString = result.substring(result.indexOf("Plogg on time"));
            super.setUpTime(ploggOnTimeString.substring(ploggOnTimeString.indexOf("=") + 2, ploggOnTimeString.indexOf("~") - 1));
        }
    }

    /**
     * parse the result to get reactive power
     *
     * @param result
     *            the result that contain the information of reactive power
     */
    public synchronized void setReactivePower(String result) {

        if (result.indexOf("Reactive Power (-G/+C)") > 0) {
            String reactivePowerString = result.substring(result.indexOf("Reactive Power (-G/+C)"));
            super.setReactivePower(Double.valueOf(reactivePowerString.substring(
                    reactivePowerString.indexOf("=") + 2,
                    reactivePowerString.indexOf("~") - 5).trim()));
        }

    }

    /**
     * parse the result to get acc reactive power (Gen)
     *
     * @param result
     *            the result that contain the information of acc reactive power
     *            (Gen)
     */
    public synchronized void setAccReactivePwrGen(String result) {

        if (result.indexOf("Acc Reactive Pwr (Gen)") > 0) {
            String accReactivePwrGenString = result.substring(result.indexOf("Acc Reactive Pwr (Gen)"));
            super.setAccReactivePowerGenerated(Double.valueOf(accReactivePwrGenString.substring(accReactivePwrGenString.indexOf("=") + 2,
                    accReactivePwrGenString.indexOf("~") - 7)));
        }

    }

    /**
     * parse the result to get acc reactive power (Con)
     *
     * @param result
     *            the result that contain the information of acc reactive power
     *            (Con)
     */
    public synchronized void setAccReactivePwrCon(String result) {

        if (result.indexOf("Acc Reactive Pwr (Con)") > 0) {
            String accReactivePwrConString = result.substring(result.indexOf("Acc Reactive Pwr (Con)"));
            super.setAccReactivePowerConsumed(Double.valueOf(accReactivePwrConString.substring(accReactivePwrConString.indexOf("=") + 2,
                    accReactivePwrConString.indexOf("~") - 7)));
        }

    }

    /**
     * parse the result to get phase angle
     *
     * @param result
     *            the result that contain the information of phase angle
     */
    public synchronized void setPhaseAngle(String result) {

        if (result.indexOf("Phase Angle (V/I)") > 0) {
            String phaseAngleString = result.substring(result.indexOf("Phase Angle (V/I)"));
            super.setPhaseAngle(Double.valueOf(phaseAngleString.substring(
                    phaseAngleString.indexOf("=") + 2, phaseAngleString.indexOf("~") - 9)));
        }

    }

    /**
     * Sets the time since which the equipment connected to the Plogg
     * is on.
     *
     * @param result a result string containing the equipment on time information
     */
    public synchronized void setEquipmentOnTime(String result) {

        if (result.indexOf("Equipment on time") > 0) {
            String equipmentOnTimeString = result.substring(result.indexOf("Equipment on time"));
            equipmentOnTime = equipmentOnTimeString.substring(
                    equipmentOnTimeString.indexOf("=") + 2,
                    equipmentOnTimeString.indexOf("~") - 1);
        }

    }

    /**
     * This method returns the on (or up) time of the equipment monitored
     * by the Plogg.
     * @return the on time of the connected equipment.
     */
    public synchronized String getEquipmentOnTime() {
        return equipmentOnTime;
    }
}
