package net.oschina.gitapp.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 用户关注数量实体类(followers/starred项目)
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
@SuppressWarnings("serial")
public class Follow implements Serializable {
	
	@JsonProperty("followers")
	private int _followers;
	
	@JsonProperty("starred")
	private int _starred;
	
	@JsonProperty("following")
	private int _following;
	
	@JsonProperty("watched")
	private int _watched;

	public int getFollowers() {
		return _followers;
	}

	public void setFollowers(int followers) {
		this._followers = followers;
	}

	public int getStarred() {
		return _starred;
	}

	public void setStarred(int starred) {
		this._starred = starred;
	}

	public int getFollowing() {
		return _following;
	}

	public void setFollowing(int following) {
		this._following = following;
	}

	public int getWatched() {
		return _watched;
	}

	public void setWatched(int watched) {
		this._watched = watched;
	}
	
	
}
