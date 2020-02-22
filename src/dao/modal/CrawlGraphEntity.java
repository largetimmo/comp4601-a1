package dao.modal;


import java.util.List;

public class  CrawlGraphEntity {

    private String id;

    private List<String> edges;

    public CrawlGraphEntity(String id, List<String> edges) {
        this.id = id;
        this.edges = edges;
    }

    public CrawlGraphEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getEdges() {
        return edges;
    }

    public void setEdges(List<String> edges) {
        this.edges = edges;
    }
}
