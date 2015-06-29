package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 分支实体类
 * @author 火蚁(http://my.oschina.net/LittleDY)
 * 
 */
@SuppressWarnings("serial")
public class Branch extends Entity {
	
	public final static String TYPE_BRANCH = "branches";
	public final static String TYPE_TAG = "tags";
	
	@JsonProperty("name")
    private String _name;

    @JsonProperty("commit")
    private Commit _commit;
    
	@JsonProperty("protected")
	private boolean _protected;
	
	private String _type;

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		this._type = type;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public boolean isProtected() {
		return _protected;
	}

	public void setProtected(boolean isProtected) {
		this._protected = isProtected;
	}
}
