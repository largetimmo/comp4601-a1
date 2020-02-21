package service;

import edu.carleton.comp4601.utility.SearchServiceManager;

import java.io.IOException;

public class StartUpManager {


    public StartUpManager() {
        startCrawler();
        startSearchServiceManager();
    }

    private void startCrawler() {
       // CrawlerManager.getInstance();
    }

    private void startSearchServiceManager() {
        try {
            SearchServiceManager.getInstance().start();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
