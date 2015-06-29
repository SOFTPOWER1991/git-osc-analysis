package net.oschina.gitapp.bean;

import net.oschina.gitapp.AppContext;

/**
 * 类名 MessageData.java</br>
 * 创建日期 2014年4月25日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月25日 上午1:04:14</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 数据加载结果
 */
public class MessageData<Result extends PageList<?>>{

	public static final int MESSAGE_STATE_ERROR = -1;
	public static final int MESSAGE_STATE_EMPTY = 0;
	public static final int MESSAGE_STATE_MORE = 1;
	public static final int MESSAGE_STATE_FULL = 2;
	
	
	public int state;
	public Result result;
	public Exception exception;
	
	public MessageData(int state) {
		this.state = state;
		this.result = null;
		this.exception = null;
	}
	
	public MessageData(Result result) {
		if(result != null) {
			int size = result.getPageSize();
			if(size == 0) {
				this.state = MESSAGE_STATE_EMPTY;
			} else if(size < AppContext.PAGE_SIZE) {
				this.state = MESSAGE_STATE_FULL;
			} else {
				this.state = MESSAGE_STATE_MORE;
			}
		} else {
			this.state = MESSAGE_STATE_ERROR;
		}
		this.result = result;
		this.exception = null;
	}
	
	public MessageData(Exception exception) {
		this.state = MESSAGE_STATE_ERROR;
		this.result = null;
		this.exception = exception;
	}
}