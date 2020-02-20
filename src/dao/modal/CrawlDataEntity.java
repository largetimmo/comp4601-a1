package dao.modal;

public class CrawlDataEntity {

    private Integer id;

    private String content;

    public CrawlDataEntity(Integer id, String content) {
        this.id = id;
        this.content = content;
    }

    public CrawlDataEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
