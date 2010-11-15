/*
 *  This class is part of the Energie Visible WebofThings project.
 *  http://www.webofthings.com/energievisible/
 *  (c) Dominique Guinard (www.guinard.org)
 *  Institute for Pervasive Computing, ETH Zurich
 *  and Cudrefin02.ch.
 */
package com.webofthings.webplogg.meter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is class common to the data of all SmartMeters. It
 * contains all the data fields a traditional SmartMeter (e.g. a Plogg)
 * should provide.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
@XmlRootElement(name = "SmartMeterData")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public abstract class ConsumptionData {

    private double watt;
    private double cumulativeWattGenerated;
    private double cumulativeWattConsumed;
    private Date dateTime;
    private double frequency;
    private double RMSVoltage;
    private double RMSCurrent;
    private String upTime;
    private double reactivePower;
    private double accReactivePowerGenerated;
    private double accReactivePowerConsumed;
    private double phaseAngle;
    private double maxWatt;


    /**
     * This gets the reactive power consumption
     *
     * @return reactive power consumption
     */
    @XmlElement
    public double getAccReactivePowerConsumed() {
        return accReactivePowerConsumed;
    }

    /**
     * Get the reactive power generated
     *
     * @return reactive power generated
     */
    @XmlElement
    public double getAccReactivePowerGenerated() {
        return accReactivePowerGenerated;
    }

    /**
     * Gets the cumulative watt consumed
     *
     * @return cumulative watt consumed
     */
    @XmlElement
    public double getCumulativeWattConsumed() {
        return cumulativeWattConsumed;
    }

    /**
     * Gets the cumulative watt generated
     *
     * @return cumulative watt generated
     */
    @XmlElement
    public double getCumulativeWattGenerated() {
        return cumulativeWattGenerated;
    }

    /**
     * Gets the current date and time on the Smart Meter
     *
     * @return data and time
     */
    @XmlElement
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * This returns the uptime of the smart meter.
     *
     * @return the time about how long the equipment is on
     */
    @XmlElement
    public String getUpTime() {
        return upTime;
    }

    /**
     * Gets the current frequency recorded by the smart meter.
     *
     * @return frequency
     */
    @XmlElement
    public double getFrequency() {
        return frequency;
    }

    /**
     * Gets the phase angle.
     *
     * @return phase angle
     */
    @XmlElement
    public double getPhaseAngle() {
        return phaseAngle;
    }

    /**
     * Get RMS (Root Mean Square) current
     *
     * @return RMS current
     */
    @XmlElement
    public double getRMSCurrent() {
        return RMSCurrent;
    }

    /**
     * Get RMS (Root Mean Square) voltage
     *
     * @return RMS voltage
     */
    @XmlElement
    public double getRMSVoltage() {
        return RMSVoltage;
    }

    /**
     * Get the reactive power
     *
     * @return the reactive power
     */
    @XmlElement
    public double getReactivePower() {
        return reactivePower;
    }

    /**
     * Get the current consumption in Watts
     *
     * @return current consumption [Watts]
     */
    @XmlElement
    public double getWatt() {
        return watt;
    }

    /**
     * This gets the maximal number of consumed watts detected so far.
     * @return The maximal number of watts.
     */
    public double getMaxWatt() {
        return maxWatt;
    }

    /**
     * This sets the maximal number of consumed watts detected so far.
     * @param maxWatt The maximal number of watts.
     */
    public void setMaxWatt(double maxWatt) {
        this.maxWatt = maxWatt;
    }


    /**
     * Acc reactive power (Con)
     *
     * @param reactivePowerCon
     */
    public void setAccReactivePowerConsumed(double reactivePowerCon) {
        this.accReactivePowerConsumed = reactivePowerCon;
    }

    /**
     * get acc reactive power (Gen)
     *
     * @param reactivePowerGen
     */
    public void setAccReactivePowerGenerated(double reactivePowerGen) {
        this.accReactivePowerGenerated = reactivePowerGen;
    }

    /**
     * Sets the cumulative watt (Con)
     *
     * @param cumulativeWattCon
     */
    public void setCumulativeWattConsumed(double cumulativeWattCon) {
        this.cumulativeWattConsumed = cumulativeWattCon;
    }

    /**
     * Sets cumulative watt (Gen)
     *
     * @param cumulativeWattGenerated
     */
    public void setCumulativeWattGenerated(double cumulativeWattGenerated) {
        this.cumulativeWattGenerated = cumulativeWattGenerated;
    }

    /**
     * Sets the SmartMeter date and time.
     *
     * @param dateTime the date and time as a String in the following format:
     * yyyy-MMM-dd HH:mm:ss
     */
    public void setDateTime(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.US);
        try {
            setDateTime(sdf.parse(dateTime));
        } catch (ParseException ex) {
            Logger.getLogger(ConsumptionData.class.getName()).log(Level.SEVERE, "A problem occured while" +
                    " parsing the date: " + dateTime, ex);
            ex.printStackTrace();
        }
    }

    /**
     * Sets the SmartMeter date and time.
     *
     * @param dateTime as a Date object.
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * set the uptime of the SmartMeter
     *
     * @param upTime
     */
    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    /**
     * Sets the frequency
     *
     * @param frequency
     */
    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    /**
     * phase angle
     *
     * @param phaseAngle
     */
    public void setPhaseAngle(double phaseAngle) {
        this.phaseAngle = phaseAngle;
    }

    /**
     * RMS current
     *
     * @param RMSCurrent
     */
    public void setRMSCurrent(double RMSCurrent) {
        this.RMSCurrent = RMSCurrent;
    }

    /**
     * Sets the current RMS voltage
     *
     * @param RMS voltage
     */
    public void setRMSVoltage(double RMSVoltage) {
        this.RMSVoltage = RMSVoltage;
    }

    /**
     * Sets the current reactive power.
     *
     * @param reactivePower
     */
    public void setReactivePower(double reactivePower) {
        this.reactivePower = reactivePower;
    }

    /**
     * Set the current consumed Watts
     *
     * @param watts
     */
    public void setWatt(double watts) {
        this.watt = watts;
    }
}
