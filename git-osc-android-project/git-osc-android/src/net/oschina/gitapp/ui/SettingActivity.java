package net.oschina.gitapp.ui;

import java.io.File;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppManager;
import net.oschina.gitapp.R;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.common.MethodsCompat;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.common.UpdateManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

/**
 * 设置界面
 * @created 2014-07-02
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener {
	
	private Preference cache;
	private Preference feedback;
	private Preference update;
	private Preference about;

	private CheckBoxPreference isReceiveNotice;
	private CheckBoxPreference voice;
	private CheckBoxPreference checkup;
	
	private AppContext mAppContext;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		initView();
		AppManager.getAppManager().addActivity(this);
	}
	
	@SuppressWarnings("deprecation")
	private void initView() {
		mAppContext = (AppContext) getApplication();
		
		// 接收通知
		isReceiveNotice = (CheckBoxPreference) findPreference("isnotice");
		isReceiveNotice.setChecked(mAppContext.isReceiveNotice());
		if (mAppContext.isReceiveNotice()) {
			isReceiveNotice.setSummary("已开启接收通知");
		} else {
			isReceiveNotice.setSummary("已关闭接收通知");
		}
		isReceiveNotice.setOnPreferenceClickListener(this);
		
		// 提示声音
		voice = (CheckBoxPreference) findPreference("voice");
		voice.setChecked(mAppContext.isVoice());
		if (mAppContext.isVoice()) {
			voice.setSummary("已开启提示声音");
		} else {
			voice.setSummary("已关闭提示声音");
		}
		voice.setOnPreferenceClickListener(this);
		
		checkup = (CheckBoxPreference) findPreference("checkup");
		checkup.setChecked(mAppContext.isCheckUp());
		checkup.setOnPreferenceClickListener(this);
		
		cache = (Preference) findPreference("cache");
		cache.setSummary(calCache());
		cache.setOnPreferenceClickListener(this);
		
		feedback = (Preference) findPreference("feedback");
		update = (Preference) findPreference("update");
		about = (Preference) findPreference("about");
		
		
		feedback.setOnPreferenceClickListener(this);
		update.setOnPreferenceClickListener(this);
		about.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == isReceiveNotice) {
			onReceiveNotice();
		} else if (preference == voice) {
			onVoice();
		} else if (preference == checkup) {
			mAppContext.setConfigCheckUp(checkup.isChecked());
		} else if (preference == cache) {
			onCache();
		} else if (preference == feedback) {
			onFeedBack();
		} else if (preference == update) {
			UpdateManager.getUpdateManager().checkAppUpdate(this, true);
		} else if (preference == about) {
			showAbout();
		}
		return true;
	}

	private void onReceiveNotice() {
		mAppContext.setConfigReceiveNotice(isReceiveNotice.isChecked());
		if (isReceiveNotice.isChecked()) {
			isReceiveNotice.setSummary("已开启接收通知");
		} else {
			isReceiveNotice.setSummary("已关闭接收通知");
		}
	}
	
	private void onVoice() {
		mAppContext.setConfigVoice(voice.isChecked());
		if (voice.isChecked()) {
			voice.setSummary("已开启提示声音");
		} else {
			voice.setSummary("已关闭提示声音");
		}
	}
	
	private void onCache() {
		UIHelper.clearAppCache(SettingActivity.this);
		cache.setSummary("OKB");
	}
	
	private String calCache() {
		long fileSize = 0;
		String cacheSize = "0KB";
		File filesDir = getFilesDir();
		File cacheDir = getCacheDir();

		fileSize += FileUtils.getDirSize(filesDir);
		fileSize += FileUtils.getDirSize(cacheDir);
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			File externalCacheDir = MethodsCompat.getExternalCacheDir(this);
			fileSize += FileUtils.getDirSize(externalCacheDir);
		}
		if (fileSize > 0)
			cacheSize = FileUtils.formatFileSize(fileSize);
		return cacheSize;
	}
	
	/**
	 * 发送反馈意见到指定的邮箱
	 */
	private void onFeedBack() {
		Intent i = new Intent(Intent.ACTION_SEND);  
		//i.setType("text/plain"); //模拟器
		i.setType("message/rfc822") ; //真机
		i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ld@oschina.net", "zhangdeyi@oschina.net"});  
		i.putExtra(Intent.EXTRA_SUBJECT,"用户反馈-git@osc Android客户端");  
		i.putExtra(Intent.EXTRA_TEXT, "");  
		startActivity(Intent.createChooser(i, "send email to me..."));
	}
	
	private void showAbout() {
		Intent intent = new Intent(SettingActivity.this, About.class);
		startActivity(intent);
	}
}
