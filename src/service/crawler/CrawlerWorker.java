package service.crawler;

import dao.impl.CrawlDataDAOImpl;
import dao.modal.CrawlDataEntity;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CrawlerWorker extends WebCrawler {

    private final static Pattern FILTER = Pattern.compile(".*(\\.(html|pdf|tiff|jpeg|gif|png|doc|docx|xls|xlsx|ppt|pptx))$");

    private Set<String> baseUrlSet;

    private TikaManager tikaManager;

    public CrawlerWorker(String... base_url) {
        baseUrlSet = new HashSet<>();
        baseUrlSet.addAll(Arrays.asList(base_url));
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return baseUrlSet.contains(url.getDomain().toLowerCase()) &&
                FILTER.matcher(url.getURL().toLowerCase()).matches();
    }

    @Override
    public void visit(Page page) {
        CrawlDataEntity crawlerData = new CrawlDataEntity();
        String url = page.getWebURL().getURL();
        crawlerData.setTimestamp(System.currentTimeMillis());
        crawlerData.setUrl(url);

        if (tikaManager == null){
            tikaManager = TikaManager.getInstance();
        }
        if (page.getContentType().toLowerCase().equals("text/html")){
            crawlerData.setContent(((HtmlParseData)page.getParseData()).getHtml());
            crawlerData.setChildUrl(((HtmlParseData)page.getParseData()).getOutgoingUrls().stream().map(WebURL::getURL).collect(Collectors.toList()));
            //
            Document parsedWeb = Jsoup.parse(((HtmlParseData)page.getParseData()).getHtml());

            CrawlDataDAOImpl.getInstance().create(crawlerData);
        }else if (url.endsWith("pdf")){
            tikaManager.parsePdf(page);
        }else if (url.endsWith("doc") || url.endsWith("docx")){
            tikaManager.parseExcel(page);
        }else if (url.endsWith("xls") || url.endsWith("xlsx")){
            tikaManager.parseExcel(page);
        }else if (url.endsWith("ppt") || url.endsWith("pptx")){
            tikaManager.parsePowerPoint(page);
        }else{
            tikaManager.parseImage(page);
        }

    }
}
