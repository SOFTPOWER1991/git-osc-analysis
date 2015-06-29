package net.oschina.gitapp.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class LuckMsg implements Serializable {
	
	@JsonProperty("message")
	private String _message;

	public String getMessage() {
		return _message;
	}

	public void setMessage(String message) {
		this._message = message;
	}
}
