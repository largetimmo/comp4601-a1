package dao;

import dao.modal.Document;

public interface CrawlDataDAO {

    void create(Document document);

    void findOneById();

    void findAll();

    void update(Document document);

    void delete(Integer id);


}
