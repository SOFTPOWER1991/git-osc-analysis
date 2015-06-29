package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class Session extends User {
	
	@JsonProperty("private_token")
	private String _privateToken;

	public String get_privateToken() {
		return _privateToken;
	}

	public void set_privateToken(String _privateToken) {
		this._privateToken = _privateToken;
	}

}
