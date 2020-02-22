package dao.indexer;

import dao.modal.CrawlDataEntity;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Indexer {
    private int count;
    private IndexWriter writer;
    Document doc;

    public Indexer(String path) throws IOException
    {
        Directory indexDirectory = FSDirectory.open(new File(path).toPath());
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(indexDirectory, indexWriterConfig);
        //indexDocument(writer);
        this.count = 0;
    }

    public void indexDocuments(boolean boost, List<CrawlDataEntity> l) throws IOException {
        for (CrawlDataEntity c : l){
            indexADocument(c);
        }
        writer.close();
    }

    public void indexADocument(CrawlDataEntity document) throws IOException {
        doc = new Document();
        try {
            doc.add(new StringField("docId",document.getDocId().toString(), Field.Store.YES));
            //doc.add(new StringField("i",document.getName(),Field.Store.YES));
            for (String s: document.getContent()){
                doc.add(new TextField("content",s,Field.Store.YES));
            }

            doc.add(new StringField("docURL",document.getUrl(),Field.Store.YES));
            Date date = new Date();
            doc.add(new StringField("docDate",Long.toString(date.getTime()), Field.Store.YES));
            doc.add(new StringField("type",document.getMetadata().get("Content-Type"),Field.Store.YES));

        }catch (NumberFormatException e){}
        writer.addDocument(doc);
        System.out.println("-------------------------------------------------------"+doc.get("docId"));
    }

    public void close() throws CorruptIndexException, IOException
    {
        writer.close();
    }

}
