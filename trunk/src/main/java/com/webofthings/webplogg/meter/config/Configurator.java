package com.webofthings.webplogg.meter.config;

/*
 *  This class is part of the Energie Visible WebofThings project.
 *  http://www.webofthings.com/energievisible/
 *  (c) Dominique Guinard (www.guinard.org)
 *  Institute for Pervasive Computing, ETH Zurich
 *  and Cudrefin02.ch.
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the configurator Singleton which loads all the properties
 * of the framework.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
public class Configurator extends Properties {

    private static Configurator UNIQUE_CONFIGURATOR = null;

    /**
     * The Configurator is a Singleton, thus the private constructor.
     */
    private Configurator() {
        String propsFileName = "config.properties";
        try {
            InputStream propsStream = this.getClass().getClassLoader().getResourceAsStream(propsFileName);
            URL loc = this.getClass().getClassLoader().getResource(propsFileName);
            System.out.println(loc);
            super.load(propsStream);
        } catch (IOException ex) {
            Logger.getLogger(Configurator.class.getName()).log(Level.SEVERE, "Unable to load the property file: " + propsFileName, ex);
        }
    }

    public static Configurator getInstance() {
        if (UNIQUE_CONFIGURATOR == null) {
            return UNIQUE_CONFIGURATOR = new Configurator();
        } else {
            return UNIQUE_CONFIGURATOR;
        }
    }
}
