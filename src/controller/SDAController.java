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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Path("/sda")
public class SDAController {
    public static CrawlDataDAO cdi;
    private SearchServiceManager smanager;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public SDAController() throws IOException {
        cdi = CrawlDataDAOImpl.getInstance();
        smanager = SearchServiceManager.getInstance();

    }

    @GET
    public String whoAmI() {
        return "COMP4601 Searchable Document Archive: Junhao Chen And Mr. Liu";
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Document getDocumentByIdXML(@PathParam("id") String id) {

        CrawlDataEntity cde = cdi.findByDocID(Integer.parseInt(id));
        return dataEntityToDocument(cde);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_HTML)
    public String getDocumentByIdHTML(@PathParam("id") String id) {

        CrawlDataEntity cde = cdi.findByDocID(Integer.parseInt(id));

        return docToHtml(dataEntityToDocument(cde));
    }


    @DELETE
    @Path("{id}")
    public String deleteDocument(@PathParam("id") String id, @Context HttpServletResponse servletResponse) throws IOException {
        CrawlDataEntity crawlDataEntity = cdi.findByDocID(Integer.valueOf(id));
        if (crawlDataEntity != null) {
            cdi.delete(crawlDataEntity.getId());
            return "OK";
        } else {
            servletResponse.sendError(404);
            return "Not found";
        }
    }

    @GET
    @Path("/delete/{query}")
    public String deleteByQuery(@PathParam("query") String query, @Context HttpServletResponse servletResponse) throws IOException, ParseException {
        Searcher sc = new Searcher();
        TopDocs td = sc.search(query, 1000);

        if (td.totalHits.value == 0) {
            return null;
        }

        sc.print(td.scoreDocs);

        List<Document> documents = sc.getDocuments(td.scoreDocs);
        if (documents.size() == 0) {
            servletResponse.sendError(204);
            return "";
        } else {
            documents.stream().map(Document::getId).map(cdi::findByDocID).map(CrawlDataEntity::getId).forEach(cdi::delete);//Delete
            return "OK";
        }

    }

    @GET
    @Path("list")
    @Produces(MediaType.TEXT_HTML)
    public String getServiceList() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<!doctype html><html><head><title>service list</title></head><body>");
        stringBuilder.append("<ul>");
        SearchServiceManager.getInstance().list().forEach(name -> {
            stringBuilder.append("<li>");
            stringBuilder.append(name);
            stringBuilder.append("</li>");
        });
        stringBuilder.append("</ul></body></html>");
        return stringBuilder.toString();
    }

    @GET
    @Path("documents")
    @Produces(MediaType.APPLICATION_XML)
    public DocumentCollection getDocumentNames() {
        List<CrawlDataEntity> list = cdi.findAll();
        DocumentCollection documentCollection = new DocumentCollection();
        documentCollection.setDocuments(new ArrayList<>());
        list.stream().map(this::dataEntityToDocument).peek(ele -> {
            ele.setContent(null);
            ele.setId(null);
            ele.setScore(null);
            ele.setUrl(null);
        }).forEach(documentCollection.getDocuments()::add);
        return documentCollection;
    }

    @GET
    @Path("documents")
    @Produces(MediaType.TEXT_HTML)
    public String getDocumentNamesHTML() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<!doctype html><html><head><title>doc names</title></head><body>");
        stringBuilder.append("<ul>");
        cdi.findAll().stream().map(CrawlDataEntity::getDocName).forEach(name -> {
            stringBuilder.append("<li>");
            stringBuilder.append(name);
            stringBuilder.append("</li>");
        });
        stringBuilder.append("</ul></body></html>");
        return stringBuilder.toString();
    }

    @GET
    @Path("noboost")
    @Produces(MediaType.TEXT_HTML)
    public String noboost() throws IOException {
        Indexer i = new Indexer();
        List<CrawlDataEntity> cde = cdi.findAll();
        i.indexDocuments(false, cde);
        return "<html> " + "<title>" + "noboost" + "</title>" + "<body><p>" + "Re-indexed" + "</p></body>" + "</html> ";
    }

    @GET
    @Path("query/{terms}")
    @Produces(MediaType.TEXT_HTML)
    public String searchLocalHTML(@PathParam("terms") String terms) throws SearchException, IOException, ClassNotFoundException, ParseException {
        Searcher sc = new Searcher();
        TopDocs td = sc.search(terms, 1000);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<!doctype html><html><head><title>search result</title></head><body>");
        List<Document> documents = sc.getDocuments(td.scoreDocs);
        if (documents.size() == 0) {
            stringBuilder.append("No document found");
        } else {
            stringBuilder.append("<table><tr><th>#</th><th>File</th><th>Score</th><th>Date</th></tr>");
            for (int i = 0; i < documents.size(); i++) {
                CrawlDataEntity crawlDataEntity = cdi.findByDocID(documents.get(i).getId());
                stringBuilder.append("<tr>");
                stringBuilder.append("<td>");
                stringBuilder.append(i + 1);
                stringBuilder.append("</td>");
                stringBuilder.append("<td>");
                stringBuilder.append("<a href=\"");
                stringBuilder.append(documents.get(i).getUrl());
                stringBuilder.append("\">");
                stringBuilder.append(documents.get(i).getName());
                stringBuilder.append("</a>");
                stringBuilder.append("<td>");
                stringBuilder.append(documents.get(i).getScore());
                stringBuilder.append("</td>");
                stringBuilder.append("<td>");
                stringBuilder.append(sdf.format(new Timestamp(crawlDataEntity.getTimestamp())));
                stringBuilder.append("</td>");
                stringBuilder.append("</tr>");
            }
            stringBuilder.append("</table>");
        }
        stringBuilder.append("</body></html>");
        return stringBuilder.toString();
    }

    @GET
    @Path("query/{terms}")
    @Produces(MediaType.APPLICATION_XML)
    public List<Document> searchLocalXML(@PathParam("terms") String terms) throws SearchException, IOException, ClassNotFoundException, ParseException {
        Searcher sc = new Searcher();
        TopDocs td = sc.search(terms, 1000);

        if (td.totalHits.value == 0) {
            return null;
        }

        sc.print(td.scoreDocs);

        return sc.getDocuments(td.scoreDocs);
    }

    @GET
    @Path("search/{terms}")
    @Produces(MediaType.APPLICATION_XML)
    public ArrayList<Document> searchDistributedXML(@PathParam("terms") String terms) throws SearchException, IOException, ClassNotFoundException, ParseException {

        SearchResult sr = smanager.search(terms);
        Searcher sc = new Searcher();

        try {
            sr.await(SDAConstants.TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } finally {
            SearchServiceManager.getInstance().reset();
        }


        TopDocs td = sc.search(terms,1000);
        List<Document> docs = sc.getDocuments(td.scoreDocs);
        sr.addAll(docs);

        return sr.getDocs();
    }

    @GET
    @Path("search/{terms}")
    @Produces(MediaType.TEXT_HTML)
    public String searchDistributedHTML(@PathParam("terms") String terms) throws SearchException, IOException, ClassNotFoundException, ParseException {

        SearchResult sr = smanager.search(terms);
        Searcher sc = new Searcher();


        try {
            sr.await(SDAConstants.TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } finally {
            SearchServiceManager.getInstance().reset();
        }

        TopDocs td = sc.search(terms,1000);
        List<Document> docs = sc.getDocuments(td.scoreDocs);
        sr.addAll(docs);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<!doctype html><html><head><title>search result</title></head><body>");
        List<Document> documents = sr.getDocs().stream().sorted(Comparator.comparingDouble(Document::getScore)).collect(Collectors.toList());
        if (documents.size() == 0) {
            stringBuilder.append("No document found");
        } else {
            stringBuilder.append("<table><tr><th>#</th><th>File</th><th>Score</th><th>Date</th></tr>");
            for (int i = 0; i < documents.size(); i++) {
                CrawlDataEntity crawlDataEntity = cdi.findByDocID(documents.get(i).getId());
                stringBuilder.append("<tr>");
                stringBuilder.append("<td>");
                stringBuilder.append(i + 1);
                stringBuilder.append("</td>");
                stringBuilder.append("<td>");
                stringBuilder.append("<a href=\"");
                stringBuilder.append(documents.get(i).getUrl());
                stringBuilder.append("\">");
                stringBuilder.append(documents.get(i).getName());
                stringBuilder.append("</a>");
                stringBuilder.append("<td>");
                stringBuilder.append(documents.get(i).getScore());
                stringBuilder.append("</td>");
                stringBuilder.append("<td>");
                if(crawlDataEntity != null && crawlDataEntity.getUrl().equals(documents.get(i).getUrl())){
                    stringBuilder.append(sdf.format(new Timestamp(crawlDataEntity.getTimestamp())));
                }
                stringBuilder.append("</td>");
                stringBuilder.append("</tr>");
            }
            stringBuilder.append("</table>");
        }
        stringBuilder.append("</body></html>");
        return stringBuilder.toString();
    }

    private Document dataEntityToDocument(CrawlDataEntity cde) {
        Document doc = new Document(cde.getId());
        if (cde.getContent() != null) {
            doc.setContent(cde.getContent().toString());
        }
        doc.setName(cde.getDocName());

        doc.setScore(cde.getScore().floatValue());
        doc.setUrl(cde.getUrl());
        return doc;
    }

    private String docToHtml(Document doc) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>\n");
        html.append("<html lang='en'>\n");

        html.append("<head>\n");
        html.append("<meta charset='utf-8'>\n");
        html.append("<title>" + doc.getName() + "</title>\n");
        html.append("</head>\n\n");

        html.append("<body>\n");
        html.append("<ul>\n");
        html.append("<p>" + doc.getContent() + "</p>\n");
        html.append("<li> Url:" + doc.getUrl() + "</li>\n");
        html.append("<li> Score:" + doc.getScore() + "</li>\n");
        html.append("<li> Id:" + doc.getId() + "</li>\n");
        html.append("</ul>\n");
        html.append("</body>\n\n");

        html.append("</html>");

        return html.toString();
    }
}
