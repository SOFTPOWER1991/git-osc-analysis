package net.oschina.gitapp;

import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.MainActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

/**
 * 启动界面
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-14 9:16
 */
public class AppStart extends Activity {
	
	private AppContext mAppContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppContext = (AppContext) getApplication();
		boolean isFrist = mAppContext.isFristStart();
		UIHelper.goMainActivity(AppStart.this);
		if (isFrist ) {
			goSplashPage();
		} else {
			goWelcomePage();
		}
		
	}
	
	private void goSplashPage() {
		Intent intent = new Intent(AppStart.this, SplashPage.class);
		startActivity(intent);
		finish();
	}
	
	private void goWelcomePage() {
		Intent intent = new Intent(AppStart.this, WelcomePage.class);
		startActivity(intent);
		finish();
	}
}
