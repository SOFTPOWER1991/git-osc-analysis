package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Git 官方的note实体类
 * @created 2014-05-26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class GitNote extends Entity {
    
    @JsonProperty("body")
    private String _body;
    
    @JsonProperty("author")
    private User _author;
    
	@JsonProperty("created_at")
    private Date _created_at;
	
	public String getBody() {
		return _body;
	}

	public void setBody(String body) {
		this._body = body;
	}

	public User getAuthor() {
		return _author;
	}

	public void setAuthor(User author) {
		this._author = author;
	}

	public Date getCreated_at() {
		return _created_at;
	}

	public void setCreated_at(Date created_at) {
		this._created_at = created_at;
	}
}
