package controller;

import dao.impl.CrawlDataDAOImpl;
import dao.modal.CrawlDataEntity;
import edu.carleton.comp4601.dao.Document;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/sda")
public class SDAController {

    @GET
    public String whoAmI(){
        return "COMP4601 Searchable Document Archive: Junhao Chen And Mr. Liu";
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Document getDocumentByIdXML(@PathParam("id") String id){
        CrawlDataDAOImpl cdi = new CrawlDataDAOImpl();
        CrawlDataEntity cde = cdi.findOneById(Integer.parseInt(id));
        Map<String,String> hm = new HashMap<String, String>();
        hm.put("id",cde.getId().toString());
        hm.put("score","0.0");
        hm.put("name","name");
        hm.put("content",cde.getContent());
        hm.put("url",cde.getUrl());
        Document doc = new Document(hm);
        System.out.println("------------------------------------------"+doc);
        return doc;
    }
}
