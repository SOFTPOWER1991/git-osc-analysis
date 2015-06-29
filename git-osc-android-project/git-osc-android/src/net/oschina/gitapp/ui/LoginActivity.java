package net.oschina.gitapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.CyptoUtils;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

public class LoginActivity extends BaseActionBarActivity 
	implements OnClickListener, OnEditorActionListener {
	
	private AppContext mAppContext;
	private AutoCompleteTextView mAccountEditText;
	private EditText mPasswordEditText;
	private ProgressDialog mLoginProgressDialog;
	private Button mLogin;
	private InputMethodManager imm;
	private TextWatcher textWatcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mAppContext = getGitApplication();
		initView();
	}

	private void initView() {
		mAccountEditText = (AutoCompleteTextView) findViewById(R.id.login_account);
		mPasswordEditText = (EditText) findViewById(R.id.login_password);
		mLogin = (Button) findViewById(R.id.login_btn_login);
		mLogin.setOnClickListener(this);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		textWatcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				
				// 若密码和帐号都为空，则登录按钮不可操作
				String account = mAccountEditText.getText().toString();
				String pwd = mPasswordEditText.getText().toString();
				if (StringUtils.isEmpty(account) || StringUtils.isEmpty(pwd)) {
					mLogin.setEnabled(false);
				} else {
					mLogin.setEnabled(true);
				}
			}
		};
		// 添加文本变化监听事件
		mAccountEditText.addTextChangedListener(textWatcher);
		mPasswordEditText.addTextChangedListener(textWatcher);
		mPasswordEditText.setOnEditorActionListener(this);
		
		String account = CyptoUtils.decode(Contanst.ACCOUNT_EMAIL, mAppContext.getProperty(Contanst.ACCOUNT_EMAIL));
		mAccountEditText.setText(account);
		String pwd = CyptoUtils.decode(Contanst.ACCOUNT_PWD, mAppContext.getProperty(Contanst.ACCOUNT_PWD));
		mPasswordEditText.setText(pwd);
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//在输入法里点击了“完成”，则去登录
		if(actionId == EditorInfo.IME_ACTION_DONE) {
			checkLogin();
			//将输入法隐藏
			InputMethodManager imm = (InputMethodManager)getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
			return true;
		}
		return false;
	}
	
	/**
	 * 检查登录
	 */
	private void checkLogin() {
		
		String email = mAccountEditText.getText().toString();
		String passwd = mPasswordEditText.getText().toString();
		
		//检查用户输入的参数
		if(StringUtils.isEmpty(email)){
			UIHelper.ToastMessage(this, getString(R.string.msg_login_email_null));
			return;
		}
		if(!StringUtils.isEmail(email)) {
			UIHelper.ToastMessage(this, getString(R.string.msg_login_email_error));
			return;
		}
		if(StringUtils.isEmpty(passwd)){
			UIHelper.ToastMessage(this, getString(R.string.msg_login_pwd_null));
			return;
		}
		
		
		// 保存用户名和密码
		mAppContext.saveAccountInfo(CyptoUtils.encode(Contanst.ACCOUNT_EMAIL, email), CyptoUtils.encode(Contanst.ACCOUNT_PWD, passwd));
		
		login(email, passwd);
	}
	
	// 登录验证
	private void login(final String account, final String passwd) {
		if(mLoginProgressDialog == null) {
    		mLoginProgressDialog = new ProgressDialog(this);
    		mLoginProgressDialog.setCancelable(true);
    		mLoginProgressDialog.setCanceledOnTouchOutside(false);
    		mLoginProgressDialog.setMessage(getString(R.string.login_tips));
    	}
		//异步登录
    	new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
	                User user = mAppContext.loginVerify(account, passwd);
	                msg.what = 1;
	                msg.obj = user;
	            } catch (Exception e) {
			    	msg.what = -1;
			    	msg.obj = e;
			    	if(mLoginProgressDialog != null) {
						mLoginProgressDialog.dismiss();
					}
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if(mLoginProgressDialog != null) {
					mLoginProgressDialog.show();
				}
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				//如果程序已经关闭，则不再执行以下处理
				if(isFinishing()) {
					return;
				}
				if(mLoginProgressDialog != null) {
					mLoginProgressDialog.dismiss();
				}
				Context context = LoginActivity.this;
				if(msg.what == 1){
					User user = (User)msg.obj;
					if(user != null){
						//提示登陆成功
						UIHelper.ToastMessage(context, R.string.msg_login_success);
						//返回标识，成功登录
						setResult(RESULT_OK);
						// 发送用户登录成功的广播
						BroadcastController.sendUserChangeBroadcase(getActivity());
						finish();
					}
				} else if(msg.what == 0){
					UIHelper.ToastMessage(context, getString(
							R.string.msg_login_fail) + msg.obj);
				} else if(msg.what == -1){
					if (msg.obj instanceof AppException) {
						AppException e = ((AppException)msg.obj);
						if (e.getCode() == 401) {
							UIHelper.ToastMessage(context, R.string.msg_login_error);
						} else {
							((AppException)msg.obj).makeToast(context);
						}
					} else {
						UIHelper.ToastMessage(context, R.string.msg_login_error);
					}
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
		checkLogin();
	}
}
