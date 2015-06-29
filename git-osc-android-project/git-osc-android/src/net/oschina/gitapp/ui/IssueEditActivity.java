package net.oschina.gitapp.ui;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Milestone;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

public class IssueEditActivity extends BaseActionBarActivity implements OnClickListener {

	private InputMethodManager imm;
	
	private Project mProject;
	
	private Issue mIssue;
	
	private ProgressBar mLoading;
	
	private ScrollView mScrollView;
	
	private EditText mIssueTitle;
	
	private TextView mIssueAssigneeName;//被指派人
	
	private LinearLayout mIssueLLAssignee;
	
	private TextView mIssueMilestone;//里程碑
	
	private LinearLayout mIssueLLMilestone;
	
	private EditText mIssueBody;
	
	private Button mPub;
	
	private TextWatcher mTextWatcher;
	
	private ProgressDialog mLoadSome;
	
	private AlertDialog.Builder mAssigneeDialog;
	
	private ArrayList<User> mAssigneesList;
	
	private AlertDialog.Builder mMilestoneDialog;
	
	private ArrayList<Milestone> mMilestonesList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_edit);
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		init();
		initViewData();
	}

	private void init() {
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
		mIssue = (Issue) intent.getSerializableExtra(Contanst.ISSUE);
		
		mLoading = (ProgressBar) findViewById(R.id.issue_edit_loading);
		mScrollView = (ScrollView) findViewById(R.id.issue_edit_sv_issue_content);
		mIssueTitle = (EditText) findViewById(R.id.issue_edit_title);
		mIssueAssigneeName = (TextView) findViewById(R.id.issue_edit_assignee_name);
		mIssueLLAssignee = (LinearLayout) findViewById(R.id.issue_edit_ll_assignee);
		mIssueMilestone = (TextView) findViewById(R.id.issue_edit_milestone);
		mIssueLLMilestone = (LinearLayout) findViewById(R.id.issue_edit_ll_milestone);
		mIssueBody = (EditText) findViewById(R.id.issue_edit_body);
		mPub = (Button) findViewById(R.id.issue_edit_pub);
		
		mIssueLLAssignee.setOnClickListener(this);
		mIssueLLMilestone.setOnClickListener(this);
		mPub.setOnClickListener(this);
		
		mTextWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (StringUtils.isEmpty(mIssueTitle.getText().toString()) || StringUtils.isEmpty(mIssueBody.getText().toString())) {
					mPub.setEnabled(false);
				} else {
					mPub.setEnabled(true);
				}
			}
		};
		mIssueTitle.addTextChangedListener(mTextWatcher);
		mIssueBody.addTextChangedListener(mTextWatcher);
		
		mLoadSome = new ProgressDialog(getActivity());
		mLoadSome.setCancelable(true);
		mLoadSome.setCanceledOnTouchOutside(false);
	}
	
	private void initViewData() {
		if (mIssue != null) {
			mActionBar.setTitle("Isssue");
		} else {
			newIssue();
		}
		mActionBar.setSubtitle(mProject.getOwner().getName() + "/" + mProject.getName());
		mLoading.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);
	}
	
	private void newIssue() {
		mActionBar.setTitle("新增Isssue");
		mIssueAssigneeName.setText("选择用户");
		mIssueMilestone.setText("选择里程碑");
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.issue_edit_ll_assignee:
			loadAssignees();
			break;
		case R.id.issue_edit_ll_milestone:
			loadMilestones();
			break;
		case R.id.issue_edit_pub:
			pubCreateIssue();
			break;
		default:
			break;
		}
	}
	
	// 加载协作者
	private void loadAssignees() {
		//异步
    	new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
					msg.what = 1;
					if (mAssigneesList == null) {
						msg.obj = getGitApplication().getProjectMembers(mProject.getId());
					} else {
						msg.obj = mAssigneesList;
					}
	            } catch (Exception e) {
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mLoadSome.setMessage("加载协作者...");
				if (mAssigneesList == null) {
					mLoadSome.show();
				}
				if (mAssigneeDialog == null) {
					mAssigneeDialog = new AlertDialog.Builder(getActivity()).setTitle("选择被指派人");
				}
			}
			
			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				if (mLoadSome != null) mLoadSome.dismiss();
				if (msg.what == 1) {
					
					mAssigneesList = (ArrayList<User>) msg.obj;
					
					final String[] arrays = new String[mAssigneesList.size()];
					int index = -1;
					for (int i = 0; i < arrays.length; i ++) {
						arrays[i] = mAssigneesList.get(i).getName();
						if (arrays[i].equalsIgnoreCase((String) mIssueAssigneeName.getText())) index = i;
					}
					mAssigneeDialog.setSingleChoiceItems(arrays, index, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									mIssueAssigneeName.setTag(mAssigneesList.get(which).getId());
									mIssueAssigneeName.setText(arrays[which]);
								}
							}).setNegativeButton("取消", null)
							.setPositiveButton("清除", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									mIssueAssigneeName.setTag(null);
									mIssueAssigneeName.setText("选择用户");
								}
							});
					mAssigneeDialog.show();
				} else {
					if (msg.obj instanceof AppException) {
						((AppException)msg.obj).makeToast(getActivity());
					} else {
						UIHelper.ToastMessage(getGitApplication(), "加载协作者失败");
					}
				}
			}
		}.execute();
	}
	
	// 加载里程碑
	private void loadMilestones() {
		//异步
    	new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
					msg.what = 1;
					if (mMilestonesList == null) {
						msg.obj = getGitApplication().getProjectMilestone(mProject.getId());
					} else {
						msg.obj = mMilestonesList;
					}
	            } catch (Exception e) {
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				mLoadSome.setMessage("加载里程碑...");
				if (mMilestonesList == null) {
					mLoadSome.show();
				}
				if (mMilestoneDialog == null) {
					mMilestoneDialog = new AlertDialog.Builder(getActivity()).setTitle("选择里程碑");
				}
			}
			
			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				if (mLoadSome != null) mLoadSome.dismiss();
				if (msg.what == 1) {
					
					mMilestonesList = (ArrayList<Milestone>) msg.obj;
					
					final String[] arrays = new String[mMilestonesList.size()];
					int index = -1;
					for (int i = 0; i < arrays.length; i ++) {
						arrays[i] = mMilestonesList.get(i).getTitle();
						if (arrays[i].equalsIgnoreCase((String) mIssueAssigneeName.getText())) index = i;
					}
					mMilestoneDialog.setSingleChoiceItems(arrays, index, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									mIssueMilestone.setTag(mMilestonesList.get(which).getId());
									mIssueMilestone.setText(arrays[which]);
								}
							}).setNegativeButton("取消", null)
							.setPositiveButton("清除", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									mIssueMilestone.setTag(null);
									mIssueMilestone.setText("选择里程碑");
								}
							});
					mMilestoneDialog.show(); 
				} else {
					if (msg.obj instanceof AppException) {
						((AppException)msg.obj).makeToast(getActivity());
					} else {
						UIHelper.ToastMessage(getGitApplication(), "加载里程碑失败");
					}
				}
			}
		}.execute();
	}
	
	private void pubCreateIssue() {
		if (!getGitApplication().isLogin()) {
			UIHelper.showLoginActivity(IssueEditActivity.this);
			return;
		}
		//异步
    	new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
					msg.what = 1;
					String title = mIssueTitle.getText().toString();
					String description = mIssueBody.getText().toString();
					String  assignee_id = (String) mIssueAssigneeName.getTag();
					String  milestone_id = (String) mIssueMilestone.getTag();
					msg.obj = getGitApplication().pubCreateIssue(mProject.getId(), title, description, assignee_id, milestone_id);
	            } catch (Exception e) {
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				mLoadSome.setMessage("正在创建Issue...");
				mLoadSome.show();
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				if (mLoadSome != null) mLoadSome.dismiss();
				if (msg.what == 1) {
					Issue issue = (Issue) msg.obj;
					if (issue != null) {
						UIHelper.showIssueDetail(getGitApplication(), mProject, issue, null, null);
						UIHelper.ToastMessage(getGitApplication(), "创建成功");
						getActivity().finish();
					}
				} else {
					if (msg.obj instanceof AppException) {
						((AppException)msg.obj).makeToast(getActivity());
					} else {
						UIHelper.ToastMessage(getGitApplication(), "创建issue失败");
					}
				}
			}
		}.execute();
	}
}
