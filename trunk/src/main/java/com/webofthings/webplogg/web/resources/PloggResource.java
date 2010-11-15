/*
 *  This class is part of the Energie Visible WebofThings project.
 *  http://www.webofthings.com/energievisible/
 *  (c) Dominique Guinard (www.guinard.org)
 *  Institute for Pervasive Computing, ETH Zurich
 *  and Cudrefin02.ch.
 */
package com.webofthings.webplogg.web.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author <a href="http://www.guinard.org">Dominique Guinard</a>
 */
@Path("/ploggs")
public class PloggResource {

    @GET
    @Produces("application/json")
    public Test GetPloggName() {
        return new Test("Hello", 2);
    }
}
