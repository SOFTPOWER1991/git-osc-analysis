package net.oschina.gitapp.ui.basefragment;

import net.oschina.gitapp.AppContext;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 碎片基类
 * @created 2014-05-12
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 更新时间：2014-05-15 上午11：38
 * 最后更新者：火蚁
 */
public class BaseFragment extends Fragment {
	
	public AppContext getGitApplication() {
		return (AppContext) getActivity().getApplication();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
