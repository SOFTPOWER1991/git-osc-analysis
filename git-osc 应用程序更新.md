# git-osc 应用程序更新

##涉及到的文件：
>1. MainActivity.java
>2. UpdateManager.java
>3. AppContext.java
>4. ApiClient.java
>5. Update.java
>6. URLs.java
>7. HTTPRequestor.java
>8. CyptoUtils.java
>9. HTTPRequestor.java




##涉及的技术点：
>1. 网络部分：Http几种请求方式
>2. 泛型
>3. 下载更新

***

`“启动更时新” `是在设置界面中设置的，当被勾选后，在程序启动时，将会请求服务器检查是否有新版本。如果有则更新，没有程序继续往下进行。

###在MainActivity.java的onCreate()方法中：
> 1. 通过单例模式获取`UpdateManager`实例对象
> 2. 在`UpdateManager`对象中，检查程序版本信息`checkAppUpdate`

```java
// 检查新版本
if (mContext.isCheckUp()) {
	UpdateManager.getUpdateManager().checkAppUpdate(this, false);
}
```

###如果`启动时更新`是选中状态的，就会进入这个方法。此时，程序将会走到`UpdateManager`中，此时会通过单例模式加载一个`UpdateManager`实例：

```java
//单例模式：延迟加载（懒汉式）加载一个UpdateManager实例对象
public static UpdateManager getUpdateManager() {
		if(updateManager == null){
			updateManager = new UpdateManager();
		}
		updateManager.interceptFlag = false;
		return updateManager;
}
```

###获取到`UpdateManager` 实例后，就会进入`checkAppUpdate`方法中:
> 1.第一步检查当前程序版本号： `getCurrentVersion`
> 2.第二步开启线程去服务器检查是否有新版本：`Update update = ((AppContext)mContext.getApplicationContext()).getUpdateInfo();`
> 3.第三部根据服务器返回的数据——`Update`,发送给`Handler` 来进行下一步操作。

```java
/**
	 * 检查App更新
	 * @param context
	 * @param isShowMsg 是否显示提示消息
	 */
	public void checkAppUpdate(Context appContext, final boolean isShowMsg){
		this.mContext = appContext;
		getCurrentVersion();
		if(isShowMsg){
			if(mProDialog == null) {
				mProDialog = ProgressDialog.show(mContext, null, "正在检测，请稍后...", true, true);
				mProDialog.setCanceledOnTouchOutside(false);
			}
			else if(mProDialog.isShowing() || (latestOrFailDialog!=null && latestOrFailDialog.isShowing()))
				return;
		}
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				//进度条对话框不显示 - 检测结果也不显示
				if(mProDialog != null && !mProDialog.isShowing()){
					return;
				}
				//关闭并释放释放进度条对话框
				if(isShowMsg && mProDialog != null){
					mProDialog.dismiss();
					mProDialog = null;
				}
				//显示检测结果
				if(msg.what == 1){
					mUpdate = (Update)msg.obj;
					if(mUpdate != null){
						if(curVersionCode < mUpdate.getNum_version()){
							apkUrl = mUpdate.getDownload_url();
							updateMsg = mUpdate.getDescription();
							showNoticeDialog();
						}else if(isShowMsg){
							showLatestOrFailDialog(DIALOG_TYPE_LATEST);
						}
					}
				}else if(isShowMsg){
					showLatestOrFailDialog(DIALOG_TYPE_FAIL);
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {					
					Update update = ((AppContext)mContext.getApplicationContext()).getUpdateInfo();
					msg.what = 1;
					msg.obj = update;
				} catch (AppException e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}			
		}.start();		
	}	

```

###在`checkAppUpdate`中做的第一步操作是，检查当前程序版本号`getCurrentVersion`: 
>通过PackageManager获取程序版本信息，并赋值给变量`curVersionCode`

```java
	/**
	 * 获取当前客户端版本信息
	 */
	private void getCurrentVersion(){
        try { 
        	PackageInfo info = mContext.getPackageManager().
        			getPackageInfo(mContext.getPackageName(), 0);
        	curVersionCode = info.versionCode;
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
	}

```
###第二步开启线程去服务器查询是否有新的版本，根据返回的`Update`来给出提示：
> 1. 如果`Update != null `，将当前版本和`Update`中获取的程序版本进行比较，如果`curVersionCode < mUpdate.getNum_version()` 那么给出更新提示，否则`提示已是最新版本`

```java
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {					
					Update update = ((AppContext)mContext.getApplicationContext()).getUpdateInfo();
					msg.what = 1;
					msg.obj = update;
				} catch (AppException e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}			
		}.start();		

```







