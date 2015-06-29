package net.oschina.gitapp.adapter;

import java.util.List;

import net.oschina.gitapp.ui.basefragment.BaseFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * viewpage适配器
 * @created 2014-05-28
 * @author 火蚁（http://my.oschina.LittleDY）
 *
 */
public class ListFragmentPagerAdapter<T extends BaseFragment> extends FragmentPagerAdapter {
	private List<T> fragmentList;
	private List<String> titleList;

	public ListFragmentPagerAdapter(FragmentManager fm,
			List<String> titleList, List<T> fragmentList) {
		super(fm);
		this.titleList = titleList;
		this.fragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int position) {
		return (fragmentList == null || fragmentList.size() == 0) ? null : fragmentList.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return (titleList.size() > position) ? titleList.get(position) : "";

	}

	@Override
	public int getCount() {
		return fragmentList == null ? 0 : fragmentList.size();
	}
}
