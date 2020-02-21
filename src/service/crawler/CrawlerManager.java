package service.crawler;

import dao.CrawlGraphDAO;
import dao.impl.CrawlDataDAOImpl;
import dao.impl.CrawlGraphDAOImpl;
import dao.modal.CrawlGraphEntity;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.stream.Collectors;

public class CrawlerManager {

    private static final Integer workers = 20;
    private static final String BASE_URL = "https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/handouts/";
    private static final String BASE_URL2 = "https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/resources/";
    private static final String BASE_URL3 = "https://www.ics.uci.edu/~lopes/";

    private static final CrawlerManager INSTANCE = new CrawlerManager();

    private DocIDServer docIDServer = null;

    public static CrawlerManager getInstance() {
        return INSTANCE;
    }

    public CrawlerManager() {
        CrawlConfig config = new CrawlConfig();
        config.setIncludeHttpsPages(true);
        config.setCrawlStorageFolder("here");
        config.setMaxDownloadSize(1024000000);
        config.setIncludeBinaryContentInCrawling(true);
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = null;
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer);
            controller.addSeed(BASE_URL);
            controller.addSeed(BASE_URL2);
            controller.addSeed(BASE_URL3);
            docIDServer = controller.getDocIdServer();
            CrawlController finalController = controller;
            new Thread(()->{
                CrawlController.WebCrawlerFactory<CrawlerWorker> factory = () -> new CrawlerWorker("dyndns.org:8443","uci.edu","sikaman.dyndns.org");
                finalController.start(factory, workers);
                Graph<String, DefaultEdge> graph = GraphManager.getInstance().generateGraph(CrawlDataDAOImpl.getInstance().findAll());
                for (String v : graph.vertexSet()){
                    CrawlGraphEntity crawlGraphEntity = new CrawlGraphEntity();
                    crawlGraphEntity.setId(v);
                    crawlGraphEntity.setEdges(graph.outgoingEdgesOf(v).stream().map(graph::getEdgeTarget).collect(Collectors.toList()));
                    CrawlGraphDAOImpl.getInstance().addDocument(crawlGraphEntity);
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public DocIDServer getDocIDServer() {
        return docIDServer;
    }
}

