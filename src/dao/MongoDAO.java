package dao;

import org.bson.Document;

/**
 * Base Mongo dao interface
 * @param <T> Entity class
 */

public interface MongoDAO<T> {

    Document map(T entity);

    T map(Document document);
}
