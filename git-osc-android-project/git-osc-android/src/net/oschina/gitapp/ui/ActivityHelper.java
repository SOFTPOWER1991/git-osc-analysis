package net.oschina.gitapp.ui;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.interfaces.ActivityHelperInterface;
import android.app.Activity;
import android.os.Bundle;

public class ActivityHelper implements ActivityHelperInterface{
	
	Activity mActivity;
	
	public ActivityHelper(Activity activity) {
		mActivity = activity;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		
	}
	
	public void onAttachedToWindow() {
		
	}

	@Override
	public Activity getActivity() {
		return mActivity;
	}

	@Override
	public AppContext getGitApplication() {
		return (AppContext)mActivity.getApplication();
	}
}
