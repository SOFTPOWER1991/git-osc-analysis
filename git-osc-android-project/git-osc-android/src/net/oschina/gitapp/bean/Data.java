package net.oschina.gitapp.bean;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 个人动态中的数据实体类
 * @created 2014-05-19 下午18：08
 * @author 火蚁(http://my.oschina.net/LittleDY)
 *
 */
@SuppressWarnings("serial")
public class Data implements Serializable {
	
	@JsonProperty("before")
	private String _before;
	
	@JsonProperty("after")
	private String _after;
	
	@JsonProperty("ref")
	private String _ref;
	
	@JsonProperty("user_id")
	private int _user_id;
	
	@JsonProperty("user_name")
	private String _user_name;
	
	// 仓库
	@JsonProperty("repository")
	private Repository _repository;
	
	// 提交，可能有多个commits
	@JsonProperty("commits")
	private List<Commit> _commits;
	
	// 提交的数量
	@JsonProperty("total_commits_count")
	private int _total_commits_count;
	
	public int getTotal_commits_count() {
		return _total_commits_count;
	}
	public void setTotal_commits_count(int total_commits_count) {
		this._total_commits_count = total_commits_count;
	}
	public List<Commit> getCommits() {
		return _commits;
	}
	public void setCommits(List<Commit> commits) {
		this._commits = commits;
	}
	public Repository getRepository() {
		return _repository;
	}
	public void setRepository(Repository repository) {
		this._repository = repository;
	}
	public String getBefore() {
		return _before;
	}
	public void setBefore(String before) {
		this._before = before;
	}
	public String getAfter() {
		return _after;
	}
	public void setAfter(String after) {
		this._after = after;
	}
	public String getRef() {
		return _ref;
	}
	public void setRef(String ref) {
		this._ref = ref;
	}
	public int getUser_id() {
		return _user_id;
	}
	public void setUser_id(int user_id) {
		this._user_id = user_id;
	}
	public String getUser_name() {
		return _user_name;
	}
	public void setUser_name(String user_name) {
		this._user_name = user_name;
	}
}
