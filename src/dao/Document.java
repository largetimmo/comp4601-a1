package dao;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used to send messages between one search engine and another 
 * and to allow clients to ask for a complete Document found by Lucene.
 * 
 * @author tonywhite
 *
 */
@XmlRootElement
public class Document implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2724551130129647004L;
	private Integer id;
	private Float score;
	private String name;
	private String url;
	private String content;

	public Document() {
	}

	public Document(Integer id) {
		this();
		this.id = id;
	}

	public Document(Map<?, ?> map) {
		this();
		this.id = (Integer) map.get("id");
		this.score = (Float) map.get("score");
		this.name = (String) map.get("name");
		this.content = (String) map.get("content");
		this.url = (String) map.get("url");
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Integer getId() {
		return id;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public Float getScore() {
		return score;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
