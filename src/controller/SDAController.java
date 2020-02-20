package controller;

import dao.Document;

import javax.print.Doc;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;

@Path("/sda")
public class SDAController {

    @GET
    public String whoAmI(){
        return "COMP4601 Searchable Document Archive: Junhao Chen And Mr. Liu";
    }

    @GET
    @Path("id")
    @Produces(MediaType.APPLICATION_XML)
    public Document getDocumentByIdXML(@PathParam("id") String id){

    }
}
