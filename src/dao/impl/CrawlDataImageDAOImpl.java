package dao.impl;

import com.mongodb.client.model.Filters;
import dao.AbstractDAO;
import dao.CrawlDataImageDAO;
import dao.modal.CrawlDataImageEntity;
import org.bson.Document;

import java.util.Map;

public class CrawlDataImageDAOImpl extends AbstractDAO implements CrawlDataImageDAO {

    private static final CrawlDataImageDAO INSTANCE = new CrawlDataImageDAOImpl();
    public static CrawlDataImageDAO getInstance(){
        return INSTANCE;
    }

    private static final String COLLECTION_NAME = "crawl_data_img";

    public CrawlDataImageDAOImpl() {
        super(COLLECTION_NAME);
    }

    @Override
    public void create(CrawlDataImageEntity entity) {
        long count = collection.countDocuments(Filters.eq("docId",entity.getDocId()));
        Document document = map(entity);
        if (count> 0){
            collection.findOneAndReplace(Filters.eq("id",entity.getDocId()),document);
        }else{
            collection.insertOne(document);
        }
    }

    @Override
    public CrawlDataImageEntity findByDocId(Integer docId) {
        if(docId == null){
            return null;
        }else{
            return map((Document) collection.find(Filters.eq("docId", docId)).iterator().tryNext());
        }
    }

    @Override
    public Document map(CrawlDataImageEntity entity) {
        Document document = new Document();
        document.put("docId",entity.getDocId());
        document.put("imageLink",entity.getImageLink());
        return document;
    }

    @Override
    public CrawlDataImageEntity map(Document document) {
        CrawlDataImageEntity crawlDataImageEntity = new CrawlDataImageEntity();
        crawlDataImageEntity.setDocId((Integer) document.get("docId"));
        crawlDataImageEntity.setImageLink((Map<String, String>) document.get("imageLink"));
        return crawlDataImageEntity;
    }
}
