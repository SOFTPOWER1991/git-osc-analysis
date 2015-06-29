package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 里程碑实体类
 * @author 火蚁
 *
 */
@SuppressWarnings("serial")
public class Milestone extends Entity {
	
	@JsonProperty("iid")
	private int _iid;
	
	@JsonProperty("projectId")
	private int _projectId;
	
	@JsonProperty("title")
	private String _title;
	
	@JsonProperty("description")
	private String _description;
	
	@JsonProperty("due_date")
	private Date _dueDate;
	
	private String _state;
	
	@JsonProperty("updated_date")
	private Date _updatedDate;
	
	@JsonProperty("created_date")
	private Date _createdDate;

	public int getIid() {
		return _iid;
	}

	public void setIid(int iid) {
		_iid = iid;
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

	public Date getDueDate() {
		return _dueDate;
	}

	public void setDueDate(Date dueDate) {
		_dueDate = dueDate;
	}

	public String getState() {
		return _state;
	}

	public void setState(String state) {
		_state = state;
	}

	public Date getUpdatedDate() {
		return _updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		_updatedDate = updatedDate;
	}

	public Date getCreatedDate() {
		return _createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		_createdDate = createdDate;
	}
	
}
