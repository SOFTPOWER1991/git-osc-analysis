package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * note实体类
 * @created 2014-05-26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Note extends Entity {

    @JsonProperty("author_id")
    private int _author_id;
    
    @JsonProperty("commit_id")
    private String _commit_id;
    
    @JsonProperty("created_at")
    private Date _created_at;
    
    @JsonProperty("line_code")
    private String _line_code;
    
    @JsonProperty("note")
    private String _note;
    
    @JsonProperty("noteable_id")
    private int _noteable_id;
    
    @JsonProperty("noteable_type")
    private String _noteable_type;
    
    @JsonProperty("project_id")
    private int _project_id;
    
    @JsonProperty("updated_at")
    private Date _updated_at;
    
	public int getAuthor_id() {
		return _author_id;
	}
	public void setAuthor_id(int author_id) {
		this._author_id = author_id;
	}
	public String getCommit_id() {
		return _commit_id;
	}
	public void setCommit_id(String commit_id) {
		this._commit_id = commit_id;
	}
	public Date getCreated_at() {
		return _created_at;
	}
	public void setCreated_at(Date created_at) {
		this._created_at = created_at;
	}
	public String getLine_code() {
		return _line_code;
	}
	public void setLine_code(String line_code) {
		this._line_code = line_code;
	}
	public String getNote() {
		return _note;
	}
	public void setNote(String note) {
		this._note = note;
	}
	public int getNoteable_id() {
		return _noteable_id;
	}
	public void setNoteable_id(int noteable_id) {
		this._noteable_id = noteable_id;
	}
	public String getNoteable_type() {
		return _noteable_type;
	}
	public void setNoteable_type(String noteable_type) {
		this._noteable_type = noteable_type;
	}
	public int getProject_id() {
		return _project_id;
	}
	public void setProject_id(int project_id) {
		this._project_id = project_id;
	}
	public Date getUpdated_at() {
		return _updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this._updated_at = updated_at;
	}
}
