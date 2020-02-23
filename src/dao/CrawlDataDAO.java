package dao;

import dao.modal.CrawlDataEntity;

import java.util.List;

public interface CrawlDataDAO extends MongoDAO<CrawlDataEntity> {

    void create(CrawlDataEntity document);

    CrawlDataEntity findOneById(Integer id);

    List<CrawlDataEntity> findAll();

    void update(CrawlDataEntity document);

    void delete(Integer id);

    CrawlDataEntity findByDocID(Integer docId);

    CrawlDataEntity findByUrl(String url);


}
