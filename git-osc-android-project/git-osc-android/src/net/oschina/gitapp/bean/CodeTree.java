package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class CodeTree extends Entity {
	
	public final static String TYPE_TREE = "tree";
	public final static String TYPE_BLOB = "blob";
	
	@JsonProperty("name")
	private String _name;
	
	@JsonProperty("type")
	private String _type;
	
	@JsonProperty("mode")
	private String _mode;
	
	private String _path;
	
	public String getPath() {
		return _path;
	}
	public void setPath(String path) {
		this._path = path;
	}
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		this._name = name;
	}
	public String getType() {
		return _type;
	}
	public void setType(String type) {
		this._type = type;
	}
	public String getMode() {
		return _mode;
	}
	public void setMode(String mode) {
		this._mode = mode;
	}
}
