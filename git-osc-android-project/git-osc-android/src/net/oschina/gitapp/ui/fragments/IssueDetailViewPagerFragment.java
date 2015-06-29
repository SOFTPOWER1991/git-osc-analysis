package net.oschina.gitapp.ui.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.IssueCommentActivity;
import net.oschina.gitapp.ui.IssueDetailActivity;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

/**
 * issue详情页面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-08-25
 */
public class IssueDetailViewPagerFragment extends BaseViewPagerFragment {
	
	private Project mProject;
	
	private Issue mIssue;
	
    public static IssueDetailViewPagerFragment newInstance(Project project, Issue issue) {
    	IssueDetailViewPagerFragment fragment = new IssueDetailViewPagerFragment();
    	Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		args.putSerializable(Contanst.ISSUE, issue);
		fragment.setArguments(args);
        return fragment;
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		Bundle args = getArguments();
		mProject = (Project) args.getSerializable(Contanst.PROJECT);
		mIssue = (Issue) args.getSerializable(Contanst.ISSUE);
		adapter.addTab("详情", "detail", IssueDetailFragment.class, args);
		adapter.addTab("评论", "comments", IssueCommentFragment.class, args);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.issue_detail_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.issue_menu_comment:
			showIssueComment();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showIssueComment() {
		if (mProject == null || mIssue == null) {
			return;
		}
		Intent intent = new Intent(getGitApplication(), IssueCommentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, mProject);
		bundle.putSerializable(Contanst.ISSUE, mIssue);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
