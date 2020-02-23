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
import java.util.List;
import java.util.Objects;

public class Indexer {
    private int count;
    private IndexWriter writer;
    Document doc;
    private String PATH = "lucene";

    public Indexer() throws IOException
    {
        Directory indexDirectory = FSDirectory.open(new File(PATH).toPath());
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(indexDirectory, indexWriterConfig);
        //indexDocument(writer);
        this.count = 0;
    }

    public void resetDocs(){
        //clean all old files
        for(File file: Objects.requireNonNull(new File("lucene").listFiles())) {
            if (!file.isDirectory())
                file.delete();
        }
    }

    public void indexDocuments(boolean boost, List<CrawlDataEntity> l) throws IOException {
        for (CrawlDataEntity c : l){
            indexADocument(c,boost);
        }
        writer.close();
    }


    public void indexADocument(CrawlDataEntity document,boolean boost) throws IOException {
        doc = new Document();
        StringField docId = new StringField("docId",document.getDocId().toString(), Field.Store.YES);
        if (document.getContent() != null){
            String temp = "";
            for (String s: document.getContent()){
                temp+=s+"\n";
            }
            TextField content = new TextField("content",temp,Field.Store.YES);
        }else {
            TextField content = new TextField("content","",Field.Store.YES);
        }
        StringField i = new StringField("i","Liu-Kyle",Field.Store.YES);
        StringField docURL = new StringField("docURL",document.getUrl(),Field.Store.YES);
        StringField docDate = new StringField("docDate",document.getTimestamp().toString(), Field.Store.YES);
        StringField type = new StringField("type",document.getMetadata().get("Content-Type"),Field.Store.YES);

        if (boost){
            //docId.setboo
        }


        writer.addDocument(doc);
        System.out.println("-------------------------------------------------------"+doc.get("docId"));
    }

    public void close() throws CorruptIndexException, IOException
    {
        writer.close();
    }

}
