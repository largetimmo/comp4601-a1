package dao.impl;

import com.mongodb.client.model.Filters;
import dao.AbstractDAO;
import dao.CrawlDataDAO;
import dao.modal.CrawlDataEntity;
import org.bson.Document;
import service.crawler.CrawlerManager;

import java.util.ArrayList;
import java.util.List;

public class CrawlDataDAOImpl extends AbstractDAO implements CrawlDataDAO {

    private static final String COLLECTION_NAME = "crawl_data";

    private static final CrawlDataDAO INSTANCE = new CrawlDataDAOImpl();

    public static CrawlDataDAO getInstance(){
        return INSTANCE;
    }

    public CrawlDataDAOImpl() {
        super(COLLECTION_NAME);
    }

    private static Integer docId = 0;


    @Override
    public synchronized void create(CrawlDataEntity document) {
        if (findOneById(document.getId())!= null){
            collection.findOneAndReplace(Filters.eq("id",document.getId()),map(document));
        }else{
            if (document.getId() == null){
                document.setId(docId++);
            }
            document.setDocId(CrawlerManager.getInstance().getDocIDServer().getDocId(document.getUrl()));
            collection.insertOne(map(document));
        }
    }

    @Override
    public CrawlDataEntity findOneById(Integer id) {
        if (id == null){
            return null;
        }
        Document document = (Document) collection.find(Filters.eq("id",id)).iterator().tryNext();
        if (document!= null) {
            return map(document);
        }
        return null;
    }

    @Override
    public List<CrawlDataEntity> findAll() {
        List<CrawlDataEntity>  list = new ArrayList<>();
        for (Object document : collection.find()){
            list.add(map((Document)document));
        }
        return list;
    }

    @Override
    public void update(CrawlDataEntity document) {
        collection.findOneAndReplace(Filters.eq("id",document.getId()),map(document));
    }

    @Override
    public void delete(Integer id) {
        collection.deleteOne(Filters.eq("id",id));
    }

    @Override
    public Document map(CrawlDataEntity entity) {
        Document document = new Document();
        document.put("id",entity.getId());
        document.put("content",entity.getContent());
        document.put("url",entity.getUrl());
        document.put("childUrl",entity.getChildUrl());
        document.put("time",entity.getTimestamp());
        document.put("docId",entity.getDocId());
        return document;
    }

    @Override
    public CrawlDataEntity map(Document document) {
        CrawlDataEntity crawlDataEntity = new CrawlDataEntity();
        crawlDataEntity.setId((Integer) document.get("id"));
        crawlDataEntity.setContent((String) document.get("content"));
        crawlDataEntity.setTimestamp((Long) document.get("time"));
        crawlDataEntity.setUrl((String) document.get("url"));
        crawlDataEntity.setChildUrl((List<String>) document.get("childUrl"));
        crawlDataEntity.setDocId((Integer) document.get("docId"));
        return crawlDataEntity;
    }
}
