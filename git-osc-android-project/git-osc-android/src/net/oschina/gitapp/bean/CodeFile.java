package net.oschina.gitapp.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 文件实体类
 * @created 2014-06-04
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class CodeFile implements Serializable {
	
	public static final String  ENCODING_BASE64 = "base64";
	
	@JsonProperty("file_name")
	private String _file_name;
	
	@JsonProperty("file_path")
	private String _file_path;
	
	@JsonProperty("size")
	private int _size;
	
	@JsonProperty("encoding")
	private String _encoding;
	
	// 文件的内容
	@JsonProperty("content")
	private String _content;
	
	@JsonProperty("ref")
	private String _ref;
	
	@JsonProperty("blob_id")
	private String _blob_id;
	
	@JsonProperty("commit_id")
	private String _commit_id;

	public String getFile_name() {
		return _file_name;
	}

	public void setFile_name(String file_name) {
		this._file_name = file_name;
	}

	public String getFile_path() {
		return _file_path;
	}

	public void setFile_path(String file_path) {
		this._file_path = file_path;
	}

	public int getSize() {
		return _size;
	}

	public void setSize(int size) {
		this._size = size;
	}

	public String getEncoding() {
		return _encoding;
	}

	public void setEncoding(String encoding) {
		this._encoding = encoding;
	}

	public String getContent() {
		return _content;
	}

	public void setContent(String content) {
		this._content = content;
	}

	public String getRef() {
		return _ref;
	}

	public void setRef(String ref) {
		this._ref = ref;
	}

	public String getBlob_id() {
		return _blob_id;
	}

	public void setBlob_id(String blob_id) {
		this._blob_id = blob_id;
	}

	public String getCommit_id() {
		return _commit_id;
	}

	public void setCommit_id(String commit_id) {
		this._commit_id = commit_id;
	}
}
