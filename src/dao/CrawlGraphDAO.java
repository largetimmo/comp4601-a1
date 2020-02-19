package dao;

import dao.modal.CrawlGraphEntity;

import java.util.List;

public interface CrawlGraphDAO {

    void addDocument(CrawlGraphEntity crawlGraphEntity);

    List<CrawlGraphEntity> findAll();

}
