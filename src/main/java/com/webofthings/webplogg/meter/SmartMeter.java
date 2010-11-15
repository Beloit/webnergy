/*
 *  This class is part of the Energie Visible WebofThings project.
 *  http://www.webofthings.com/energievisible/
 *  (c) Dominique Guinard (www.guinard.org)
 *  Institute for Pervasive Computing, ETH Zurich
 *  and Cudrefin02.ch.
 */
package com.webofthings.webplogg.meter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This represents a generic SmartMeter.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class SmartMeter {
    private ConsumptionData latestConsumption;
    private String id;
    private String type;
    private String name;
    private Status connectedDevicePowered;


    /**
     * This creates a new SmartMeter object.
     * @param id the unique identifier of this SmartMeter
     * @param type its type (free text)
     * @param name its name (free text)
     */
    public SmartMeter(String id, String type, String name, boolean isConnectedDevicePowered) {
        this.id = id;
        this.type = type;
        this.name = name;
        connectedDevicePowered = new Status("This shows whether the smart meter currently lets the power"
                + " reach the connected device.", isConnectedDevicePowered);
    }

    /**
     * This creates a new SmartMeter object using minimal information,
     * i.e. the smart meter unique identifier.
     * @param id the unique identifier of this SmartMeter
     */
    public SmartMeter(String id) {
        this.id = id;
    }

    /**
     * Default constructor (for JAXB)
     */
    public SmartMeter(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    /**
     * This method returns whether the device connected to the Smart Meter
     * is currently powered (i.e. is the Smart Meter letting the power through 
     * or not).
     * @return true if the connected device is currently on, false if it is
     * off.
     */
    @XmlTransient
    public Status getStatus() {
        return connectedDevicePowered;
    }


    /**
     * This method sets the current power through status.
     * @param status should be set to true if the device connected to the Smart
     * Meter is currently powered. False otherwise.
     */
    public void setStatus(boolean status) {
        this.connectedDevicePowered.setStatus(status);
    }

    /**
     * This returns the latest consumption data known for this SmartMeter.
     * @return the latest consumption.
     */
    public ConsumptionData getLatestConsumption() {
        return latestConsumption;
    }

    /**
     * This updates the latest consumption for this SmartMeter.
     * @param latestConsumption the new consumption data.
     */
    @XmlTransient
    public void setLatestConsumption(ConsumptionData latestConsumption) {
        this.latestConsumption = latestConsumption;
    }

}
