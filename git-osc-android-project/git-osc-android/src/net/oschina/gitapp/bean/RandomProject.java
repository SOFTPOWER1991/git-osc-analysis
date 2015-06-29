package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 随机项目实体类，带有抽奖信息
 * @created 2014-08-28
 * @author 火蚁(http://my.oschina.net/LittleDY)
 *
 */
@SuppressWarnings("serial")
public class RandomProject extends Project {
	
	@JsonProperty("rand_num")
	private int _rand_num;
	
	@JsonProperty("msg")
	private String _msg;
	
	@JsonProperty("img")
	private String _img;

	public int getRand_num() {
		return _rand_num;
	}

	public void setRand_num(int rand_num) {
		this._rand_num = rand_num;
	}

	public String getMsg() {
		return _msg;
	}

	public void setMsg(String msg) {
		this._msg = msg;
	}

	public String getImg() {
		return _img;
	}

	public void setImg(String img) {
		this._img = img;
	}
}
