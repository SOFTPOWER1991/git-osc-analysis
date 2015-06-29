package net.oschina.gitapp.bean;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class ProjectNotification extends Entity {
	
	@JsonProperty("name")
	private String _name;
	
	@JsonProperty("created_at")
	private Date _created_at;
	
	@JsonProperty("creator_id")
	private int _creator_id;
	
	@JsonProperty("owner")
	private User _owner;
	
	@JsonProperty("notifications")
	private List<Notification> _notifications;

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public Date getCreated_at() {
		return _created_at;
	}

	public void setCreated_at(Date created_at) {
		this._created_at = created_at;
	}

	public int getCreator_id() {
		return _creator_id;
	}

	public void setCreator_id(int creator_id) {
		this._creator_id = creator_id;
	}

	public User getOwner() {
		return _owner;
	}

	public void setOwner(User owner) {
		this._owner = owner;
	}

	public List<Notification> getNotifications() {
		return _notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this._notifications = notifications;
	}
}
