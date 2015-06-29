package net.oschina.gitapp.interfaces;

/**
 * 状态监听事件
 * @author 火蚁
 *
 */
public interface OnStatusListener {
	public final static int STATUS_NONE = 0x0;
	public final static int STATUS_LOADING = 0x01;
	public final static int STATUS_LOADED = 0x11;

	public void onStatus(int status);
}
