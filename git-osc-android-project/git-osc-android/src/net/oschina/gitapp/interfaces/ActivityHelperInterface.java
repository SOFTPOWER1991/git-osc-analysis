package net.oschina.gitapp.interfaces;

import net.oschina.gitapp.AppContext;
import android.app.Activity;

public interface ActivityHelperInterface {
	public AppContext getGitApplication();
	public Activity getActivity();
}
