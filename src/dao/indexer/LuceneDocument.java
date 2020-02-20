package dao.indexer;

public class LuceneDocument {
    private String name;
    private int id;
    private int score;
    private String url;
    private String content;


    public LuceneDocument(String name, int id,int score,String url,String content){
        super();
        this.name = name;
        this.id = id;
        this.score = score;
        this.url = url;
        this.content = content;
    }
}
