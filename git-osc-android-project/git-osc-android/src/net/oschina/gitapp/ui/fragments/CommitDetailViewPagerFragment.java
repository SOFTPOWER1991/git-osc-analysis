package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

/**
 * commit详情页面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class CommitDetailViewPagerFragment extends BaseViewPagerFragment {
	
    public static CommitDetailViewPagerFragment newInstance(Project project,Commit commit) {
    	CommitDetailViewPagerFragment fragment = new CommitDetailViewPagerFragment();
    	Bundle args = new Bundle();
		args.putSerializable(Contanst.PROJECT, project);
		args.putSerializable(Contanst.COMMIT, commit);
		fragment.setArguments(args);
        return fragment;
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		Bundle args = getArguments();
		adapter.addTab("详情", "detail", CommitFileDetailFragment.class, args);
		adapter.addTab("评论", "comments", CommitCommentFragment.class, args);
	}
}
