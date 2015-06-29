package net.oschina.gitapp.bean;

@SuppressWarnings("serial")
public class ProjectMember extends User {

	public static final String URL = "/members";
	
	private int _accessLevel;

	public int getAccessLevel() {
		return _accessLevel;
	}

	public void setAccessLevel(int accessLevel) {
		_accessLevel = accessLevel;
	}
	
}
