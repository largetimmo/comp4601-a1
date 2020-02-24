package service.crawler;

import dao.CrawlDataDAO;
import dao.CrawlDataImageDAO;
import dao.impl.CrawlDataDAOImpl;
import dao.impl.CrawlDataImageDAOImpl;
import dao.indexer.PageRank;
import dao.modal.CrawlDataEntity;
import dao.modal.CrawlDataImageEntity;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CrawlerManager {

    private static final Integer workers = 20;
    private static final String BASE_URL = "https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/handouts/";
    private static final String BASE_URL2 = "https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/resources/";
    private static final String BASE_URL3 = "https://www.ics.uci.edu/~lopes/";

    private static final CrawlerManager INSTANCE = new CrawlerManager();

    private DocIDServer docIDServer = null;
    private CrawlDataDAO crawlDataDAO;
    private CrawlDataImageDAO crawlDataImageDAO;


    public static CrawlerManager getInstance() {
        return INSTANCE;
    }

    public CrawlerManager() {
        crawlDataDAO = CrawlDataDAOImpl.getInstance();
        crawlDataImageDAO = CrawlDataImageDAOImpl.getInstance();
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
//                finalController.start(factory, workers);
                //Add image data
                List<CrawlDataImageEntity> list = crawlDataImageDAO.findAll();
                list.stream().forEach(ele->{
                    for (Map.Entry<String,String> entry : ele.getImageLink().entrySet()){
                        Integer docId = docIDServer.getDocId(entry.getValue());
                        if (docId != -1){
                            CrawlDataEntity entity = crawlDataDAO.findByDocID(docId);
                            entity.setContent(Arrays.asList(entry.getKey()));
                            crawlDataDAO.update(entity);
                        }
                    }
                });
                //Calculate page rank
                new PageRank().calculatePageRank();
                System.out.println("Calculate finish");
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public DocIDServer getDocIDServer() {
        return docIDServer;
    }
}

