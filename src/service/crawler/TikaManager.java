package service.crawler;

import dao.modal.CrawlDataEntity;
import edu.uci.ics.crawler4j.crawler.Page;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class TikaManager {

    private static final TikaManager INSTANCE = new TikaManager();

    public static  TikaManager getInstance() {
        return INSTANCE;
    }

    public TikaManager() {

    }

    public CrawlDataEntity parse(Page page, CrawlDataEntity crawlDataEntity){
        Parser parser = new AutoDetectParser();

        Metadata metadata = new Metadata();
        BodyContentHandler bodyContentHandler = new BodyContentHandler(-1);
        try {
            parser.parse(new ByteArrayInputStream(page.getContentData()),bodyContentHandler,metadata,new ParseContext());
            Map<String,String> metadataMap = new HashMap<>();
            for (String name : metadata.names()) {
                metadataMap.put(name,metadata.get(name));
            }
            crawlDataEntity.setMetadata(metadataMap);
            String content = bodyContentHandler.toString();
            content = content.replace("\t\r","");
            String[] contentArray = content.split("\n");
            List<String> contentList = new LinkedList<>();
            for (String c: contentArray){
                if (!c.isEmpty()){
                    contentList.add(c);
                }
            }
            crawlDataEntity.setContent(contentList);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }
        return crawlDataEntity;

        //todo
    }


}
