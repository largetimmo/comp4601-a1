package dao.indexer;

import dao.modal.CrawlDataEntity;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Indexer {
    private int count;
    private IndexWriter writer;
    Document doc;
    private String PATH = "lucene";


    public Indexer() throws IOException
    {
        try{
            for (File file : new java.io.File("lucene").listFiles()){
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
        }catch (NullPointerException e){}

        Directory indexDirectory = FSDirectory.open(new File(PATH).toPath());
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(indexDirectory, indexWriterConfig);
        //indexDocument(writer);
        this.count = 0;
    }

//    public void resetDocs() throws IOException {
//        //clean all old files
////        for(File file: Objects.requireNonNull(new File("lucene").listFiles())) {
////            if (!file.isDirectory())
////                file.delete();
////        }
//        for (File file : new java.io.File("lucene").listFiles()){
//            if (!file.isDirectory()) {
//                file.delete();
//            }
//        }
//
//    }

    public boolean indexDocuments(boolean boost, List<CrawlDataEntity> l) throws IOException {
        for (CrawlDataEntity c : l){
            boolean temp = indexADocument(c,boost);
            if (!temp){
                return false;
            }
        }
        writer.close();
        return true;
    }


    public boolean indexADocument(CrawlDataEntity document,boolean boost) throws IOException {
        doc = new Document();
        TextField docId = new TextField("docId",document.getDocId().toString(), Field.Store.YES);
        TextField content;
        if (document.getContent() != null){
            String temp = "";
            for (String s: document.getContent()){
                temp+=s+"\n";
            }
             content = new TextField("content",temp,Field.Store.YES);
        }else {
             content = new TextField("content","",Field.Store.YES);
        }
        TextField i = new TextField("i","Liu-Kyle",Field.Store.YES);
        TextField docURL = new TextField("docURL",document.getUrl(),Field.Store.YES);
        TextField docDate = new TextField("docDate",document.getTimestamp().toString(), Field.Store.YES);
        TextField type = new TextField("type",document.getMetadata().get("Content-Type"),Field.Store.YES);

        if (boost){
            try{
                docId.setBoost(document.getScore().floatValue());
                content.setBoost(document.getScore().floatValue());
                i.setBoost(document.getScore().floatValue());
                docURL.setBoost(document.getScore().floatValue());
                docDate.setBoost(document.getScore().floatValue());
                type.setBoost(document.getScore().floatValue());
            }catch (NullPointerException e){
                return false;
            }
        }

        doc.add(docId);
        doc.add(content);
        doc.add(i);
        doc.add(docURL);
        doc.add(docDate);
        doc.add(type);


        writer.addDocument(doc);
        System.out.println("-------------------------------------------------------"+doc.get("docId"));
        return true;
    }

    public void close() throws CorruptIndexException, IOException
    {
        writer.close();
    }

}
