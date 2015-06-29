package net.oschina.gitapp.ui;

import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.text.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.bean.MoreMenuItem;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.StarWatchOptionResult;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.widget.DropDownMenu;

/**
 * 项目详情界面
 * 
 * @created 2014-05-26 上午10：26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * @最后更新：
 * @更新内容：
 * @更新者：
 */
@SuppressWarnings("deprecation")
public class ProjectActivity extends BaseActionBarActivity implements
		OnClickListener {
	
	private final int MORE_MENU_SHARE = 00;// 分享
	private final int MORE_MENU_COPY_LINK = 01;// 复制链接
	private final int MORE_MENU_OPEN_WITH_BROWS = 02;// 在浏览器中打开
	
	private final int ACTION_LOAD_PROJECT = 0;// 加载项目
	private final int ACTION_LOAD_PARENT_PROJECT = 1;// 加载项目的父项目信息
	
	private Project mProject;
	
	private String projectId;

	private ProgressBar mLoading;
	
	private ScrollView mContent;
	
	private TextView mProjectName;
	
	private TextView mUpdateTime;
	
	private ImageView mFlag;
	
	private TextView mDescription;
	
	private TextView mStarNum;
	
	private ImageView mStarStared;// 项目的star状态
	
	private TextView mWatchNum;
	
	private ImageView mWatchStared;// 项目的Watch状态
	
	private TextView mCreated;
	
	private TextView mForkNum;
	
	private TextView mLocked;
	
	private TextView mLanguage;
	
	private TextView mOwnerName;
	
	private View mForkView;
	
	private TextView mForkMes;
	
	private AppContext mAppContext;
	
	private LinearLayout mLLStar;
	private LinearLayout mLLWatch;
	private LinearLayout mLLOwner;
	private LinearLayout mLLReadMe;
	private LinearLayout mLLCodes;
	private LinearLayout mLLCommits;
	private LinearLayout mLLIssues;
	
	private DropDownMenu mMoreMenuWindow;
	
	private List<MoreMenuItem> mMoreItems = new ArrayList<MoreMenuItem>();
	
	private String url_link = null;
	
	private Bitmap bitmap;
	
	private View.OnClickListener onMoreMenuItemClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mMoreMenuWindow != null && mMoreMenuWindow.isShowing()) {
				mMoreMenuWindow.dismiss();
			}
			if (mProject == null) {
				return;
			}
			int id = v.getId();
			switch (id) {
			case MORE_MENU_SHARE:
				UIHelper.showShareOption(ProjectActivity.this, mProject.getName(), url_link , 
						"我在关注《" + mProject.getOwner().getName() + "的项目" + mProject.getName() + "》" + "，你也来瞧瞧呗！", bitmap);
				break;
			case MORE_MENU_COPY_LINK:
				ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				cbm.setText(url_link);
				UIHelper.ToastMessage(mAppContext, "已复制到剪贴板");
				break;
			case MORE_MENU_OPEN_WITH_BROWS:
				UIHelper.openBrowser(ProjectActivity.this, url_link);
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		mAppContext = getGitApplication();
		initView();
	}

	private void initView() {
		// 拿到传过来的project对象
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		projectId = intent.getStringExtra(Contanst.PROJECTID);

		mLoading = (ProgressBar) findViewById(R.id.project_detail_loading);
		mContent = (ScrollView) findViewById(R.id.project_content);
		mProjectName = (TextView) findViewById(R.id.project_name);
		mUpdateTime = (TextView) findViewById(R.id.project_update);
		mFlag = (ImageView) findViewById(R.id.project_flag);
		mDescription = (TextView) findViewById(R.id.project_description);
		mStarNum = (TextView) findViewById(R.id.project_starnum);
		mStarStared = (ImageView) findViewById(R.id.project_star_stared);
		mWatchNum = (TextView) findViewById(R.id.project_watchnum);
		mWatchStared = (ImageView) findViewById(R.id.project_watch_stared);
		mCreated = (TextView) findViewById(R.id.project_createed);
		mForkNum = (TextView) findViewById(R.id.project_forknum);
		mLocked = (TextView) findViewById(R.id.project_locked);
		mLanguage = (TextView) findViewById(R.id.project_language);
		mOwnerName = (TextView) findViewById(R.id.project_ownername);
		mForkView = findViewById(R.id.project_ll_fork);
		mForkMes = (TextView) findViewById(R.id.project_fork_form);
		
		mLLStar = (LinearLayout) findViewById(R.id.project_star);
		mLLWatch = (LinearLayout) findViewById(R.id.project_watch);
		mLLOwner = (LinearLayout) findViewById(R.id.project_owner);
		mLLReadMe = (LinearLayout) findViewById(R.id.project_readme);
		mLLCodes = (LinearLayout) findViewById(R.id.project_issues);
		mLLCommits = (LinearLayout) findViewById(R.id.project_commits);
		mLLIssues = (LinearLayout) findViewById(R.id.project_code);
		
		mLLStar.setOnClickListener(this);
		mLLWatch.setOnClickListener(this);
		mLLOwner.setOnClickListener(this);
		mLLReadMe.setOnClickListener(this);
		mLLCodes.setOnClickListener(this);
		mLLCommits.setOnClickListener(this);
		mLLIssues.setOnClickListener(this);
		if (null == mProject) {
			loadProject(ACTION_LOAD_PROJECT, projectId);
		} else {
			initData();
			initMoreMenu();
		}
	}

	private void initData() {
		mActionBar.setTitle(mProject.getName());
		mActionBar.setSubtitle(mProject.getOwner().getName());
		
		mProjectName.setText(mProject.getName());
		mUpdateTime.setText("更新于 " + getUpdateTime());
		setFlag();
		
		mDescription.setText(getDescription(mProject.getDescription()));
		mStarNum.setText(mProject.getStars_count() + "");
		setStared(mProject.isStared());
		mWatchNum.setText(mProject.getWatches_count() + "");
		setWatched(mProject.isWatched());
		mCreated.setText(StringUtils.friendly_time(mProject.getCreatedAt()));
		mForkNum.setText(mProject.getForks_count() + "");
		mLocked.setText(getLocked());
		mLanguage.setText(getLanguage());
		mOwnerName.setText(mProject.getOwner().getName());
		
		// 显示是否有fork信息
		initForkMess();
		
		// 记录项目的地址链接：
		url_link = URLs.URL_HOST + mProject.getOwner().getUsername() + URLs.URL_SPLITTER + mProject.getPath();
		// 截取屏幕
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (bitmap == null) {
					bitmap = UIHelper.takeScreenShot(ProjectActivity.this);
				}
			}
		}, 500);
	}
	
	private void setStared(boolean stared) {
		if (stared) {
			mStarStared.setBackgroundResource(R.drawable.project_star);
			((TextView)findViewById(R.id.project_star_text)).setText("unstar");
		} else {
			mStarStared.setBackgroundResource(R.drawable.project_unstar);
			((TextView)findViewById(R.id.project_star_text)).setText("star");
		}
	}
	
	private void setWatched(boolean watched) {
		if (watched) {
			mWatchStared.setBackgroundResource(R.drawable.project_watch);
			((TextView)findViewById(R.id.project_watch_text)).setText("unwatch");
		} else {
			mWatchStared.setBackgroundResource(R.drawable.project_unwatch);
			((TextView)findViewById(R.id.project_watch_text)).setText("watch");
		}
	}
	
	private void setFlag() {
		// 判断项目的类型，显示不同的图标（私有项目、公有项目、fork项目）   
		if (mProject.getParent_id() != null) {
			mFlag.setBackgroundResource(R.drawable.project_flag_fork);
		} else if (mProject.isPublic()) {
			mFlag.setBackgroundResource(R.drawable.project_flag_public);
		} else {
			mFlag.setBackgroundResource(R.drawable.project_flag_private);
		}
	}
	
	private void initMoreMenu() {
		mMoreMenuWindow = new DropDownMenu(ProjectActivity.this, onMoreMenuItemClick);
		MoreMenuItem share = new MoreMenuItem(MORE_MENU_SHARE, R.drawable.more_menu_icon_share, "分享项目");
		mMoreItems.add(share);
		
		MoreMenuItem copy_link = new MoreMenuItem(MORE_MENU_COPY_LINK, R.drawable.more_menu_icon_copy, "复制项目链接");
		mMoreItems.add(copy_link);
		
		MoreMenuItem open_with_brows = new MoreMenuItem(MORE_MENU_OPEN_WITH_BROWS, R.drawable.more_menu_icon_browser, "在浏览器中打开");
		mMoreItems.add(open_with_brows);
		
		mMoreMenuWindow.addItems(mMoreItems);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.projet_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mProject == null) {
			return false;
		}
		int id = item.getItemId();
		switch (id) {
		case R.id.project_menu_more:
			showMoreOptionMenu();
			break;
		case R.id.project_menu_create_issue:
			// 新增issue
			UIHelper.showIssueEditOrCreate(getGitApplication(), mProject, null);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showMoreOptionMenu() {
		if (mMoreMenuWindow == null) {
			return;
		}
		View v = findViewById(R.id.project_menu_more);
		int x = mMoreMenuWindow.getWidth() - v.getWidth() + 20;
		
		mMoreMenuWindow.showAsDropDown(v, -x, 0);
	}

	private String getUpdateTime() {
		if (mProject.getLast_push_at() !=null) {
			return StringUtils.friendly_time(mProject.getLast_push_at());
		} else {
			return StringUtils.friendly_time(mProject.getCreatedAt());
		}
	}
	
	private String getDescription(String description) {
		if (description == null || StringUtils.isEmpty(description)) {
			return "该项目暂无简介！";
		} else {
			return description;
		}
	}
	
	private String getLocked() {
		if (mProject.isPublic()) {
			return "Public";
		} else {
			return "Private";
		}
	}
	
	private String getLanguage() {
		if (mProject.getLanguage() == null) {
			return "未指定";
		} else {
			return mProject.getLanguage();
		}
	}
	
	private void initForkMess() {
		if (mProject.getParent_id() == null) {
			return;
		} else {
			mForkView.setVisibility(View.VISIBLE);
			findViewById(R.id.project_fork_ll_line).setVisibility(View.VISIBLE);
			loadProject(ACTION_LOAD_PARENT_PROJECT, mProject.getParent_id() + "");
			mForkView.setOnClickListener(this);
		}
	}

	private void loadProject(final int action, final String projectId) {
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();

				try {
					msg.obj = mAppContext.getProject(projectId);
					msg.what = 1;
				} catch (AppException e) {
					mLoading.setVisibility(View.GONE);
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (action == ACTION_LOAD_PROJECT) {
					mLoading.setVisibility(View.VISIBLE);
					mContent.setVisibility(View.GONE);
				}
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				if (action == ACTION_LOAD_PROJECT) {
					mLoading.setVisibility(View.GONE);
					mContent.setVisibility(View.VISIBLE);
				}
				if (msg != null) {
					if (msg.what == 1) {
						if (action == ACTION_LOAD_PROJECT) {
							mProject = (Project) msg.obj;
							initData();
							if (mMoreMenuWindow == null) {
								initMoreMenu();
							}
						} else {
							Project p = (Project) msg.obj;
							if (p != null) {
								mForkMes.setText(p.getOwner().getName() + " / " + p.getName());
							}
						}
					} else {
						if (action == ACTION_LOAD_PROJECT) {
							if (msg.obj instanceof AppException) {
								((AppException) msg.obj).makeToast(mAppContext);
							}
						}
					}
				}
			}

		}.execute();
	}

	@Override
	public void onClick(View v) {
		
		if (mProject == null ) {
			return;
		}
		int id = v.getId();
		switch (id) {
		case R.id.project_star:
			starOption();
			break;
		case R.id.project_watch:
			watchOption();
			break;
		case R.id.project_owner:
			if (mProject.getOwner() != null) {
				UIHelper.showUserInfoDetail(ProjectActivity.this, mProject.getOwner(), mProject.getOwner().getId());
			}
			break;
		case R.id.project_ll_fork:
			if (mProject.getParent_id() != null) {
				UIHelper.showProjectDetail(ProjectActivity.this, null, mProject.getParent_id() + "");
			}
			break;
		case R.id.project_readme:
			UIHelper.showProjectReadMeActivity(ProjectActivity.this, mProject);
			break;
		case R.id.project_code:
			UIHelper.showProjectCodeActivity(ProjectActivity.this, mProject);
			break;
		case R.id.project_commits:
			UIHelper.showProjectListActivity(ProjectActivity.this, mProject, ProjectSomeInfoListActivity.PROJECT_LIST_TYPE_COMMITS);
			break;
		case R.id.project_issues:
			UIHelper.showProjectListActivity(ProjectActivity.this, mProject, ProjectSomeInfoListActivity.PROJECT_LIST_TYPE_ISSUES);
			break;
		}
	}
	
	private void watchOption() {
		if (mProject == null) {
			return;
		}
		if (!mAppContext.isLogin()) {
			UIHelper.showLoginActivity(ProjectActivity.this);
			return;
		}
		
		
		final ProgressDialog loadingDialog = new ProgressDialog(this);
		loadingDialog.setCanceledOnTouchOutside(false);
		if (mProject.isWatched()) {
			loadingDialog.setMessage("正在unwatch该项目...");
		} else {
			loadingDialog.setMessage("正在watch该项目...");
		}
		
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					if (mProject.isWatched()) {
						msg.obj = ApiClient.watchProject(mAppContext, mProject.getId(), "unwatch");
					} else {
						msg.obj = ApiClient.watchProject(mAppContext, mProject.getId(), "watch");
					}
					msg.what = 1;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (loadingDialog != null) {
					loadingDialog.show();
				}
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				loadingDialog.hide();
				if (msg.what == 1) {
					String resMsg = "";
					StarWatchOptionResult res = (StarWatchOptionResult) msg.obj;
					if (res.getCount() > mProject.getWatches_count()) {
						setWatched(true);
						mProject.setWatched(true);
						resMsg = "watch成功";
					} else {
						setWatched(false);
						mProject.setWatched(false);
						resMsg = "unwatch成功";
					}
					mProject.setWatches_count(res.getCount());
					mWatchNum.setText(res.getCount() + "");
					UIHelper.ToastMessage(mAppContext, resMsg);
				} else {
					((AppException)msg.obj).makeToast(mAppContext);
				}
			}
		}.execute();
	}
	
	private void starOption() {
		if (mProject == null) {
			return;
		}
		if (!mAppContext.isLogin()) {
			UIHelper.showLoginActivity(ProjectActivity.this);
			return;
		}
		final ProgressDialog loadingDialog = new ProgressDialog(this);
		loadingDialog.setCanceledOnTouchOutside(false);
		if (mProject.isStared()) {
			loadingDialog.setMessage("正在unstar该项目...");
		} else {
			loadingDialog.setMessage("正在star该项目...");
		}
		
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					if (mProject.isStared()) {
						msg.obj = ApiClient.starProject(mAppContext, mProject.getId(), "unstar");
					} else {
						msg.obj = ApiClient.starProject(mAppContext, mProject.getId(), "star");
					}
					msg.what = 1;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if (loadingDialog != null) {
					loadingDialog.show();
				}
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				loadingDialog.hide();
				if (msg.what == 1) {
					String resMsg = "";
					StarWatchOptionResult res = (StarWatchOptionResult) msg.obj;
					if (res.getCount() > mProject.getStars_count()) {
						setStared(true);
						mProject.setStared(true);
						resMsg = "star成功";
					} else {
						setStared(false);
						mProject.setStared(false);
						resMsg = "unstar成功";
					}
					mProject.setStars_count(res.getCount());
					mStarNum.setText(res.getCount() + "");
					UIHelper.ToastMessage(mAppContext, resMsg);
				} else {
					((AppException)msg.obj).makeToast(mAppContext);
				}
			}
		}.execute();
	}
}