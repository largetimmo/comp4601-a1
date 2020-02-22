package dao.modal;

import java.util.Map;

public class CrawlDataImageEntity {

    private Integer docId; //HTML document id

    private Map<String,String> imageLink;

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Map<String, String> getImageLink() {
        return imageLink;
    }

    public void setImageLink(Map<String, String> imageLink) {
        this.imageLink = imageLink;
    }
}
