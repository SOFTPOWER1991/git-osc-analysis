package net.oschina.gitapp;

import static net.oschina.gitapp.common.Contanst.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.bean.Branch;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.Comment;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.Follow;
import net.oschina.gitapp.bean.GitNote;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Language;
import net.oschina.gitapp.bean.Milestone;
import net.oschina.gitapp.bean.NotificationReadResult;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.ProjectNotificationArray;
import net.oschina.gitapp.bean.RandomProject;
import net.oschina.gitapp.bean.ReadMe;
import net.oschina.gitapp.bean.UpLoadFile;
import net.oschina.gitapp.bean.Update;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.MethodsCompat;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class AppContext extends Application {

	// 手机网络类型
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int PAGE_SIZE = 20;// 默认分页大小
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间
	
	private boolean login = false; // 登录状态
	private int loginUid = 0; // 登录用户的id
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();

	private Handler unLoginHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
				UIHelper.showLoginActivity(AppContext.this);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler(this));
		init();
	}
	
	/**
	 * 获得未登录的handle
	 * @return
	 */
	public Handler getUnLoginHandler(final Context context) {
		Handler unLoginHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
					UIHelper.showLoginActivity(context);
				}
			}
		};
		return unLoginHandler;
	}

	/**
	 * 初始化Application
	 */
	private void init() {
		// 初始化用记的登录信息
		User loginUser = getLoginInfo();
		if (null != loginUser && StringUtils.toInt(loginUser.getId()) > 0
				&& !StringUtils.isEmpty(getProperty(PROP_KEY_PRIVATE_TOKEN))) {
			// 记录用户的id和状态
			this.loginUid = StringUtils.toInt(loginUser.getId());
			this.login = true;
		}
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		String res = AppConfig.getAppConfig(this).get(key);
		return res;
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	/**
	 * 是否Https登录
	 * 
	 * @return
	 */
	public boolean isHttpsLogin() {
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		// 默认是http
		if (StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}

	/**
	 * 设置是是否Https登录
	 * 
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b) {
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}
	
	/**
	 * 是否是第一次启动App
	 * @return
	 */
	public boolean isFristStart() {
		boolean res = false;
		String perf_frist = getProperty(AppConfig.CONF_FRIST_START);
		// 默认是http
		if (StringUtils.isEmpty(perf_frist)) {
			res = true;
			setProperty(AppConfig.CONF_FRIST_START, "false");
		}
		
		return res;
	}

	/**
	 * 是否加载显示文章图片
	 * 
	 * @return
	 */
	public boolean isLoadImage() {
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		// 默认是加载的
		if (StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}

	/**
	 * 设置是否加载文章图片
	 * 
	 * @param b
	 */
	public void setConfigLoadimage(boolean b) {
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}

	/**
	 * 设置是否发出提示音
	 * 
	 * @param b
	 */
	public void setConfigVoice(boolean b) {
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}

	/**
	 * 是否启动检查更新
	 * 
	 * @return
	 */
	public boolean isCheckUp() {
		String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
		// 默认是开启
		if (StringUtils.isEmpty(perf_checkup))
			return true;
		else
			return StringUtils.toBool(perf_checkup);
	}

	/**
	 * 设置启动检查更新
	 * 
	 * @param b
	 */
	public void setConfigCheckUp(boolean b) {
		setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
	}

	/**
	 * 检测当前系统声音是否为正常模式
	 * 
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * 应用程序是否发出提示音
	 * 
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}
	
	/**
	 * 是否接收通知
	 * @return
	 */
	public boolean isReceiveNotice() {
		String perf_notice = getProperty(AppConfig.CONF_RECEIVENOTICE);
		// 默认是开启提示声音
		if (StringUtils.isEmpty(perf_notice)) {
			return true;
		} else {
			return StringUtils.toBool(perf_notice);
		}
	}
	
	/**
	 * 设置是否接收通知
	 */
	public void setConfigReceiveNotice(boolean isReceiveNotice) {
		setProperty(AppConfig.CONF_RECEIVENOTICE, String.valueOf(isReceiveNotice));
	}

	/**
	 * 是否发出提示音
	 * 
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		// 默认是开启提示声音
		if (StringUtils.isEmpty(perf_voice)) {
			return true;
		} else {
			return StringUtils.toBool(perf_voice);
		}
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 用户登录
	 * 
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 * @throws IOException
	 */
	public User loginVerify(String account, String pwd) throws AppException {
		User user = ApiClient.login(this, account, pwd);
		if (null != user) {
			// 保存登录用户的信息
			saveLoginInfo(user);
		}
		return user;
	}

	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public User getLoginInfo() {
		User user = new User();
		user.setId(getProperty(PROP_KEY_UID));
		user.setUsername(getProperty(PROP_KEY_USERNAME));
		user.setName(getProperty(PROP_KEY_NAME));
		user.setBio(getProperty(PROP_KEY_BIO));
		user.setWeibo(getProperty(PROP_KEY_WEIBO));
		user.setBlog(getProperty(PROP_KEY_BLOG));
		user.setTheme_id(StringUtils.toInt(getProperty(PROP_KEY_THEME_ID), 1));
		user.setState(getProperty(PROP_KEY_STATE));
		user.setCreated_at(getProperty(PROP_KEY_CREATED_AT));
		user.setPortrait(getProperty(PROP_KEY_PORTRAIT));
		user.setIsAdmin(StringUtils.toBool(getProperty(PROP_KEY_IS_ADMIN)));
		user.setCanCreateGroup(StringUtils
				.toBool(getProperty(PROP_KEY_CAN_CREATE_GROUP)));
		user.setCanCreateProject(StringUtils
				.toBool(getProperty(PROP_KEY_CAN_CREATE_PROJECT)));
		user.setCanCreateTeam(StringUtils
				.toBool(getProperty(PROP_KEY_CAN_CREATE_TEAM)));
		Follow follow = new Follow();
		follow.setFollowers(StringUtils.toInt(getProperty(ROP_KEY_FOLLOWERS)));
		follow.setStarred(StringUtils.toInt(getProperty(ROP_KEY_STARRED)));
		follow.setFollowing(StringUtils.toInt(getProperty(ROP_KEY_FOLLOWING)));
		follow.setWatched(StringUtils.toInt(getProperty(ROP_KEY_WATCHED)));
		user.setFollow(follow);
		return user;
	}
	
	/**
	 * 保存用户的email和pwd
	 * @param email
	 * @param pwd
	 */
	public void saveAccountInfo(String email, String pwd) {
		setProperty(ACCOUNT_EMAIL, email);
		setProperty(ACCOUNT_PWD, pwd);
	}

	/**
	 * 保存登录用户的信息
	 * 
	 * @param user
	 */
	@SuppressWarnings("serial")
	private void saveLoginInfo(final User user) {
		if (null == user) {
			return;
		}
		// 保存用户的信息
		this.loginUid = StringUtils.toInt(user.getId());
		this.login = true;
		setProperties(new Properties() {
			{
				setProperty(PROP_KEY_UID, String.valueOf(user.getId()));
				setProperty(PROP_KEY_USERNAME, String.valueOf(user.getUsername()));
				setProperty(PROP_KEY_NAME, String.valueOf(user.getName()));
				setProperty(PROP_KEY_BIO, String.valueOf(user.getBio()));// 个人介绍
				setProperty(PROP_KEY_WEIBO, String.valueOf(user.getWeibo()));
				setProperty(PROP_KEY_BLOG, String.valueOf(user.getBlog()));
				setProperty(PROP_KEY_THEME_ID, String.valueOf(user.getTheme_id()));
				setProperty(PROP_KEY_STATE, String.valueOf(user.getState()));
				setProperty(PROP_KEY_CREATED_AT, String.valueOf(user.getCreated_at()));
				setProperty(PROP_KEY_PORTRAIT, String.valueOf(user.getPortrait()));// 个人头像
				setProperty(PROP_KEY_IS_ADMIN, String.valueOf(user.isIsAdmin()));
				setProperty(PROP_KEY_CAN_CREATE_GROUP, String.valueOf(user.isCanCreateGroup()));
				setProperty(PROP_KEY_CAN_CREATE_PROJECT, String.valueOf(user.isCanCreateProject()));
				setProperty(PROP_KEY_CAN_CREATE_TEAM, String.valueOf(user.isCanCreateTeam()));
				setProperty(ROP_KEY_FOLLOWERS, String.valueOf(user.getFollow().getFollowers()));
				setProperty(ROP_KEY_STARRED, String.valueOf(user.getFollow().getStarred()));
				setProperty(ROP_KEY_FOLLOWING, String.valueOf(user.getFollow().getFollowing()));
				setProperty(ROP_KEY_WATCHED, String.valueOf(user.getFollow().getWatched()));
			}
		});
	}

	/**
	 * 清除登录信息，用户的私有token也一并清除
	 */
	private void cleanLoginInfo() {
		this.loginUid = 0;
		this.login = false;
		removeProperty(PROP_KEY_PRIVATE_TOKEN, PROP_KEY_UID, PROP_KEY_USERNAME, PROP_KEY_EMAIL,
				PROP_KEY_NAME, PROP_KEY_BIO, PROP_KEY_WEIBO, PROP_KEY_BLOG,
				PROP_KEY_THEME_ID, PROP_KEY_STATE, PROP_KEY_CREATED_AT,
				PROP_KEY_PORTRAIT, PROP_KEY_IS_ADMIN,
				PROP_KEY_CAN_CREATE_GROUP, PROP_KEY_CAN_CREATE_PROJECT,
				PROP_KEY_CAN_CREATE_TEAM, ROP_KEY_FOLLOWERS, ROP_KEY_STARRED,
				ROP_KEY_FOLLOWING, ROP_KEY_WATCHED);
	}

	/**
	 * 用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}

	/**
	 * 获取登录用户id
	 * 
	 * @return
	 */
	public int getLoginUid() {
		return this.loginUid;
	}

	/**
	 * 用户注销
	 */
	public void loginout() {
		ApiClient.cleanToken();
		// 清除已登录用户的信息
		cleanLoginInfo();
		this.login = false;
		this.loginUid = 0;
		// 发送广播通知
		BroadcastController.sendUserChangeBroadcase(this);
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		deleteDatabase("webview.db");
		deleteDatabase("webview.db-shm");
		deleteDatabase("webview.db-wal");
		deleteDatabase("webviewCache.db");
		deleteDatabase("webviewCache.db-shm");
		deleteDatabase("webviewCache.db-wal");
		// 清除数据缓存
		clearCacheFolder(getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(getCacheDir(), System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
					System.currentTimeMillis());
		}
		// 清除编辑器保存的临时内容
		Properties props = getProperties();
		for (Object key : props.keySet()) {
			String _key = key.toString();
			if (_key.startsWith("temp"))
				removeProperty(_key);
		}
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}
	
	/**
	 * 获得一个项目信息
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public Project getProject(String projectId) throws AppException {
		return ApiClient.getProject(this, projectId);
	}
	
	/**
	 * 获得具体用户的项目列表
	 * @param userId
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public CommonList<Project> getUserProjects(String userId, int page) throws AppException {
		return ApiClient.getUserProjects(this, userId, page);
	}

	/**
	 * 获得最近更新的项目
	 * 
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getExploreLatestProject(int page,
			boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "latestProjectList_" + page + "_" + PAGE_SIZE;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getExploreLatestProject(this, page);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				e.printStackTrace();
				list = (CommonList<Project>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Project>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Project>();
		}
		return list;
	}

	/**
	 * 获取热门项目
	 * 
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getExplorePopularProject(int page,
			boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "popularProjectList_" + page + "_" + PAGE_SIZE;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getExplorePopularProject(this, page);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				e.printStackTrace();
				list = (CommonList<Project>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Project>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Project>();
		}
		return list;
	}

	/**
	 * 获取推荐项目
	 * 
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getExploreFeaturedProject(int page,
			boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "faturedProjectList_" + page + "_" + PAGE_SIZE;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getExploreFeaturedProject(this, page);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				e.printStackTrace();
				list = (CommonList<Project>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Project>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Project>();
		}
		return list;
	}
	
	/**
	 * 获得查询项目的结果
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public List<Project> getSearcheProject(String query, int page) throws AppException {
		return ApiClient.getSearcheProject(this, query, page);
	}

	/**
	 * 获得个人动态列表
	 * 
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Event> getMySelfEvents(int page, boolean isRefresh)
			throws AppException {
		CommonList<Event> list = null;
		String cacheKey = "myselfEventsList_" + +page + "_" + PAGE_SIZE;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getMySelfEvents(this, page);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				e.printStackTrace();
				list = (CommonList<Event>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Event>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Event>();
		}
		return list;
	}

	/**
	 * 获得个人的所有项目
	 * 
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getMySelfProjectList(int page, boolean isRefresh)
			throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "myselfProjectList_" + page + "_" + PAGE_SIZE;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getMySelfProjectList(this, page);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				e.printStackTrace();
				list = (CommonList<Project>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Project>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Project>();
		}
		return list;
	}
	
	/**
	 * 获得一个项目的commit列表
	 * 
	 * @param projectId
	 * @param pageIndex
	 * @param isRefresh
	 * @param ref_name
	 *            分支（optional）
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Commit> getProjectCommitList(int projectId, int page,
			boolean isRefresh, String ref_name) throws AppException {
		CommonList<Commit> list = null;
		String cacheKey = "projectCommitList_" + projectId + "_" + page + "_"
				+ PAGE_SIZE;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getProjectCommitList(this, projectId, page,
						ref_name);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				e.printStackTrace();
				list = (CommonList<Commit>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Commit>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Commit>();
		}
		return list;
	}
	
	/**
	 * 获取具体用户的最近动态列表
	 * @param user_id
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public CommonList<Event> getUserEvents(String user_id ,int page) throws AppException {
		return ApiClient.getUserEvents(this, user_id, page);
	}

	/**
	 * 获得项目的代码树列表
	 * 
	 * @param appContext
	 * @param projectId
	 *            项目的id
	 * @param path
	 *            (optional) 路径(可以拿到项目文件夹下的文件树)
	 * @param ref_name
	 *            (optional) 分支或者标签，空则为默认的master分支
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<CodeTree> getProjectCodeTree(int projectId, String path,
			String ref_name, boolean isRefresh) throws Exception {
		CommonList<CodeTree> list = null;
		String pathKey = path;
		if (pathKey.contains("/")) {
			pathKey = pathKey.replaceAll("/", ".");
		}
		String cacheKey = "projectCodeLsit_" + projectId + "_" + pathKey + "_"
				+ ref_name;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getProjectCodeTree(this, projectId, path,
						ref_name);
				if (list != null) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				e.printStackTrace();
				list = (CommonList<CodeTree>) readObject(cacheKey);
				if (list == null)
					throw e;
			} catch (Exception e) {
				throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<CodeTree>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<CodeTree>();
		}
		return list;
	}

	/**
	 * 获得一个项目issues列表
	 * 
	 * @param appContext
	 * @param projectId
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Issue> getProjectIssuesList(int projectId, int page,
			boolean isRefresh) throws AppException {
		CommonList<Issue> list = null;
		String cacheKey = "projectIssuesList_" + projectId + "_" + page + "_"
				+ PAGE_SIZE;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getProjectIssuesList(this, projectId, page);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				e.printStackTrace();
				list = (CommonList<Issue>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Issue>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Issue>();
		}
		return list;
	}
	
	/**
	 * 获得issue详情
	 * @param projectId
	 * @param issueId
	 * @return
	 * @throws AppException
	 */
	public Issue getIssue(String projectId, String issueId) throws AppException {
		return ApiClient.getIssue(this, projectId, issueId);
	}

	/**
	 * 获得issue的评论列表
	 * 
	 * @param appContext
	 * @param projectId
	 * @param noteId
	 * @param page
	 * @param isRefresh
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public CommonList<GitNote> getIssueCommentList(String projectId,
			String issueId, int page, boolean isRefresh) throws Exception {
		CommonList<GitNote> list = null;
		String cacheKey = "issueCommentList" + projectId + "_" + issueId + "_"
				+ page;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getIssueCommentList(this, projectId, issueId,
						page);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (Exception e) {
				e.printStackTrace();
				list = (CommonList<GitNote>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<GitNote>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<GitNote>();
		}
		return list;
	}

	/**
	 * 提交commit评论
	 * 
	 * @param projectId
	 * @param issueId
	 * @param body
	 * @return
	 * @throws AppException
	 */
	public GitNote pubIssueComment(String projectId, String issueId, String body)
			throws AppException {
		return ApiClient.pubIssueComment(this, projectId, issueId, body);
	}

	@SuppressWarnings("unchecked")
	public CommonList<Branch> getProjectBranchsOrTagsLsit(String projectId,
			int page, String branchOrTag, boolean isRefresh) throws Exception {
		CommonList<Branch> list = null;
		String cacheKey = "projectBranchsOrTagsLsit_" + projectId + "_" + page
				+ "_" + branchOrTag;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getProjectBranchsOrTagsLsit(this, projectId,
						page, branchOrTag);
				if (list != null && page == 1) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (Exception e) {
				e.printStackTrace();
				list = (CommonList<Branch>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Branch>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Branch>();
		}
		return list;
	}

	/**
	 * 获得代码文件详情
	 * 
	 * @param projectId
	 *            项目的id
	 * @param file_path
	 *            文件的路径
	 * @param ref
	 *            分支或者标签
	 * @return
	 * @throws Exception
	 */
	public CodeFile getCodeFile(String projectId, String file_path, String ref)
			throws Exception {
		CodeFile codeFile = null;
		try {
			codeFile = ApiClient.getCodeFile(this, projectId, file_path, ref);
		} catch (AppException e) {
			e.printStackTrace();
			if (codeFile == null)
				throw e;
		} catch (Exception e) {
			throw e;
		}
		return codeFile;
	}
	
	/**
	 * 获得一个项目的readme文件内容
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public ReadMe getReadMeFile(String projectId) throws AppException {
		return ApiClient.getReadMeFile(this, projectId);
	}

	/**
	 * 获得commit文件的diff
	 * 
	 * @param projectId
	 * @param commitId
	 * @param isRefresh
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public CommonList<CommitDiff> getCommitDiffList(String projectId,
			String commitId, boolean isRefresh) throws Exception {
		CommonList<CommitDiff> list = null;
		String cacheKey = "CommitDiffLsit_" + projectId + "_" + commitId;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getCommitDiffList(this, projectId, commitId);
				if (list != null) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (Exception e) {
				e.printStackTrace();
				list = (CommonList<CommitDiff>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<CommitDiff>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<CommitDiff>();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public CommonList<Comment> getCommitCommentList(String projectId,
			String commitId, boolean isRefresh) throws AppException {
		CommonList<Comment> list = null;
		String cacheKey = "CommitCommentLsit_" + projectId + "_" + commitId;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getCommitCommentList(this, projectId,
						commitId, isRefresh);
				if (list != null) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (Exception e) {
				e.printStackTrace();
				list = (CommonList<Comment>) readObject(cacheKey);
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Comment>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Comment>();
		}
		return list;
	}

	/**
	 * 通过commits获取代码文件的内容
	 * 
	 * @param appContext
	 * @param projectId
	 * @param commitId
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public String getCommitFileDetail(String projectId, String commitId,
			String filePath) throws Exception {
		return ApiClient.getCommitFileDetail(this, projectId, commitId,
				filePath);
	}

	/**
	 * 获得项目的参与成员
	 * 
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public List<User> getProjectMembers(String projectId) throws AppException {
		return ApiClient.getProjectMembers(this, projectId);
	}

	/**
	 * 获得项目的里程碑
	 * 
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public List<Milestone> getProjectMilestone(String projectId)
			throws AppException {
		return ApiClient.getProjectMilestone(this, projectId);
	}

	/**
	 * 创建一个issue
	 * 
	 * @param projectId
	 *            项目ID
	 * @param title
	 *            标题
	 * @param description
	 *            描述
	 * @param assignee_id
	 *            被指派人的ID
	 * @param milestone_id
	 *            里程碑的ID
	 * @return
	 * @throws AppException
	 */
	public Issue pubCreateIssue(String projectId, String title,
			String description, String assignee_id, String milestone_id)
			throws AppException {
		return ApiClient.pubCreateIssue(this, projectId, title, description,
				assignee_id, milestone_id);
	}

	/**
	 * 上传文件
	 * 
	 * @param files
	 * @return
	 * @throws AppException
	 */
	public UpLoadFile upLoad(File file) throws AppException {
		return ApiClient.upLoadFile(this, file);
	}
	
	/**
	 * 获得通知
	 * @param filter
	 * @param all
	 * @param project_id
	 * @return
	 * @throws AppException
	 */
	public CommonList<ProjectNotificationArray> getNotification(String filter,
			String all, String project_id) throws AppException {
		return ApiClient.getNotification(this, filter, all, project_id);
	}
	
	/**
	 * 设置通知为已读
	 * @param appContext
	 * @param notificationId
	 * @return
	 * @throws AppException
	 */
	public NotificationReadResult setNotificationIsRead(String notificationId) throws AppException {
		return ApiClient.setNotificationIsRead(this, notificationId);
	}
	
	/**
	 * 获取App更新信息
	 * @return
	 * @throws AppException
	 */
	public Update getUpdateInfo() throws AppException {
		return ApiClient.getUpdateInfo(this);
	}
	
	/**
	 * 获得语言列表
	 * @return
	 * @throws AppException
	 */
	public CommonList<Language> getLanguageList() throws AppException {
		CommonList<Language> list = null;
		String cacheKey = "languages_list";
		if (!isReadDataCache(cacheKey)) {
			try {
				list = ApiClient.getLanguageList(this);
				if (list != null) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (Exception e) {
				e.printStackTrace();
				list = (CommonList<Language>) readObject(cacheKey);
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Language>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Language>();
		}
		return list;
	}
	
	/**
	 * 根据语言的ID来获得项目的列表
	 * @param languageId
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public List<Project> getLanguageProjectList(String languageId, int page) throws AppException {
		return ApiClient.getLanguageProjectList(this, languageId, page);
	}
	
	/**
	 * 获得一个随机的项目
	 * @return
	 * @throws AppException
	 */
	public RandomProject getRandomProject() throws AppException {
		return ApiClient.getRandomProject(this);
	}
	
	/**
	 * 更新代码库中的代码文件
	 * @param appContext
	 * @param project_id
	 * @param branch_name
	 * @param content
	 * @param commit_message
	 * @return
	 * @throws AppException
	 */
	public String updateRepositoryFiles(String project_id, String ref, String file_path, String branch_name, String content, String commit_message) throws AppException {
		return ApiClient.updateRepositoryFiles(this, project_id, ref, file_path, branch_name, content, commit_message);
	}
	
	/**
	 * 获得某个用户的star列表
	 * @param user_id
	 * @param page
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getStarProjectList(String user_id,
			int page, boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "StarProjectList_" + user_id;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getStarProjectList(this, user_id, page, isRefresh);
				if (list != null) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (Exception e) {
				e.printStackTrace();
				list = (CommonList<Project>) readObject(cacheKey);
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Project>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Project>();
		}
		return list;
	}
	
	/**
	 * 获得某个用户的watch列表
	 * @param user_id
	 * @param page
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getWatchProjectList(String user_id,
			int page, boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		String cacheKey = "WatchProjectList_" + user_id;
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				list = ApiClient.getWatchProjectList(this, user_id, page, isRefresh);
				if (list != null) {
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (Exception e) {
				e.printStackTrace();
				list = (CommonList<Project>) readObject(cacheKey);
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Project>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Project>();
		}
		return list;
	}
}
