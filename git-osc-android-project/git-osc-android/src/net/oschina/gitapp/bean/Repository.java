package net.oschina.gitapp.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 仓库实体类
 * @created 2014-05-19 下午18：12
 * @author 火蚁（http://my.oschina,net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Repository implements Serializable {
	
	@JsonProperty("name")
	private String _name;
	
	@JsonProperty("url")
	private String _url;
	
	@JsonProperty("description")
	private String _description;
	
	@JsonProperty("homePage")
	private String _homePage;
	
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		this._name = name;
	}
	public String getUrl() {
		return _url;
	}
	public void setUrl(String url) {
		this._url = url;
	}
	public String getDescription() {
		return _description;
	}
	public void setDescription(String description) {
		this._description = description;
	}
	public String getHomePage() {
		return _homePage;
	}
	public void setHomePage(String homePage) {
		this._homePage = homePage;
	}
}
