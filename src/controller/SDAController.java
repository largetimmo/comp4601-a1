package controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/sda")
public class SDAController {

    @GET
    public String whoAmI(){
        return "COMP4601 Searchable Document Archive: Junhao Chen And Mr. Liu";
    }
}
