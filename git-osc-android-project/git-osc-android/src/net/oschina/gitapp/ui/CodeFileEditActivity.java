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
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

public class CodeFileEditActivity extends BaseActionBarActivity implements OnClickListener {
	
	private AppContext mAppContext;
	
	private CodeFile mCodeFile;
	
	private Project mProject;
	
	private String mPath;
	
	private String mBranch;
	
	private EditText mEditContent;
	
	private EditText mCommitMsg;
	
	private Button mCodeFilePub;
	
	private ProgressDialog mPubing;
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if (mEditContent.getText().toString().equals(mCodeFile.getContent()) || StringUtils.isEmpty(mCommitMsg.getText().toString())) {
				mCodeFilePub.setEnabled(false);
			} else {
				mCodeFilePub.setEnabled(true);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_codefile);
		mAppContext = getGitApplication();
		initView();
		initData();
	}
	
	private void initView() {
		mEditContent = (EditText) findViewById(R.id.codefile_edit);
		mCommitMsg = (EditText) findViewById(R.id.codefile_edit_msg);
		mCodeFilePub = (Button) findViewById(R.id.codefile_edit_pub);
		
		mEditContent.addTextChangedListener(mTextWatcher);
		mCommitMsg.addTextChangedListener(mTextWatcher);
		mCodeFilePub.setOnClickListener(this);
	}
	
	private void initData() {
		Intent intent = getIntent();
		mCodeFile = (CodeFile) intent.getSerializableExtra(Contanst.CODE_FILE);
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mPath = intent.getStringExtra(Contanst.PATH);
		mBranch = intent.getStringExtra(Contanst.BRANCH);
		if (mCodeFile != null) {
			mEditContent.setText(mCodeFile.getContent());
			mTitle = mCodeFile.getFile_name();
			mSubTitle = mProject.getOwner().getName() + "/" + mProject.getName();
		}
	}

	private void pubCommitCodeFile() {
		final String content = mEditContent.getText().toString();
		final String commit_message = mCommitMsg.getText().toString();
		if (content.equals(mCodeFile.getContent())) {
			UIHelper.ToastMessage(mAppContext, "文件内容没有改变");
			return;
		}
		if (StringUtils.isEmpty(commit_message)) {
			UIHelper.ToastMessage(mAppContext, "文件内容没有改变");
			return;
		}
		if (mPubing == null) {
			mPubing = new ProgressDialog(CodeFileEditActivity.this);
			mPubing.setMessage("正在提交...");
		}
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					msg.obj = mAppContext.updateRepositoryFiles(mProject.getId(), mBranch, mPath, mBranch, content, commit_message);
					msg.what = 1;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mPubing.show();
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				mPubing.dismiss();
				if (msg.what == 1) {
					String res = (String) msg.obj;
					if (res != null) {
						finish();
						UIHelper.ToastMessage(mAppContext, "提交成功");
					}
				} else {
					if (msg.obj instanceof AppException) {
						((AppException)msg.obj).makeToast(mAppContext);
					}
				}
			}
			
		}.execute();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.codefile_edit_pub:
			pubCommitCodeFile();
			break;

		default:
			break;
		}
	}
	
}
