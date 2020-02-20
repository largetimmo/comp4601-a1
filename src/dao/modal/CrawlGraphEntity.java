package dao.modal;


import java.util.List;

public class CrawlGraphEntity {

    private Integer id;

    private List<Integer> edges;

    public CrawlGraphEntity(Integer id, List<Integer> edges) {
        this.id = id;
        this.edges = edges;
    }

    public CrawlGraphEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getEdges() {
        return edges;
    }

    public void setEdges(List<Integer> edges) {
        this.edges = edges;
    }
}
