package net.oschina.gitapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

/**
 * 评论issue界面
 * @created 2014-08-25
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
public class IssueCommentActivity extends BaseActionBarActivity implements OnClickListener {
	
	private AppContext mAppContext;
	
	private Project mProject;
	
	private Issue mIssue;
	
	private EditText mCommentContent;
	
	private Button mCommentPub;
	
	private TextWatcher mTextWatcher;
	
	private ProgressDialog mPubing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_comment);
		mAppContext = getGitApplication();
		initView();
		initData();
	}
	
	private void initView() {
		mCommentContent = (EditText) findViewById(R.id.issue_comment_content);
		mCommentPub = (Button) findViewById(R.id.issue_comment_pub);
		mTextWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (mProject == null || mIssue == null) return;
				if (StringUtils.isEmpty(mCommentContent.getText().toString())) {
					mCommentPub.setEnabled(false);
				} else {
					mCommentPub.setEnabled(true);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		}; 
		mCommentContent.addTextChangedListener(mTextWatcher);
		mCommentPub.setOnClickListener(this);
	}
	
	private void initData() {
		Intent intent = getIntent();
		if (null != intent) {
			mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
			mIssue = (Issue) intent.getSerializableExtra(Contanst.ISSUE);
		}
		
		if (null != mProject && mIssue != null) {
			mTitle = "评论 Issue #" + mIssue.getIid();
			mSubTitle = mProject.getOwner().getName() + "/" + mProject.getName();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.issue_comment_pub:
			pubComment();
			break;

		default:
			break;
		}
	}
	
	private void pubComment() {
		if (mProject == null || mIssue == null) {
			return;
		}
		if (mPubing == null) {
			mPubing = new ProgressDialog(this);
			mPubing.setCanceledOnTouchOutside(false);
			mPubing.setMessage("正在提交评论...");
		}
		
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
					msg.obj = mAppContext.pubIssueComment(mProject.getId(), mIssue.getId(), mCommentContent.getText().toString());
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
				if(mPubing != null) {
					mPubing.show();
				}
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				//如果程序已经关闭，则不再执行以下处理
				if(isFinishing()) {
					return;
				}
				if(mPubing != null) {
					mPubing.dismiss();
				}
				if (msg.what == 1) {
					UIHelper.ToastMessage(getGitApplication(), "评论成功");
					finish();
				} else {
					((AppException)(msg.obj)).makeToast(getGitApplication());
				}
			}
		}.execute();
	}
}
