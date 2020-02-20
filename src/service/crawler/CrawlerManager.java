package service.crawler;

import dao.impl.CrawlDataDAOImpl;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlerManager {

    private static final Integer workers = 4;
    private static final String BASE_URL = "https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/handouts/";
    private static final String BASE_URL2 = "https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/resources/";
    private static final String BASE_URL3 = "https://www.ics.uci.edu/~lopes/";

    private static final CrawlerManager INSTANCE = new CrawlerManager();

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
            CrawlController finalController = controller;
            new Thread(()->{
                CrawlController.WebCrawlerFactory<CrawlerWorker> factory = () -> new CrawlerWorker("dyndns.org:8443","ics].uci.edu");
                finalController.start(factory, workers);
                GraphManager.getInstance().generateGraph(CrawlDataDAOImpl.getInstance().findAll());
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

