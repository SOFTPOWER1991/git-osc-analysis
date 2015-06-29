package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * commit实体类
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Commit extends Entity {

    @JsonProperty("title")
	private String _title;
	
	@JsonProperty("message")
	private String _message;

	@JsonProperty("short_id")
    private String _shortId;
	
	@JsonProperty("author")
	private User _author;
	
	@JsonProperty("author_name")
	private String _author_name;
	
	@JsonProperty("author_email")
	private String _author_email;

    @JsonProperty("created_at")
    private Date _createdAt;
    
    public String getShortId() {
        return _shortId;
    }

    public void setShortId(String shortId) {
        _shortId = shortId;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }
    
    public User getAuthor() {
		return _author;
	}

	public void setAuthor(User author) {
		this._author = author;
	}
	
	public String getAuthor_name() {
		return _author_name;
	}

	public void setAuthor_name(String author_name) {
		this._author_name = author_name;
	}

	public String getAuthor_email() {
		return _author_email;
	}

	public void setAuthor_email(String author_email) {
		this._author_email = author_email;
	}

	public String getMessage() {
		return _message;
	}

	public void setMessage(String message) {
		this._message = message;
	}

    public Date getCreatedAt() {
        return _createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        _createdAt = createdAt;
    }
}
