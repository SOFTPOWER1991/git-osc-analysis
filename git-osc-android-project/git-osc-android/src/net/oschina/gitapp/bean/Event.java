package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 用户最新动态实体类
 * @created 2014-05-19 下午18：00
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 */
@SuppressWarnings("serial")
public class Event extends Entity {
	  
	/** 动态的类型*/
	public final static byte EVENT_TYPE_CREATED   = 0x1;// 创建了issue
	public final static byte EVENT_TYPE_UPDATED   = 0x2;// 更新项目
	public final static byte EVENT_TYPE_CLOSED    = 0x3;// 关闭项目
  	public final static byte EVENT_TYPE_REOPENED  = 0x4;// 重新打开了项目
  	public final static byte EVENT_TYPE_PUSHED    = 0x5;// push
  	public final static byte EVENT_TYPE_COMMENTED = 0x6;// 评论
  	public final static byte EVENT_TYPE_MERGED    = 0x7;// 合并
  	public final static byte EVENT_TYPE_JOINED    = 0x8; //# User joined project
  	public final static byte EVENT_TYPE_LEFT      = 0x9; //# User left project
  	public final static byte EVENT_TYPE_FORKED    = 0xb;// fork了项目
	
  	@JsonProperty("action")
	private int _action;
  	
	@JsonProperty("author")
	private User _author;
	
	@JsonProperty("author_id")
	private int _author_id;
	
	@JsonProperty("created_at")
	private Date _created_at;
	
	@JsonProperty("data")
	private Data _data;// 数据
	
	@JsonProperty("project")
	private Project _project;
	
	@JsonProperty("project_id")
	private int _project_id;
	
	@JsonProperty("public")
	private boolean _public;
	
	@JsonProperty("target_id")
	private String _target_id;
	
	@JsonProperty("target_type")
	private String _target_type;
	
	@JsonProperty("title")
	private String _title;
	
	@JsonProperty("update_at")
	private Date _updated_at;
	
	@JsonProperty("events")
	private Events _events;
	
	public Events getEvents() {
		return _events;
	}
	public void setEvents(Events events) {
		this._events = events;
	}
	public Project getProject() {
		return _project;
	}
	public void setProject(Project project) {
		this._project = project;
	}
	public int getAction() {
		return _action;
	}
	public void setAction(int action) {
		this._action = action;
	}
	public User getAuthor() {
		return _author;
	}
	public void setAuthor(User author) {
		this._author = author;
	}
	public int getAuthor_id() {
		return _author_id;
	}
	public void setAuthor_id(int author_id) {
		this._author_id = author_id;
	}
	public Date getCreated_at() {
		return _created_at;
	}
	public void setCreated_at(Date created_at) {
		this._created_at = created_at;
	}
	public Data getData() {
		return _data;
	}
	public void setData(Data data) {
		this._data = data;
	}
	public int getProject_id() {
		return _project_id;
	}
	public void setProject_id(int project_id) {
		this._project_id = project_id;
	}
	public boolean isPublic() {
		return _public;
	}
	public void setPublic(boolean _public) {
		this._public = _public;
	}
	public String getTarget_id() {
		return _target_id;
	}
	public void _setTarget_id(String target_id) {
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
}