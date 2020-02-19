package dao;

import edu.carleton.comp4601.dao.Document;
import edu.carleton.comp4601.dao.DocumentCollection;

public interface CrawlDataDAO {

    void create(Document document);

    Document findOneById();

    DocumentCollection findAll();

    void update(Document document);

    void delete(Integer id);


}
