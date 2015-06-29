package net.oschina.gitapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.interfaces.OnStatusListener;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.util.SourceEditor;

/**
 * 代码文件详情
 * 
 * @created 2014-06-13
 * @author 火蚁
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class CommitFileDetailActivity extends BaseActionBarActivity implements
		OnStatusListener {

	private final int MENU_REFRESH_ID = 0;
	private final int MENU_MORE_ID = 1;

	private Menu optionsMenu;
	
	private WebView mWebView;
	
	private ProgressBar mLoading;
	
	private SourceEditor mEditor;
	
	private Project mProject;
	
	private CommitDiff mCommitDiff;
	
	private Commit mCommit;
	
	private AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置actionbar加载动态
		setContentView(R.layout.activity_code_file_view);
		appContext = getGitApplication();
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mCommitDiff = (CommitDiff) intent.getSerializableExtra(Contanst.COMMITDIFF);
		mCommit = (Commit) intent.getSerializableExtra(Contanst.COMMIT);
		init();
	}

	private void init() {
		String path = mCommitDiff.getNew_path();
		int index = path.lastIndexOf("/");
		if (index == -1) {
			mActionBar.setTitle(path);
		} else {
			mActionBar.setTitle(path.substring(index + 1));
		}
		mActionBar.setSubtitle("提交" + mCommit.getShortId());
		mWebView = (WebView) findViewById(R.id.code_file_webview);

		mEditor = new SourceEditor(mWebView);
		
		mLoading = (ProgressBar) findViewById(R.id.code_file_loading);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenu = menu;
		// 刷新按钮
		MenuItem refreshItem = menu.add(0, MENU_REFRESH_ID, MENU_REFRESH_ID,
				"刷新");
		refreshItem.setIcon(R.drawable.abc_ic_menu_refresh);

		MenuItem moreOption = menu.add(0, MENU_MORE_ID, MENU_MORE_ID, "更多");
		moreOption.setIcon(R.drawable.abc_ic_menu_moreoverflow_normal_holo_dark);
		MenuItemCompat.setShowAsAction(refreshItem,
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		/*MenuItemCompat.setShowAsAction(moreOption,
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);*/
		loadDatasCode(mProject.getId(), mCommit.getId(), mCommitDiff.getNew_path());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch (id) {
		case MENU_REFRESH_ID:
			loadDatasCode(mProject.getId(), mCommit.getId(), mCommitDiff.getNew_path());
			break;
		case MENU_MORE_ID:
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStatus(int status) {
		if(optionsMenu == null) {
			return;
		}
		// 更新状态
		if(status == STATUS_LOADING) {
			mLoading.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.GONE);
		} else {
			mLoading.setVisibility(View.GONE);
			mWebView.setVisibility(View.VISIBLE);
			if (status == STATUS_NONE) {
				
			}
		}
	}

	private void loadDatasCode(final String projectId, final String commitId, final String filePath) {
		
		onStatus(STATUS_LOADING);
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					AppContext ac = getGitApplication();
					String body = ac.getCommitFileDetail(projectId, commitId, filePath);
					msg.what = 1;
					msg.obj = body;
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				if (msg.what == 1 && msg.obj != null) {
					onStatus(STATUS_LOADED);
					String body = (String) msg.obj;
					if (body != null) {
						mEditor.setSource(filePath, body, false);
					}
				} else {
					onStatus(STATUS_NONE);
					if (msg.obj instanceof AppException) {
						AppException e = ((AppException)msg.obj);
						if (e.getCode() == 404) {
							UIHelper.ToastMessage(appContext, "读取失败，文件已被删除");
						} else {
							((AppException)msg.obj).makeToast(appContext);
						}
						
					}else {
						UIHelper.ToastMessage(appContext, ((Exception)msg.obj).getMessage());
					} 
				}
			}
		}.execute();
	}
}
