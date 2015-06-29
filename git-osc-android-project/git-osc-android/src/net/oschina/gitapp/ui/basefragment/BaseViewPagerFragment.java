package net.oschina.gitapp.ui.basefragment;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.widget.PagerSlidingTabStrip;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 带有导航条的基类
 *
 */
public abstract class BaseViewPagerFragment extends BaseFragment{

	protected PagerSlidingTabStrip mTabStrip;
	protected ViewPager  mViewPager;
	protected ViewPageFragmentAdapter mTabsAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.base_viewpage_fragment, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        mTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.pager_tabstrip);
        
        mViewPager = (ViewPager)view.findViewById(R.id.pager);

        mTabsAdapter = new ViewPageFragmentAdapter(
        		getChildFragmentManager(), mTabStrip, mViewPager);

        onSetupTabAdapter(mTabsAdapter);
        mTabsAdapter.notifyDataSetChanged();
        mViewPager.setOffscreenPageLimit(2);
        if (savedInstanceState != null) {
        	int pos = savedInstanceState.getInt("position");
        	mViewPager.setCurrentItem(pos, true);
        }
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mViewPager.getCurrentItem());
    }
	
	protected abstract void onSetupTabAdapter(ViewPageFragmentAdapter adapter);
}