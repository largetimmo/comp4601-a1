package dao;

import dao.modal.CrawlDataImageEntity;

public interface CrawlDataImageDAO extends MongoDAO<CrawlDataImageEntity> {

    void create(CrawlDataImageEntity entity);

    CrawlDataImageEntity findByDocId(Integer docId);
}
