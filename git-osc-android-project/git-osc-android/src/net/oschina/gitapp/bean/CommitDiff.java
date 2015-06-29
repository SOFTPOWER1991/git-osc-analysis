package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * commit文件diff实体类
 * @created 2014-06-11 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 * 最后更新：
 * 更新者：
 */
@SuppressWarnings("serial")
public class CommitDiff extends Entity {
	
	public final static String TYPE_TEXT = "text";//文本文件
	public final static String TYPE_IMAGE = "image";//图片文件
	public final static String TYPE_BINARY = "binary";//二进制文件
	
	@JsonProperty("diff")
	private String _diff;
	
	@JsonProperty("new_path")
	private String _new_path;
	
	@JsonProperty("old_path")
	private String _old_path;
	
	@JsonProperty("a_mode")
	private int _a_mode;
	
	@JsonProperty("b_mode")
	private int _b_mode;
	
	@JsonProperty("new_file")
	private boolean _new_file;
	
	@JsonProperty("renamed_file")
	private boolean _renamed_file;
	
	@JsonProperty("deleted_file")
	private boolean _deleted_file;
	
	@JsonProperty("type")
	private String _type;

	public String getDiff() {
		return _diff;
	}

	public void setDiff(String diff) {
		this._diff = diff;
	}

	public String getNew_path() {
		return _new_path;
	}

	public void setNew_path(String new_path) {
		this._new_path = new_path;
	}

	public String getOld_path() {
		return _old_path;
	}

	public void setOld_path(String old_path) {
		this._old_path = old_path;
	}

	public int getA_mode() {
		return _a_mode;
	}

	public void setA_mode(int a_mode) {
		this._a_mode = a_mode;
	}

	public int getB_mode() {
		return _b_mode;
	}

	public void setB_mode(int b_mode) {
		this._b_mode = b_mode;
	}

	public boolean isNew_file() {
		return _new_file;
	}

	public void setNew_file(boolean new_file) {
		this._new_file = new_file;
	}

	public boolean isRenamed_file() {
		return _renamed_file;
	}

	public void setRenamed_file(boolean renamed_file) {
		this._renamed_file = renamed_file;
	}

	public boolean isDeleted_file() {
		return _deleted_file;
	}

	public void setDeleted_file(boolean deleted_file) {
		this._deleted_file = deleted_file;
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		this._type = type;
	}
	
}
