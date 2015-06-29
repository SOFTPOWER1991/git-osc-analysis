package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.HTTPRequestor;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.ReadMe;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

/**
 * 项目ReadMe文件详情
 * @created 2014-07-17
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
public class ProjectReadMeActivity extends BaseActionBarActivity {
	
	private Project mProject;
	
	private View mLoading;
	
	private WebView mWebView;
	
	public String linkCss = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/readme_style.css\">";
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_readme_fragment);
		Intent intent = getIntent();
		if (intent != null) {
			mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
			mTitle = "README.md";
			mSubTitle = "master";
		}
		initView();
		loadData();
	}

	private void initView() {
		mLoading = findViewById(R.id.project_readme_loading);
		mWebView = (WebView) findViewById(R.id.project_readme_webview);
	}
	
	private void loadData() {
		
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					msg.obj = getGitApplication().getReadMeFile(mProject.getId());
					msg.what = 1;
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				}
				return msg;
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				if (getActivity().isFinishing()) {
					return;
				}
				mLoading.setVisibility(View.GONE);
				if (msg.what == 1) {
					ReadMe readMe = (ReadMe) msg.obj;
					
					if (readMe.getContent() != null) {
						mWebView.setVisibility(View.VISIBLE);
						String body = linkCss + "<div class='markdown-body'>" + readMe.getContent() + "</div>";
						mWebView.loadDataWithBaseURL(null, body, "text/html", HTTPRequestor.UTF_8, null);
					} else {
						getActivity().findViewById(R.id.project_readme_empty).setVisibility(View.VISIBLE);
					}
					
				} else {
					if (msg.obj instanceof AppException) {
						AppException e = (AppException)msg.obj;
						if (e.getCode() == 404) {
							getActivity().findViewById(R.id.project_readme_empty).setVisibility(View.VISIBLE);
						} else {
							((AppException)msg.obj).makeToast(getGitApplication());
						}
					}
				}
			}
		}.execute();
	}
}
