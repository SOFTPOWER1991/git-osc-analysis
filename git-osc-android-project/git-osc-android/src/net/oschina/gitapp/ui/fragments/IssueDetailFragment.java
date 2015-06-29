package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.HTTPRequestor;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseFragment;

/**
 * issue详情fragment
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-08-25
 */
public class IssueDetailFragment extends BaseFragment {
	
	private AppContext mAppContext;
	
	private Issue mIssue;
	
	private Project mProject;
	
	private TextView mIssueTitle;

	private TextView mIssueUserName;

	private ImageView mIssueUserFace;

	private TextView mIssueData;

	private WebView mWebView;
	
    public static IssueDetailFragment newInstance(Project project, Commit commit) {
    	IssueDetailFragment fragment = new IssueDetailFragment();
    	Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		args.putSerializable(Contanst.COMMIT, commit);
		fragment.setArguments(args);
        return fragment;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mAppContext = getGitApplication();
		return inflater.inflate(R.layout.issue_detail, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		initData();
	}
    
	private void initView(View view) {
		
		mIssueTitle = (TextView) view.findViewById(R.id.issue_title);
		mIssueUserName = (TextView) view.findViewById(R.id.issue_author_name);
		mIssueUserFace = (ImageView) view.findViewById(R.id.issue_author_face);
		mIssueData = (TextView) view.findViewById(R.id.issue_date);
		mWebView = (WebView) view.findViewById(R.id.issue_content);
	}
	
	private void initData() {
		Bundle args = getArguments();
		if (args != null) {
			mProject = (Project) args.getSerializable(Contanst.PROJECT);
			mIssue = (Issue) args.getSerializable(Contanst.ISSUE);
		}
		
		if (mProject != null && mIssue != null) {
			setIssueDetail();
		}
	}
	
	private void setIssueDetail() {
		mIssueTitle.setText(mIssue.getTitle());
		mIssueUserName.setText(mIssue.getAuthor().getName());
		mIssueData.setText("创建于"
				+ StringUtils.friendly_time(mIssue.getCreatedAt()));
		mWebView.loadDataWithBaseURL(null, UIHelper.WEB_STYLE + mIssue.getDescription(),
				"text/html", HTTPRequestor.UTF_8, null);
		
		String portrait = mIssue.getAuthor().getPortrait() == null ? "" : mIssue.getAuthor().getPortrait();
		if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
			mIssueUserFace.setImageResource(R.drawable.widget_dface);
		} else {
			String portraitURL = URLs.GITIMG + mIssue.getAuthor().getPortrait();
			UIHelper.showUserFace(mIssueUserFace, portraitURL);
		}
	}
}
