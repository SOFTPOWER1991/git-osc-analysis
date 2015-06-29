package net.oschina.gitapp.bean;

import java.util.Date;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 评论comment实体类
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Comment extends Entity {

    @JsonProperty("note")
	private String _note;
	
	@JsonProperty("created_at")
	private Date _created_at;

	@JsonProperty("project_id")
    private int _project_id;
	
	@JsonProperty("line_code")
	private String _line_code;
	
	@JsonProperty("commit_id")
	private String _commit_id;
	
	@JsonProperty("author")
	private User _author;

	public String getNote() {
		return _note;
	}

	public void setNote(String note) {
		this._note = note;
	}

	public Date getCreated_at() {
		return _created_at;
	}

	public void setCreated_at(Date created_at) {
		this._created_at = created_at;
	}

	public int getProject_id() {
		return _project_id;
	}

	public void setProject_id(int project_id) {
		this._project_id = project_id;
	}

	public String getLine_code() {
		return _line_code;
	}

	public void setLine_code(String line_code) {
		this._line_code = line_code;
	}

	public String getCommit_id() {
		return _commit_id;
	}

	public void setCommit_id(String commit_id) {
		this._commit_id = commit_id;
	}

	public User getAuthor() {
		return _author;
	}

	public void setAuthor(User author) {
		this._author = author;
	}
}
