package net.oschina.gitapp.ui;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.AppManager;
import net.oschina.gitapp.R;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.ProjectNotificationArray;
import net.oschina.gitapp.common.DoubleClickExitHelper;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.common.UpdateManager;
import net.oschina.gitapp.interfaces.*;
import net.oschina.gitapp.ui.fragments.ExploreViewPagerFragment;
import net.oschina.gitapp.ui.fragments.MySelfViewPagerFragment;
import net.oschina.gitapp.widget.BadgeView;

/**
 * 程序主界面
 * @created 2014-04-29
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：2014-05-29
 * 更新内容：更改以callBack的方式进行交互
 * 更新者：火蚁
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends ActionBarActivity implements
		DrawerMenuCallBack {
	
	static final String DRAWER_MENU_TAG = "drawer_menu";
	static final String DRAWER_CONTENT_TAG = "drawer_content";
	

	static final String CONTENT_TAG_EXPLORE = "content_explore";
	static final String CONTENT_TAG_MYSELF = "content_myself";

	static final String CONTENTS[] = {
		CONTENT_TAG_EXPLORE, 
		CONTENT_TAG_MYSELF 
	};
	
	static final String FRAGMENTS[] = {
		ExploreViewPagerFragment.class.getName(),
		MySelfViewPagerFragment.class.getName()
	};
	
	final String TITLES[] = {
		"发现",
		"我的"
	};
	
	private static DrawerNavigationMenu mMenu = DrawerNavigationMenu.newInstance();
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private FragmentManager mFragmentManager;
	private DoubleClickExitHelper mDoubleClickExitHelper;

	// 当前显示的界面标识
	private String mCurrentContentTag;
	private ActionBar mActionBar;
	private AppContext mContext;
	
	private static String mTitle;// actionbar标题
	
	public static BadgeView mNotificationBadgeView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = (AppContext) getApplicationContext();
		initView(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		// 检查新版本
		if (mContext.isCheckUp()) {
			UpdateManager.getUpdateManager().checkAppUpdate(this, false);
		}
		// 启动轮询获取通知信息
		if (mContext.isReceiveNotice()) {
			foreachUserNotice();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTitle != null) {
			mActionBar.setTitle(mTitle);
		}
		if (mCurrentContentTag != null && mContext !=null && mMenu != null) {
			if (mCurrentContentTag.equalsIgnoreCase(CONTENTS[1])) {
				if (!mContext.isLogin()) {
					onClickExplore();
					mMenu.highlightExplore();
				}
			}
		}
	}

	private void initView(Bundle savedInstanceState) {
		
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		
		mDoubleClickExitHelper = new DoubleClickExitHelper(this);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerListener(new DrawerMenuListener());
		// 设置滑出菜单的阴影效果
		//mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, 0, 0);

		
		mFragmentManager = getSupportFragmentManager();
		if (null == savedInstanceState) {
			setExploreShow();
		}
	}
	
	private void setExploreShow() {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.main_slidingmenu_frame,
				mMenu, DRAWER_MENU_TAG)
				.replace(R.id.main_content,
						ExploreViewPagerFragment.newInstance(),
						DRAWER_CONTENT_TAG).commit();

		mTitle = "发现";
		mActionBar.setTitle(mTitle);
		mCurrentContentTag = CONTENT_TAG_EXPLORE;
	}
	
	/**
	 * 轮询通知信息
	 */
	private void foreachUserNotice() {
		final boolean isLogin = mContext.isLogin();
		final Handler handler = new Handler() {
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					CommonList<ProjectNotificationArray> commonList = (CommonList<ProjectNotificationArray>) msg.obj;
					int count = 0;
					for (ProjectNotificationArray pna : commonList.getList()) {
						count += pna.getProject().getNotifications().size();
					}
					UIHelper.sendBroadCast(MainActivity.this, count);
				}
				foreachUserNotice();// 回调
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					sleep(60 * 1000);
					if (isLogin) {
						msg.obj = mContext.getNotification("", "", "");
						msg.what = 1;
					} else {
						msg.what = 0;
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.main_actionbar_menu_search:
			UIHelper.showSearch(mContext);
			return true;
		case R.id.main_actionbar_menu_notification:
			onClickNotice();
			return true;
		default:
			break;
		}
		return mDrawerToggle.onOptionsItemSelected(item)
				|| super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 判断菜单是否打开
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawers();
				return true;
			}
			return mDoubleClickExitHelper.onKeyDown(keyCode, event);
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawers();
				return true;
			} else {
				mDrawerLayout.openDrawer(Gravity.START);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 显示内容*/
	private void showMainContent(int pos) {
		//关闭侧边栏菜单
		mDrawerLayout.closeDrawers();
		//获取当前的tag
		String tag = CONTENTS[pos];
		//如果获得的tag 就是当前的tag，那么返回，什么也不做
		if (tag.equalsIgnoreCase(mCurrentContentTag)) return;
		// FragmentTransaction对fragment进行添加,移除,替换,以及执行其他动作。
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		if(mCurrentContentTag != null) {
			Fragment fragment = mFragmentManager.findFragmentByTag(mCurrentContentTag);
			//这儿为什么要移除 Fragment。
			if(fragment != null) {
				ft.remove(fragment);
			}
		}
		//instantiate 这个是干嘛的？
		ft.replace(R.id.main_content, Fragment.instantiate(this, FRAGMENTS[pos]), tag);
		ft.commit();
		
		mActionBar.setTitle(TITLES[pos]);
		mTitle = mActionBar.getTitle().toString();//记录主界面的标题
		mCurrentContentTag = tag;
	}
	
	//回调方法用于：触发菜单栏中的各个条目
	private void showLoginActivity() {
		if (!mContext.isLogin()) {
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivity(intent);
		} else {
			UIHelper.showMySelfInfoDetail(MainActivity.this);
		}
	}
	
	@Override
	public void onClickLogin() {
		showLoginActivity();
	}

	@Override
	public void onClickExplore() {
		showMainContent(0);
	}

	@Override
	public void onClickMySelf() {
		if (!mContext.isLogin()) {
			UIHelper.showLoginActivity(this);
			return;
		} else {
			showMainContent(1);
		}
	}

	public void onClickNotice() {
		if (!mContext.isLogin()) {
			UIHelper.showLoginActivity(this);
			return;
		}
		Intent intent = new Intent(mContext, NotificationActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onClickLanguage() {
		Intent intent = new Intent(mContext, LanguageActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onClickShake() {
		Intent intent = new Intent(mContext, ShakeActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onClickSetting() {
		Intent intent = new Intent(mContext, SettingActivity.class);
		startActivity(intent);
	}

	@Override
	public void onClickExit() {
		this.finish();
	}
	
	private class DrawerMenuListener implements DrawerLayout.DrawerListener {
		@Override
		public void onDrawerOpened(View drawerView) {
			mDrawerToggle.onDrawerOpened(drawerView);
		}

		@Override
		public void onDrawerClosed(View drawerView) {
			mDrawerToggle.onDrawerClosed(drawerView);
		}

		@Override
		public void onDrawerSlide(View drawerView, float slideOffset) {
			mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
		}

		@Override
		public void onDrawerStateChanged(int newState) {
			mDrawerToggle.onDrawerStateChanged(newState);
		}
	}
}
