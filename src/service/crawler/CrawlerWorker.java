package service.crawler;

import dao.impl.CrawlDataDAOImpl;
import dao.impl.CrawlDataImageDAOImpl;
import dao.modal.CrawlDataEntity;
import dao.modal.CrawlDataImageEntity;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
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
        crawlerData.setChildUrl((page.getParseData()).getOutgoingUrls().stream().map(WebURL::getURL).collect(Collectors.toList()));
        if (tikaManager == null) {
            tikaManager = TikaManager.getInstance();
        }
        if (page.getParseData() instanceof HtmlParseData) {
            //
            Document parsedWeb = Jsoup.parse(((HtmlParseData) page.getParseData()).getHtml());
            Elements imgTags = parsedWeb.select("a");
            if (imgTags.size() > 0) {
                CrawlDataImageEntity imageEntity = new CrawlDataImageEntity();
                imageEntity.setDocId(CrawlerManager.getInstance().getDocIDServer().getDocId(url));
                imageEntity.setImageLink(new HashMap<>());
                for (Element ele : imgTags) {
                    String imageText = ele.attr("alt");
                    String imageLink = ele.attr("src");
                    imageEntity.getImageLink().put(imageText, imageLink);
                }
                CrawlDataImageDAOImpl.getInstance().create(imageEntity);
            }
            Elements paragraphTag = parsedWeb.select("p");
            Elements titleTag = parsedWeb.select("title");
            List<String> contentList = new ArrayList<>();
            paragraphTag.stream().map(Element::text).forEach(contentList::add);
            titleTag.stream().map(Element::text).forEach(contentList::add);
            crawlerData.setContent(contentList);
        }
        Metadata metadata = tikaManager.parse(page);
        Map<String,String> metadataMap = new HashMap<>();
        for (String name : metadata.names()) {
            metadataMap.put(name,metadata.get(name));
        }
        crawlerData.setMetadata(metadataMap);
        CrawlDataDAOImpl.getInstance().create(crawlerData);

    }
}
