package dao.impl;

import com.mongodb.client.model.Filters;
import dao.AbstractDAO;
import dao.CrawlGraphDAO;
import dao.modal.CrawlGraphEntity;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class CrawlGraphDAOImpl extends AbstractDAO implements CrawlGraphDAO {

    private static final String COLLECTION_NAME = "crawl_graph";

    private static final CrawlGraphDAO INSTANCE = new CrawlGraphDAOImpl();

    public static CrawlGraphDAO getInstance(){
        return INSTANCE;
    }

    public CrawlGraphDAOImpl() {
        super(COLLECTION_NAME);
    }


    @Override
    public synchronized void addDocument(CrawlGraphEntity crawlGraphEntity) {
        long count = collection.countDocuments(Filters.eq("id",crawlGraphEntity.getId()));
        Document document = map(crawlGraphEntity);
        if (count > 0){
            collection.findOneAndReplace(Filters.eq("id",crawlGraphEntity.getId()),document);
        }else{
            collection.insertOne(document);
        }
    }

    @Override
    public List<CrawlGraphEntity> findAll() {
        List<CrawlGraphEntity> result = new ArrayList<>();
        for (Object document : collection.find()){
            result.add(map((Document) document));
        }
        return result;
    }

    @Override
    public Document map(CrawlGraphEntity entity) {
        Document document = new Document();
        document.put("id",entity.getId());
        document.put("edges",entity.getEdges());
        return document;
    }

    @Override
    public CrawlGraphEntity map(Document document) {
        CrawlGraphEntity crawlGraphEntity = new CrawlGraphEntity();
        crawlGraphEntity.setId((String) document.get("id"));
        crawlGraphEntity.setEdges((List<String>) document.get("edges"));
        return crawlGraphEntity;
    }
}
