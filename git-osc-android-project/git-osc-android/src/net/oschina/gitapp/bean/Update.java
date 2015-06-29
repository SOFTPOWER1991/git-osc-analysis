package net.oschina.gitapp.bean;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 应用程序更新实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressWarnings("serial")
public class Update extends Entity {
	
	@JsonProperty("created_at")
	private Date _created_at;
	
	@JsonProperty("description")
	private String _description;
	
	@JsonProperty("download_url")
	private String _download_url;
	
	@JsonProperty("num_version")
	private int _num_version;
	
	@JsonProperty("relevance_url")
	private String _relevance_url;
	
	@JsonProperty("updated_at")
	private Date _updated_at;
	
	@JsonProperty("version")
	private String _version;
	
	@JsonProperty("version_type")
	private int _version_type;

	public Date getCreated_at() {
		return _created_at;
	}

	public void setCreated_at(Date created_at) {
		this._created_at = created_at;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		this._description = description;
	}

	public String getDownload_url() {
		return _download_url;
	}

	public void setDownload_url(String download_url) {
		this._download_url = download_url;
	}

	public int getNum_version() {
		return _num_version;
	}

	public void setNum_version(int num_version) {
		this._num_version = num_version;
	}

	public String getRelevance_url() {
		return _relevance_url;
	}

	public void setRelevance_url(String relevance_url) {
		this._relevance_url = relevance_url;
	}

	public Date getUpdated_at() {
		return _updated_at;
	}

	public void setUpdated_at(Date updated_at) {
		this._updated_at = updated_at;
	}

	public String getVersion() {
		return _version;
	}

	public void setVersion(String version) {
		this._version = version;
	}

	public int getVersion_type() {
		return _version_type;
	}

	public void setVersion_type(int version_type) {
		this._version_type = version_type;
	}
	
}
