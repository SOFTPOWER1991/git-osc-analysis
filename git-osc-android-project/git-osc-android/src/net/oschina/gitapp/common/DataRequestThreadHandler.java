package net.oschina.gitapp.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class DataRequestThreadHandler extends HandlerThread 
	implements Handler.Callback{
	
	private Handler mMainHandler;
	private Handler mThreadHandler;
	
	public DataRequestThreadHandler() {
		super("datarequest_handler");
	}
	
	/**
	 * 请求数据
	 * */
	public synchronized void request(int what, @SuppressWarnings("rawtypes") AsyncDataHandler datahandler) {
		if(datahandler == null) {
			return;
		}
		if(!isAlive()) {
			//启动线程
			start();
		}
		datahandler.onPreExecute();
		if(mThreadHandler == null) {
			mThreadHandler = new Handler(getLooper(), this);
		}
		//将相同的任务移除
		mThreadHandler.removeMessages(what);
		mThreadHandler.obtainMessage(what, datahandler).sendToTarget();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean handleMessage(Message msg) {
		final AsyncDataHandler dataHandler = (AsyncDataHandler) msg.obj;
		final Object result = dataHandler.execute();
		if(mMainHandler == null) {
			mMainHandler = new Handler(Looper.getMainLooper());
		}
		//通过主线程，将数据回调
		mMainHandler.post(new Runnable() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				dataHandler.onPostExecute(result);
			}
		});
		return true;
	}
	
	public static interface AsyncDataHandler<Result> {

		/** 准备异步执行前(主线程)*/
		public void onPreExecute();
		/** 异步执行*/
		public Result execute();
		/** 主线程回调结果(主线程)*/
		public void onPostExecute(Result result);
	}
}
