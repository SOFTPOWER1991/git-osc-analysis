package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.ui.fragments.IssueDetailViewPagerFragment;

/**
 * issue详情activity
 * @created 2014-08-25
 * @author 火蚁(http://my.oschina.net/LittleDY)
 *
 */
public class IssueDetailActivity extends BaseActionBarActivity {

	private FragmentManager mFragmentManager;
	
	private ProgressBar mLoading;
	
	private Project mProject;
	
	private Issue mIssue;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_detail);
		initView();
		init(savedInstanceState);
	}
	
	private void init(Bundle savedInstanceState) {
		mFragmentManager = getSupportFragmentManager();
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mIssue = (Issue) intent.getSerializableExtra(Contanst.ISSUE);
		String projectId = intent.getStringExtra(Contanst.PROJECTID);
		String issueId = intent.getStringExtra(Contanst.ISSUEID);
		if (mIssue == null || mProject == null) {
			loadIssueAndProject(projectId, issueId);
		} else {
			initData();
		}
	}
	
	private void initData() {
		String title = "Issue " + (mIssue.getIid() == 0 ? "" :  "#" + mIssue.getIid());
		mActionBar.setTitle(title);
		mActionBar.setSubtitle(mProject.getOwner().getName() + "/"
				+ mProject.getName());
		
    	FragmentTransaction ft = mFragmentManager.beginTransaction();
    	ft.replace(R.id.issue_content, IssueDetailViewPagerFragment.newInstance(mProject, mIssue)).commit();
	}
	
	private void initView() {
		mLoading = (ProgressBar) findViewById(R.id.loading);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	private void loadIssueAndProject(final String projectId, final String issueId) {
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					mProject = getGitApplication().getProject(projectId);
					mIssue = getGitApplication().getIssue(projectId, issueId);
					msg.what = 1;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				}
				return msg;
			}
			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				mLoading.setVisibility(View.GONE);
				if (msg.what == 1) {
					initData();
				} else {
					if (msg.obj instanceof AppException) {
						((AppException)msg.obj).makeToast(getGitApplication());
					}
				}
			}
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mLoading.setVisibility(View.VISIBLE);
			}
		}.execute();
	}
}
