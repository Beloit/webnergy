/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webofthings.webplogg.meter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This encapsulates the status of a SmartMeter.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Status {
    private String description;
    private boolean status;

    /**
     * No args constructor for JAXB.
     */
    public Status() {
    }

    /**
     * This creates a new Status object.
     * @param description A description of what this status depicts.
     * @param status The boolean value of the status, is it on?
     */
    public Status(String description, boolean status) {
        this.description = description;
        this.status = status;
    }

    /**
     * This returns a textual description of what the status depicts.
     * @return a free-text description of what the status depicts.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOn() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        if (status) {
            return "on";
        } else {
            return "off";
        }
    }
}
