package service.crawler;

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

public class TikaManager {

    private static final TikaManager INSTANCE = new TikaManager();

    public static  TikaManager getInstance() {
        return INSTANCE;
    }

    private Tika tika = new Tika();

    public TikaManager() {

    }

    public Metadata parse(Page page){
        Parser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        BodyContentHandler bodyContentHandler = new BodyContentHandler();
        try {
            parser.parse(new ByteArrayInputStream(page.getContentData()),bodyContentHandler,metadata,new ParseContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }
        return metadata;

        //todo
    }


}
