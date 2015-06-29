package net.oschina.gitapp.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressWarnings("serial")
public abstract class Entity implements Serializable {
	
	@JsonProperty("id")
	protected String _id;

	public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }
	
	// 缓存的key
	protected String cacheKey;

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
}
