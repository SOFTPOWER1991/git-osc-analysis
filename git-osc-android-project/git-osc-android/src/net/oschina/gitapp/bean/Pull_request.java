package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * pr实体类
 * @created 2014-07-14
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Pull_request extends Entity {
	
	@JsonProperty("iid")
	private int _iid;
	
	@JsonProperty("title")
	private String _title;

	public int getIid() {
		return _iid;
	}

	public void setIid(int iid) {
		this._iid = iid;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		this._title = title;
	}
}
