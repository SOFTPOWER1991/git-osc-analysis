package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 通知实体类
 * @created 2014-07-04
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Notification extends Entity {
	
	@JsonProperty("created_at")
	private Date _created_at;
	
	@JsonProperty("mute")
	private boolean _mute;
	
	@JsonProperty("participating")
	private boolean _participating;
	
	@JsonProperty("project_id")
	private String _project_id;
	
	@JsonProperty("read")
	private boolean _read;
	
	@JsonProperty("target_id")
	private String _target_id;
	
	@JsonProperty("target_type")
	private String _target_type;
	
	@JsonProperty("title")
	private String _title;
	
	@JsonProperty("updated_at")
	private Date _updated_at;
	
	@JsonProperty("user_id")
	private int _user_id;
	
	@JsonProperty("userinfo")
	private User _userinfo;

	public Date getCreated_at() {
		return _created_at;
	}

	public void setCreated_at(Date created_at) {
		this._created_at = created_at;
	}

	public boolean isMute() {
		return _mute;
	}

	public void setMute(boolean mute) {
		this._mute = mute;
	}

	public boolean isParticipating() {
		return _participating;
	}

	public void setParticipating(boolean participating) {
		this._participating = participating;
	}

	public String getProject_id() {
		return _project_id;
	}

	public void setProject_id(String project_id) {
		this._project_id = project_id;
	}

	public boolean isRead() {
		return _read;
	}

	public void setRead(boolean read) {
		this._read = read;
	}

	public String getTarget_id() {
		return _target_id;
	}

	public void setTarget_id(String target_id) {
		this._target_id = target_id;
	}

	public String getTarget_type() {
		return _target_type;
	}

	public void setTarget_type(String target_type) {
		this._target_type = target_type;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		this._title = title;
	}

	public Date getUpdated_at() {
		return _updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this._updated_at = updated_at;
	}

	public int getUser_id() {
		return _user_id;
	}

	public void setUser_id(int user_id) {
		this._user_id = user_id;
	}

	public User getUserinfo() {
		return _userinfo;
	}

	public void setUserinfo(User userinfo) {
		this._userinfo = userinfo;
	}
}
