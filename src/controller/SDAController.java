package controller;

import dao.CrawlDataDAO;
import dao.impl.CrawlDataDAOImpl;
import dao.indexer.Indexer;
import dao.indexer.Searcher;
import dao.modal.CrawlDataEntity;
import edu.carleton.comp4601.dao.Document;
import edu.carleton.comp4601.dao.DocumentCollection;
import edu.carleton.comp4601.utility.SDAConstants;
import edu.carleton.comp4601.utility.SearchException;
import edu.carleton.comp4601.utility.SearchResult;
import edu.carleton.comp4601.utility.SearchServiceManager;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("/sda")
public class SDAController {
    public static CrawlDataDAO cdi;
    private SearchServiceManager smanager;

    public SDAController() throws IOException {
        cdi = CrawlDataDAOImpl.getInstance();
        smanager = SearchServiceManager.getInstance();

    }

    @GET
    public String whoAmI(){
        return "COMP4601 Searchable Document Archive: Junhao Chen And Mr. Liu";
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Document getDocumentByIdXML(@PathParam("id") String id){

        CrawlDataEntity cde = cdi.findByDocID(Integer.parseInt(id));
        return dataEntityToDocument(cde);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public String getDocumentByIdHTML(@PathParam("id") String id){

        CrawlDataEntity cde = cdi.findByDocID(Integer.parseInt(id));

        return docToHtml(dataEntityToDocument(cde));
    }

    @DELETE
    @Path("{id}")
    public String deleteDocument(@PathParam("id") String id,@Context HttpServletResponse servletResponse) throws IOException {
        CrawlDataEntity crawlDataEntity  = cdi.findByDocID(Integer.valueOf(id));
        if (crawlDataEntity != null){
            cdi.delete(crawlDataEntity.getId());
            return "OK";
        }else{
            servletResponse.sendError(404);
            return "Not found";
        }
    }

    @GET
    @Path("documents")
    @Produces(MediaType.APPLICATION_XML)
    public DocumentCollection getDocumentNames(){
        List<CrawlDataEntity> list = cdi.findAll();
        DocumentCollection documentCollection = new DocumentCollection();
        list.stream().map(this::dataEntityToDocument).peek(ele->{
            ele.setContent(null);
            ele.setId(null);
            ele.setScore(null);
            ele.setUrl(null);
        }).forEach(documentCollection.getDocuments()::add);
        return documentCollection;
    }

    @GET
    @Path("list")
    @Produces(MediaType.TEXT_HTML)
    public String getServiceList(){
       ArrayList<String> list = smanager.list();
       return "<html> " + "<title>" + "Search services list" + "</title>" + "<body><p>" + list + "</p></body>" + "</html> ";
    }

    @GET
    @Path("noboost")
    @Produces(MediaType.TEXT_HTML)
    public String noboost() throws IOException {
        Indexer i = new Indexer();
        List<CrawlDataEntity> cde = cdi.findAll();
        i.indexDocuments(false,cde);
        return "<html> " + "<title>" + "noboost" + "</title>" + "<body><p>" + "Re-indexed" + "</p></body>" + "</html> ";
    }

    @GET
    @Path("query/{terms}")
    @Produces(MediaType.APPLICATION_XML)
    public List<Document> searchLocalXML(@PathParam("terms") String terms) throws SearchException, IOException, ClassNotFoundException, ParseException {
        Searcher sc = new Searcher();
        TopDocs td = sc.search(terms,1000);

        if(td.totalHits.value == 0) {
            return null;
        }

        sc.print(td.scoreDocs);

        return sc.getDocuments(td.scoreDocs);
    }
    @GET
    @Path("search/{terms}")
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<Document> searchDistributedXML(@PathParam("terms") String terms) throws SearchException, IOException, ClassNotFoundException {

        SearchResult sr = smanager.search(terms);

        try {
            sr.await(SDAConstants.TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }finally {
            SearchServiceManager.getInstance().reset();
        }

        ArrayList<Document> documents = sr.getDocs();
        if(documents == null || documents.isEmpty()) {
            return new ArrayList<Document>();
        }

        return sr.getDocs();
    }

    private Document dataEntityToDocument(CrawlDataEntity cde){
        Document doc = new Document(cde.getId());
        if (cde.getContent() != null){
            doc.setContent(cde.getContent().toString());
        }
        doc.setContent(content);
        doc.setName(cde.getDocName());
        doc.setScore(cde.getScore());
        doc.setUrl(cde.getUrl());
        return doc;
    }

    private String docToHtml(Document doc){
        StringBuilder html = new StringBuilder();
        html.append( "<!doctype html>\n" );
        html.append( "<html lang='en'>\n" );

        html.append( "<head>\n" );
        html.append( "<meta charset='utf-8'>\n" );
        html.append( "<title>"+ doc.getName() +"</title>\n" );
        html.append( "</head>\n\n" );

        html.append( "<body>\n" );
        html.append( "<ul>\n" );
        html.append( "<p>" + doc.getContent() + "</p>\n" );
        html.append( "<li> Url:" + doc.getUrl() + "</li>\n" );
        html.append( "<li> Score:" + doc.getScore() + "</li>\n" );
        html.append( "<li> Id:" + doc.getId() + "</li>\n" );
        html.append( "</ul>\n" );
        html.append( "</body>\n\n" );

        html.append( "</html>" );

        return html.toString();
    }
}
