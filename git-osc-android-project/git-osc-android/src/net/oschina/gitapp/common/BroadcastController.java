package net.oschina.gitapp.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 广播控制器
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
public class BroadcastController {

	public static final String ACTION_USERCHANGE = "git.oschina.net.ACTION_USERCHANGE";
	
	/** 
	 * 发送用户变化的广播
	 * @param context 上下文
	 * */
	public static void sendUserChangeBroadcase(Context context) {
		context.sendBroadcast(new Intent(ACTION_USERCHANGE));
	}
	
	/**
	 * 注册一个监听用户变化的广播
	 * @param context 上下文
	 * @param receiver 广播接收器
	 * */
	public static void registerUserChangeReceiver(Context context, 
			BroadcastReceiver receiver) {
		IntentFilter filter = new IntentFilter(ACTION_USERCHANGE);
		context.registerReceiver(receiver, filter);
	}
	
	/**
	 * 注销一个广播接收器
	 * @param context 上下文
	 * @param receiver 要注销的广播接收器
	 * */
	public static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
		try {
			context.unregisterReceiver(receiver);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}