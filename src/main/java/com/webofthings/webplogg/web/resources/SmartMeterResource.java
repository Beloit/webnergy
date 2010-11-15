/*
 *  This class is part of the Energie Visible WebofThings project.
 *  http://www.webofthings.com/energievisible/
 *  (c) Dominique Guinard (www.guinard.org)
 *  Institute for Pervasive Computing, ETH Zurich
 *  and Cudrefin02.ch.
 */
package com.webofthings.webplogg.web.resources;

import com.webofthings.webplogg.meter.SmartMeter;
import com.webofthings.webplogg.meter.ConsumptionData;
import com.webofthings.webplogg.meter.SmartMeterManager;
import com.webofthings.webplogg.meter.Status;
import com.webofthings.webplogg.meter.plogg.PloggManager;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 * This is the Smart Meter resource which is the first application related
 * resource clients will get to.
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
@Path("/smartmeters")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML})
public class SmartMeterResource {

    private static SmartMeterManager ploggMgr = PloggManager.getInstance();

    public SmartMeterResource() {
    }

    @GET
    public List<SmartMeter> getSmartMeters() {
        return new ArrayList(ploggMgr.getManagedSmartMeters().values());
    }

    /**
     * This produces an HTML representation of all the SmartMeters. I.e.
     * a list with links to the each SmartMeter.
     * @return The HTML representation of the list of SmartMeters.
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getSmartMetersHTML() {
        StringBuilder htmlOut = new StringBuilder("<html><body>");
        for (SmartMeter currentSmartMeter : ploggMgr.getManagedSmartMeters().values()) {

            /* Build URLs to child-resources (SmartMeter) */
            UriBuilder builder = UriBuilder.fromPath("{id}/");
            UriBuilder clone = builder.clone();
            URI uri = clone.build(currentSmartMeter.getId());

            htmlOut.append("<p>Name: " + currentSmartMeter.getName() + "</br>");
            htmlOut.append("ID: " + "<a href=\"");
            htmlOut.append(uri.toString());
            htmlOut.append("\">" + currentSmartMeter.getId() + "</a>");
            htmlOut.append("</p>");
        }

        htmlOut.append("</body></hmtl>");
        return htmlOut.toString();
    }

    @Path("/{smartMeterId}")
    @GET
    public ConsumptionData getMeterData(@PathParam("smartMeterId") String smartMeterId) {
        return ploggMgr.getDataFromMeter(smartMeterId);
    }

    /**
     * This produces an HTML representation of a given SmartMeter.
     * @return The HTML representation of the list of SmartMeters.
     */
    @Path("/{smartMeterId}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getMeterDataHTML(@PathParam("smartMeterId") String smartMeterId) {
        StringBuilder htmlOut = new StringBuilder("<html><body>");
        ConsumptionData cons = ploggMgr.getDataFromMeter(smartMeterId);

        /* Build URLs to child-resources (SmartMeter) */
        UriBuilder builder = UriBuilder.fromPath("../");
        UriBuilder clone = builder.clone();
        URI uriBack = clone.build();

        builder = UriBuilder.fromPath("status/");
        UriBuilder clone2 = builder.clone();
        URI uriStatus = clone2.build();

        htmlOut.append("<p>Watts: ").
                append(cons.getWatt()).
                append("</p>");
        htmlOut.append("<p><a href=\"");
        htmlOut.append(uriStatus.toString());
        htmlOut.append("\">Check/change the status of this Smart Meters...</a></p>");
        htmlOut.append("<p><a href=\"");
        htmlOut.append(uriBack.toString());
        htmlOut.append("\">Back to the list of available Smart Meters...</a></p>");


        htmlOut.append("</body></hmtl>");
        return htmlOut.toString();
    }

    @Path("/{smartMeterId}/status")
    @GET
    public Status getMeterStatus(@PathParam("smartMeterId") String smartMeterId) {
        return ploggMgr.getSmartMeter(smartMeterId).getStatus();
    }

    @Path("/{smartMeterId}/status")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getMeterStatusHTML(@PathParam("smartMeterId") String smartMeterId) {
        StringBuilder htmlOut = new StringBuilder("<html><body>");
        ConsumptionData cons = ploggMgr.getDataFromMeter(smartMeterId);

        /* Build URLs to child-resources (SmartMeter) */
        UriBuilder builder = UriBuilder.fromPath("../");
        UriBuilder clone = builder.clone();
        URI uri = clone.build(smartMeterId);

        htmlOut.append("<p>").append(smartMeterId).append(" is currently ").append(
                ploggMgr.getSmartMeter(smartMeterId).getStatus()).append("!</p>");
        htmlOut.append("<p><form action=\"\" method=\"post\">");
        if (ploggMgr.getSmartMeter(smartMeterId).getStatus().isOn()) {
            htmlOut.append("<input type=\"radio\" name=\"status\" value=\"on\" checked />On<br/>");
            htmlOut.append("<input type=\"radio\" name=\"status\" value=\"off\" />Off<br/>");
        } else {
            htmlOut.append("<input type=\"radio\" name=\"status\" value=\"on\" />On<br/>");
            htmlOut.append("<input type=\"radio\" name=\"status\" value=\"off\" checked />Off<br/>");
        }
        htmlOut.append("<input type=\"submit\" value=\"Submit\"/></p>");
        htmlOut.append("<p><a href=\"");
        htmlOut.append(uri.toString());
        htmlOut.append("\">Back to the Smart Meter...</a></p>");


        htmlOut.append("</body></hmtl>");
        return htmlOut.toString();
    }

    /**
     * This resource is used to change the status of a Smart Meter.
     * @param smartMeterId The unique identifier of the Smart Meter to update.
     * @param status A plain text entity body containing either "on" or "off".
     * @return a Response message.
     */
    @Path("/{smartMeterId}/status")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response setMeterStatus(@PathParam("smartMeterId") String smartMeterId, String status) {
        doSetMeterStatus(status, smartMeterId);
        return Response.ok(status).build();
    }

    /**
     * This resource is used to change the status of a Smart Meter.
     * @param smartMeterId The unique identifier of the Smart Meter to update.
     * @param message A form field named status containing the either "on" or "off".
     * @return a Response message.
     */
    @Path("/{smartMeterId}/status")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response setMeterStatusPost(@PathParam("smartMeterId") String smartMeterId, @FormParam("status") String status) {
        doSetMeterStatus(status, smartMeterId);
        return Response.ok("<html><body><p>Ok, turned " + status + "</p><p>"
                + "<a href=\"../\">Back to the Smart Meter...</a></p></body></html>").build();
    }

    private void doSetMeterStatus(String status, String smartMeterId) {
        if (status.toLowerCase().equals("off")) {
            ploggMgr.turnOffPower(smartMeterId);
        } else {
            ploggMgr.turnOnPower(smartMeterId);

        }
    }
    //    @GET
//    public PloggMeterData getPloggs() {
//        PloggMeterData meteredData = new PloggMeterData("~~Live Meter results are:~~~~Time entry               = 2010 MAY 30 19:08:01 ~~Watts (-Gen +Con)        = 2.60 W ~~Cumulative Watts (Gen)   = 0.000 kWh ~~Cumulative Watts (Con)   = 53.903 kWh ~~Frequency                = 50.0 Hz ~~RMS Voltage              = 230.5 V ~~RMS Current              = 0.021 A ~~Plogg on time            = 11 days 12:19:43 ~~Reactive Power (-G/+C)   = -4.26 VAR ~~Acc Reactive Pwr (Gen)   = 57.171 KVARh ~~Acc Reactive Pwr (Con)   = 0.000 KVARh ~~Phase Angle (V/I)        = 297 Degrees ~~Equipment on time        = 11 days 12:19:44 ~~~~>");
//        return meteredData;
//    }

    /* testing method, returns what a Plogg would return */
//    @Path("/{smartMeterId}")
//    @GET
//    public ConsumptionData getMeterData(@PathParam("smartMeterId") String smartMeterId) {
//        PloggMeterData meteredData = new PloggMeterData("~~Live Meter results are:~~~~Time entry               = 2010 MAY 30 19:08:01 ~~Watts (-Gen +Con)        = 2.60 W ~~Cumulative Watts (Gen)   = 0.000 kWh ~~Cumulative Watts (Con)   = 53.903 kWh ~~Frequency                = 50.0 Hz ~~RMS Voltage              = 230.5 V ~~RMS Current              = 0.021 A ~~Plogg on time            = 11 days 12:19:43 ~~Reactive Power (-G/+C)   = -4.26 VAR ~~Acc Reactive Pwr (Gen)   = 57.171 KVARh ~~Acc Reactive Pwr (Con)   = 0.000 KVARh ~~Phase Angle (V/I)        = 297 Degrees ~~Equipment on time        = 11 days 12:19:44 ~~~~>");
//        return meteredData;
//    }
}
