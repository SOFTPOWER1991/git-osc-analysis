package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * issue实体类
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Issue extends Entity {
	public enum Action {
		LEAVE, CLOSE, REOPEN
	}
	
	public static final String StateClosed = "closed";
	public static final String StateOpened = "opened";
	
	@JsonProperty("iid")
	private int _iid;
	
	@JsonProperty("project_id")
	private int _projectId;
	
	@JsonProperty("title")
	private String _title;
	
	@JsonProperty("description")
	private String _description;
	
	@JsonProperty("description")
	private String[] _labels;
	
	@JsonProperty("milestone")
	private Milestone _milestone;
	
	@JsonProperty("assignee")
	private User _assignee;
	
	@JsonProperty("author")
	private User _author;
	
	@JsonProperty("stae")
	private String _state;
	
	@JsonProperty("updated_at")
	private Date _updatedAt;
	
	@JsonProperty("created_at")
	private Date _createdAt;
	
	public int getIid() {
		return _iid;
	}

	public void setIid(int iid) {
		this._iid = iid;
	}
	
	public int getProjectId() {
		return _projectId;
	}

	public void setProjectId(int projectId) {
		_projectId = projectId;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public String[] getLabels() {
		return _labels;
	}

	public void setLabels(String[] labels) {
		_labels = labels;
	}

	public Milestone getMilestone() {
		return _milestone;
	}

	public void setMilestone(Milestone milestone) {
		_milestone = milestone;
	}

	public User getAssignee() {
		return _assignee;
	}

	public void setAssignee(User assignee) {
		_assignee = assignee;
	}

	public User getAuthor() {
		return _author;
	}

	public void setAuthor(User author) {
		_author = author;
	}

	public String getState() {
		return _state;
	}

	public void setState(String state) {
		_state = state;
	}

	public Date getUpdatedAt() {
		return _updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		_updatedAt = updatedAt;
	}

	public Date getCreatedAt() {
		return _createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		_createdAt = createdAt;
	}

}
