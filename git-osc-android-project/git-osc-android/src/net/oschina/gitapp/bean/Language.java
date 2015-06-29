package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 编程语言实体类
 * @created 2014-08-02
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Language extends Entity {
	
	@JsonProperty("created_at")
	private Date created_at;
	
	@JsonProperty("detail")
	private String detail;
	
	@JsonProperty("ident")
	private String ident;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("order")
	private int order;
	
	@JsonProperty("parent_id")
	private String parent_id;
	
	@JsonProperty("projects_count")
	private int projects_count;
	
	@JsonProperty("updated_at")
	private Date updated_at;

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public int getProjects_count() {
		return projects_count;
	}

	public void setProjects_count(int projects_count) {
		this.projects_count = projects_count;
	}

	public Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	
	
}
