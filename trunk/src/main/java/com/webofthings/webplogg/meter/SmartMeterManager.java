/*
 *  This class is part of the Energie Visible WebofThings project.
 *  http://www.webofthings.com/energievisible/
 *  (c) Dominique Guinard (www.guinard.org)
 *  Institute for Pervasive Computing, ETH Zurich
 *  and Cudrefin02.ch.
 */
package com.webofthings.webplogg.meter;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an abstraction of what a SmartMeterManager should look like.
 * A concrete SMM has access to a number of SmartMeters from which
 * it can extract SmartMeterData.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
public abstract class SmartMeterManager {
    /**
     * Concrete smart meters should implement the Singleton pattern, thus
     * we protect the constructor.
     */
    protected SmartMeterManager() {
    }

    /**
     * Users of SmartMeterManagers should call getInstance() since SmartMeterManagers
     * are singletons.
     * @return the unique instance of a concrete SmartMeterManager.
     */
    public static SmartMeterManager getInstance() {
       throw new UnsupportedOperationException("Please re-implement this in a"
               + " concerete Manager.");
    }

    private Map<String, SmartMeter> smartMeters = new HashMap<String, SmartMeter>(20);

    /**
     * This method returns the electricity consumption data
     * of one of the managed SmartMeters.
     * @param id the identifier of the SmartMeter to get the data from.
     * @return the electricity consumption data of the selected SmartMeter
     */
    public abstract ConsumptionData getDataFromMeter(String id);

    /**
     * This method is to be implemented by concrete SmartMeterManagers
     * in order for them to discover the SmartMeters they manage.
     */
    public abstract void discoverSmartMeters();

    /**
     * This method turns off the device connected to a
     * Smart Meter given its unique identifier.
     * @param smartMeterId the unique identifier of the Smart Meter to be
     * turned off power.
     */
    public abstract void turnOffPower(String smartMeterId);

    /**
     * This method turns on the device connected to a
     * Smart Meter given its unique identifier.
     * @param smartMeterId the unique identifier of the Smart Meter to be
     * turned off power.
     */
    public abstract void turnOnPower(String smartMeterId);

    /**
     * This returns all the currently managed SmartMeters.
     * @return a list containing all the SmartMeters.
     */
    public Map<String, SmartMeter> getManagedSmartMeters() {
        return smartMeters;
    }

    /**
     * This adds a SmartMeter to the list of managed
     * SmartMeters
     * @param id the new Smart Meter.
     */
    public void addSmartMeter(SmartMeter smartMeter) {
        smartMeters.put(smartMeter.getId(), smartMeter);
    }

    /**
     * This method gets a SmartMeter from the manager given the meter
     * identifier.
     * @param id the identifier of a SmartMeter (e.g. Bluetooth id: 0021ED0000034A8B).
     * @return the corresponding SmartMeter object.
     */
    public SmartMeter getSmartMeter(String id) {
        return smartMeters.get(id);
    }


}
