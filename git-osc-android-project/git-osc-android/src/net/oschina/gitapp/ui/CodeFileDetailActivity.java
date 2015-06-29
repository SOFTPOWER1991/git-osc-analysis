package net.oschina.gitapp.ui;

import java.util.ArrayList;
import java.util.List;
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
import android.webkit.WebView;
import android.widget.ProgressBar;
import net.oschina.gitapp.AppConfig;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.MoreMenuItem;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.interfaces.OnStatusListener;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.util.MarkdownUtils;
import net.oschina.gitapp.util.SourceEditor;
import net.oschina.gitapp.widget.DropDownMenu;

/**
 * 代码文件详情
 * 
 * @created 2014-06-04
 * @author 火蚁
 * 
 */
@SuppressWarnings("deprecation")
public class CodeFileDetailActivity extends BaseActionBarActivity implements
		OnStatusListener {

	private final int MORE_MENU_SHARE = 00;// 分享
	private final int MORE_MENU_COPY_LINK = 01;// 复制链接
	private final int MORE_MENU_OPEN_WITH_BROWS = 02;// 在浏览器中打开
	private final int MORE_MENU_DOWNLOAD = 03;
	private final int MORE_MENU_EDIT = 04;

	private AppContext mContext;

	private Menu optionsMenu;

	private WebView mWebView;

	private ProgressBar mLoading;

	private SourceEditor editor;

	private CodeFile mCodeFile;

	private Project mProject;

	private String mFileName;

	private String mPath;

	private String mRef;

	private DropDownMenu mMoreMenuWindow;

	private List<MoreMenuItem> mMoreItems = new ArrayList<MoreMenuItem>();

	private String url_link = null;

	private Bitmap bitmap;

	private View.OnClickListener onMoreItemClickListener = new View.OnClickListener() {

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
				UIHelper.showShareOption(CodeFileDetailActivity.this,
						mFileName, url_link, "我正在看项目《" + mProject.getName()
								+ "》的文件" + mFileName + "，你也来瞧瞧呗！", bitmap);
				break;
			case MORE_MENU_COPY_LINK:
				ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				cbm.setText(url_link);
				UIHelper.ToastMessage(mContext, "已复制到剪贴板");
				break;
			case MORE_MENU_OPEN_WITH_BROWS:
				if (!mProject.isPublic()) {
					if (!mContext.isLogin()) {
						UIHelper.showLoginActivity(CodeFileDetailActivity.this);
						return;
					}
					url_link = url_link + "?private_token="
							+ ApiClient.getToken(mContext);
				}
				UIHelper.openBrowser(CodeFileDetailActivity.this, url_link);
				break;
			case MORE_MENU_DOWNLOAD:
				downloadFile();
				break;
			case MORE_MENU_EDIT:
				showEditCodeFileActivity();
				break;
			default:
				break;
			}
		}
	};

	private void downloadFile() {
		String path = AppConfig.DEFAULT_SAVE_FILE_PATH;
		boolean res = FileUtils.writeFile(mCodeFile.getContent().getBytes(),
				path, mFileName);
		if (res) {
			UIHelper.ToastMessage(mContext, "文件已经保存在" + path);
		} else {
			UIHelper.ToastMessage(mContext, "保存文件失败");
		}
	}

	private void showEditCodeFileActivity() {
		Intent intent = new Intent(CodeFileDetailActivity.this,
				CodeFileEditActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.CODE_FILE, mCodeFile);
		bundle.putSerializable(Contanst.PROJECT, mProject);
		bundle.putString(Contanst.BRANCH, mRef);
		bundle.putString(Contanst.PATH, mPath);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置actionbar加载动态
		setContentView(R.layout.activity_code_file_view);
		mContext = getGitApplication();
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mFileName = intent.getStringExtra("fileName");
		mPath = intent.getStringExtra("path");
		mRef = intent.getStringExtra("ref");
		init();
		loadDatasCode(mProject.getId(), mPath, mRef);

		url_link = URLs.URL_HOST + mProject.getOwner().getUsername()
				+ URLs.URL_SPLITTER + mProject.getPath() + URLs.URL_SPLITTER
				+ "blob" + URLs.URL_SPLITTER + mRef + URLs.URL_SPLITTER + mPath;
	}

	private void init() {
		mTitle = mFileName;
		mSubTitle = mRef;
		mWebView = (WebView) findViewById(R.id.code_file_webview);
		editor = new SourceEditor(mWebView);

		mLoading = (ProgressBar) findViewById(R.id.code_file_loading);
	}

	private void initMoreMenu() {

		mMoreMenuWindow = new DropDownMenu(CodeFileDetailActivity.this,
				onMoreItemClickListener);

		MoreMenuItem shar = new MoreMenuItem(MORE_MENU_SHARE,
				R.drawable.more_menu_icon_share, "分享");
		mMoreItems.add(shar);

		MoreMenuItem copy_link = new MoreMenuItem(MORE_MENU_COPY_LINK,
				R.drawable.more_menu_icon_copy, "复制链接");
		mMoreItems.add(copy_link);

		MoreMenuItem open_with_brows = new MoreMenuItem(
				MORE_MENU_OPEN_WITH_BROWS, R.drawable.more_menu_icon_browser,
				"在浏览器中打开");
		mMoreItems.add(open_with_brows);

		MoreMenuItem download = new MoreMenuItem(MORE_MENU_DOWNLOAD,
				R.drawable.more_menu_icon_download, "下载该文件");
		mMoreItems.add(download);

		// 如果该文件是属于登陆用户的项目，则显示有编辑的操作
		if (mProject.getRelation() != null
				&& (mProject.getRelation().equalsIgnoreCase(
						Project.RELATION_TYPE_DEVELOPER) || mProject
						.getRelation().equalsIgnoreCase(
								Project.RELATION_TYPE_MASTER))) {
			MoreMenuItem edit = new MoreMenuItem(MORE_MENU_EDIT,
					R.drawable.more_menu_icon_edit, "编辑");
			mMoreItems.add(edit);
		}
		mMoreMenuWindow.addItems(mMoreItems);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenu = menu;
		getMenuInflater().inflate(R.menu.projet_code_detail_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch (id) {
		case R.id.project_detail_menu_refresh:
			loadDatasCode(mProject.getId(), mPath, mRef);
			break;
		case R.id.project_detail_menu_more:
			showMoreOptionMenu();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showMoreOptionMenu() {
		if (mMoreMenuWindow != null) {
			View v = findViewById(R.id.project_detail_menu_more);
			int x = mMoreMenuWindow.getWidth() - v.getWidth() + 20;

			mMoreMenuWindow.showAsDropDown(v, -x, 0);
		}
	}

	@Override
	public void onStatus(int status) {
		if (optionsMenu == null) {
			return;
		}

		if (status == STATUS_LOADING) {
			mLoading.setVisibility(View.VISIBLE);
			mWebView.setVisibility(View.GONE);
		} else {
			mLoading.setVisibility(View.GONE);
			mWebView.setVisibility(View.VISIBLE);
			if (status == STATUS_NONE) {
			}
		}
	}

	private void loadDatasCode(final String projectId, final String path,
			final String ref_name) {
		onStatus(STATUS_LOADING);
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					AppContext ac = getGitApplication();
					CodeFile codeFile = ac.getCodeFile(projectId, path,
							ref_name);
					msg.what = 1;
					msg.obj = codeFile;
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
					if (mMoreMenuWindow == null) {
						initMoreMenu();
					}
					mCodeFile = (CodeFile) msg.obj;
					editor.setMarkdown(MarkdownUtils.isMarkdown(mPath));
					editor.setSource(mPath, mCodeFile);

					onStatus(STATUS_LOADED);

					// 截取屏幕
					Handler mHandler = new Handler();
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							if (bitmap == null) {
								bitmap = UIHelper
										.takeScreenShot(CodeFileDetailActivity.this);
							}
						}
					}, 500);
				} else {
					onStatus(STATUS_NONE);
					if (msg.obj instanceof AppException) {
						AppException appException = (AppException) msg.obj;
						appException.makeToast(mContext);
					} else {
						UIHelper.ToastMessage(mContext,
								((Exception) msg.obj).getMessage());
					}
				}
			}
		}.execute();
	}
}
