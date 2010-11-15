package com.webofthings.webplogg.web;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import com.webofthings.webplogg.meter.SmartMeterManager;
import com.webofthings.webplogg.meter.config.Configurator;
import com.webofthings.webplogg.meter.plogg.PloggManager;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

/**
 * This the main class of the application. It is in charge of starting
 * the Webserver (i.e. Grizzly) and binding the resources.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
public class App {

    private static int getPort(int defaultPort) {
        String port = System.getenv("JERSEY_HTTP_PORT");
        if (null != port) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException e) {
            }
        }
        return defaultPort;
    }

    private static URI getBaseURI() {
        int port = Integer.parseInt(Configurator.getInstance().
                getProperty("WEB_SERVER_PORT"));
        String restApiPrefix = Configurator.getInstance().getProperty("REST_API_PREFIX");
        String restApiVersion = Configurator.getInstance().getProperty("REST_API_VERSION");
        return UriBuilder.fromUri("http://" + "localhost" + "/" + restApiPrefix + "/" + "ver" + "/" + restApiVersion + "/")
                .port(getPort(port)).build();
    }
    public static final URI BASE_URI = getBaseURI();

    protected static SelectorThread startServer() throws IOException {
        final Map<String, String> initParams = new HashMap<String, String>();

        /* give Grizzly the path to our resources */
        initParams.put("com.sun.jersey.config.property.packages",
                "com.webofthings.webplogg.web.resources");

        System.out.println("Starting grizzly...");
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(BASE_URI, initParams);
        return threadSelector;
    }

    public static void main(String[] args) throws IOException {
        /* first we ask all the SmartMeterManager to discover their meters */

        PloggManager smartMeterMgr = (PloggManager) PloggManager.getInstance();
        smartMeterMgr.discoverSmartMeters();
        //smartMeterMgr.setupPAN();

        Runnable runnableMgr = smartMeterMgr;
        Thread mgrThread = new Thread(runnableMgr);
        mgrThread.start();


        /* and then we start Grizzly */
        SelectorThread threadSelector = startServer();
        System.out.println(String.format("Webnergy app started with WADL available at "
                + "%sapplication.wadl\nTry out %ssmartmeters\nHit enter to stop it...",
                BASE_URI, BASE_URI));
        System.in.read();
        threadSelector.stopEndpoint();
    }
}
