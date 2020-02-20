package dao.indexer;

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

public class Searcher {
    private IndexSearcher searcher;
    private QueryParser qParser;
    private Query query;

    public Searcher(String indexDir) throws IOException {
        //Directory indexDirectory = FSDirectory.open(new File(indexDir).toPath());
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexDir).toPath())));
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
}
