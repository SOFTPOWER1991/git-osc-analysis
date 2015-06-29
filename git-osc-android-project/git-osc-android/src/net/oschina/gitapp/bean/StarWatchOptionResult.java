package net.oschina.gitapp.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * star or watch 一个项目返回的结果实体类
 * @created 2014-08-21
 * @author deyi
 *
 */
@SuppressWarnings("serial")
public class StarWatchOptionResult implements Serializable {
	
	@JsonProperty("count")
	private int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
