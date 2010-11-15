/*
 *  This class is part of the Energie Visible WebofThings project.
 *  http://www.webofthings.com/energievisible/
 *  (c) Dominique Guinard (www.guinard.org)
 *  Institute for Pervasive Computing, ETH Zurich
 *  and Cudrefin02.ch.
 */

package com.webofthings.webplogg.meter;

/**
 * Concrete implementations of this class are responsible
 * for syncing with the SmartMeters managed by a SmartMeterManager.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
public abstract class SmartMeterSyncThread implements Runnable{
    public abstract void run();

}
