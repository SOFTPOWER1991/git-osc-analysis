package net.oschina.gitapp.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommitDiffListAdapter;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.interfaces.OnStatusListener;
import net.oschina.gitapp.ui.basefragment.BaseFragment;

/**
 * 发现页面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class CommitFileDetailFragment extends BaseFragment implements
	OnStatusListener {
	
	private AppContext mAppContext;
	
	private Commit mCommit;
	
	private Project mProject;
	
	private ImageView mCommitAuthorFace;
	
	private TextView mCommitAuthorName;
	
	private TextView mCommitDate;
	
	private TextView mCmmitMessage;
	
	private View mLoading;
	
	private TextView mCommitFileSum;
	
	private LinearLayout mCommitDiffll;
	
	private CommonList<CommitDiff> mCommitDiffList = new CommonList<CommitDiff>();
	
	private CommitDiffListAdapter adapter;
	
    public static CommitFileDetailFragment newInstance(Project project, Commit commit) {
    	CommitFileDetailFragment fragment = new CommitFileDetailFragment();
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
		return inflater.inflate(R.layout.commit_detail_file_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		initData();
	}
    
	private void initView(View view) {
		
		mCommitAuthorFace = (ImageView) view.findViewById(R.id.commit_author_face);
		
		mCommitAuthorName = (TextView) view.findViewById(R.id.commit_author_name);
		
		mCommitDate = (TextView) view.findViewById(R.id.commit_date);
		
		mCmmitMessage = (TextView) view.findViewById(R.id.commit_message);
		
		mLoading = view.findViewById(R.id.commit_diff_ll_loading);
		
		mCommitFileSum = (TextView) view.findViewById(R.id.commit_diff_changefile_sum);
		
		mCommitDiffll = (LinearLayout) view.findViewById(R.id.commit_diff_file_list);
	}
	
	private void initData() {
		Bundle args = getArguments();
		if (args != null) {
			mProject = (Project) args.getSerializable(Contanst.PROJECT);
			mCommit = (Commit) args.getSerializable(Contanst.COMMIT);
		}
		
		mCommitDate.setText("提交于" + StringUtils.friendly_time(mCommit.getCreatedAt()));
		mCommitAuthorName.setText(mCommit.getAuthor_name());
		mCmmitMessage.setText(mCommit.getTitle());
		loadAuthorFace();
		loadDatasCode(false);
	}
	
	@Override
	public void onStatus(int status) {
		
	}
	
	// 加载提交用户头像
	private void loadAuthorFace() {
		String portrait = mCommit.getAuthor() == null ? null : mCommit.getAuthor().getPortrait();
		if (portrait == null || portrait.endsWith(".gif")) {
			mCommitAuthorFace.setBackgroundResource(R.drawable.mini_avatar);
		} else {
			String faceurl = URLs.GITIMG + portrait;
			UIHelper.showUserFace(mCommitAuthorFace, faceurl);
		}
	}
	
	private void loadDatasCode(final boolean isRefresh) {
		onStatus(STATUS_LOADING);
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					AppContext ac = getGitApplication();
					CommonList<CommitDiff> commitDiffList = ac.getCommitDiffList(mProject.getId(), mCommit.getId(), isRefresh);
					msg.what = 1;
					msg.obj = commitDiffList;
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				
				if (msg.what == 1 && msg.obj != null) {
					mLoading.setVisibility(View.GONE);
					onStatus(STATUS_LOADED);
					mCommitDiffList = (CommonList<CommitDiff>) msg.obj;
					mCommitFileSum.setText(mCommitDiffList.getCount() + " 个文件发生了变化");
					adapter = new CommitDiffListAdapter(mAppContext, mCommitDiffList.getList(), R.layout.commit_diff_listitem, mCommitDiffll);
					adapter.setData(mProject, mCommit);
					mCommitDiffll.setVisibility(View.VISIBLE);
					adapter.notifyDataSetChanged();
				} else {
					mLoading.setVisibility(View.GONE);
					((AppException)msg.obj).makeToast(getActivity());
					onStatus(STATUS_NONE);
				}
			}
		}.execute();
	}

}
