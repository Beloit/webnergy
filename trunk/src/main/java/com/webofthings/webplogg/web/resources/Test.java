/*
 *  This class is part of the Energie Visible WebofThings project.
 *  http://www.webofthings.com/energievisible/
 *  (c) Dominique Guinard (www.guinard.org)
 *  Institute for Pervasive Computing, ETH Zurich
 *  and Cudrefin02.ch.
 */
package com.webofthings.webplogg.web.resources;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
@XmlRootElement
public class Test {

    public Test() {
    }

    public Test(String one, int two) {
        this.one = one;
        this.two = two;
    }

    @XmlElement(name="one")
    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    @XmlElement(name="two")
    public int getTwo() {
        return two;
    }

    public void setTwo(int two) {
        this.two = two;
    }
    private String one;
    private int two;
}
