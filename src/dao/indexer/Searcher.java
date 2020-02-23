package dao.indexer;

import dao.impl.CrawlDataDAOImpl;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Searcher {
    private IndexSearcher searcher;
    private QueryParser qParser;
    private Query query;
    private String PATH = "lucene";

    public Searcher() throws IOException {
        //Directory indexDirectory = FSDirectory.open(new File(indexDir).toPath());
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(PATH).toPath())));
        qParser = new QueryParser("content",new StandardAnalyzer());
    }

    public TopDocs search( String searchQuery,int hitsPerPage)
            throws IOException, ParseException {
        query = qParser.parse(searchQuery);
        return searcher.search(query,hitsPerPage);
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException {
        return searcher.doc(scoreDoc.doc);
    }

    public void print(ScoreDoc[] hits) throws IOException {
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("docId") + "\t" + d.get("docURL"));
        }
    }

    public List<edu.carleton.comp4601.dao.Document> getDocuments(ScoreDoc[] hits) throws CorruptIndexException, IOException{
        //DocumentCollection dc = new DocumentCollection();
        List<edu.carleton.comp4601.dao.Document> temp = new ArrayList<edu.carleton.comp4601.dao.Document>();
        for (int i=0;i<hits.length;i++){
            Document document = getDocument(hits[i]);
            System.out.println("-------------------------------------------------------"+document.get("docId"));
            edu.carleton.comp4601.dao.Document doc = new edu.carleton.comp4601.dao.Document(Integer.parseInt(document.get("docId")));
            doc.setContent(document.get("content"));
            doc.setUrl(document.get("docURL"));
            doc.setScore(hits[i].score);
            doc.setName(CrawlDataDAOImpl.getInstance().findByDocID(doc.getId()).getDocName());
            temp.add(doc);
        }
        //dc.setDocuments((ArrayList<edu.carleton.comp4601.dao.Document>) temp);
        return temp;
    }
}
