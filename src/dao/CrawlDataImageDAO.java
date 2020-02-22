package dao;

import dao.modal.CrawlDataImageEntity;

import java.util.List;

public interface CrawlDataImageDAO extends MongoDAO<CrawlDataImageEntity> {

    void create(CrawlDataImageEntity entity);

    CrawlDataImageEntity findByDocId(Integer docId);

    List<CrawlDataImageEntity> findAll();
}
