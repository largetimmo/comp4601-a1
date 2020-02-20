package service.crawler;

import edu.uci.ics.crawler4j.crawler.Page;

public class TikaManager {

    private static final TikaManager INSTANCE = new TikaManager();

    public static  TikaManager getInstance() {
        return INSTANCE;
    }

    public TikaManager() {

    }

    public void parsePdf(Page page){
        //todo
    }
    public void parseImage(Page page){
        //todo
    }
    public void parseWord(Page page){
        //todo
    }
    public void parseExcel(Page page){
        //todo
    }
    public void parsePowerPoint(Page page){
        //todo
    }

}
