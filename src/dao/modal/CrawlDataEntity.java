package dao.modal;

import java.util.List;
import java.util.Map;

public class CrawlDataEntity {

    private Integer id;

    private List<String> content;

    private String url;

    private List<String> childUrl;

    private Long timestamp;

    private Integer docId;

    private String docName;

    private Map<String,String> metadata;

    private Double score;

    public CrawlDataEntity(Integer id,  String url, List<String> childUrl, Long timestamp) {
        this.id = id;
        this.url = url;
        this.childUrl = childUrl;
        this.timestamp = timestamp;
        this.score = 0.0;
    }

    public CrawlDataEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getChildUrl() {
        return childUrl;
    }

    public void setChildUrl(List<String> childUrl) {
        this.childUrl = childUrl;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }
    public Double getScore(){ return score; }

    public void setScore(double score){this.score = score;}
}
