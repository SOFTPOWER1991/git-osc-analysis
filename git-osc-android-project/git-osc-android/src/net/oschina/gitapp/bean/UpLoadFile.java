package net.oschina.gitapp.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 上传文件实体类
 * @created 2014-07-03
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class UpLoadFile implements Serializable {
	
	@JsonProperty("success")
	private boolean _success;
	
	@JsonProperty("msg")
	private String _msg;
	
	@JsonProperty("file")
	private GitFile _file;

	public boolean isSuccess() {
		return _success;
	}

	public void setSuccess(boolean success) {
		this._success = success;
	}

	public String getMsg() {
		return _msg;
	}

	public void setMsg(String msg) {
		this._msg = msg;
	}

	public GitFile getFile() {
		return _file;
	}

	public void setFile(GitFile file) {
		this._file = file;
	}

	class GitFile {
		@JsonProperty("filename")
		private String _filename;
		
		@JsonProperty("title")
		private String _title;
		
		@JsonProperty("url")
		private String _url;

		public String getFilename() {
			return _filename;
		}

		public void setFilename(String filename) {
			this._filename = filename;
		}

		public String getTitle() {
			return _title;
		}

		public void setTitle(String title) {
			this._title = title;
		}

		public String getUrl() {
			return _url;
		}

		public void setUrl(String url) {
			this._url = url;
		}
	}
}
