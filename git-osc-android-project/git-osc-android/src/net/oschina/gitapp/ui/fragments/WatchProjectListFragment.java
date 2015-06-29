package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.os.Bundle;
import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ExploreListProjectAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 用户watch项目列表
 * @created 2014-08-27
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class WatchProjectListFragment extends BaseSwipeRefreshFragment<Project, CommonList<Project>> {
	
	private User mUser;
	
	public static WatchProjectListFragment newInstance(User user) {
		WatchProjectListFragment fragment = new WatchProjectListFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.USER, user);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mUser = (User) args.getSerializable(Contanst.USER);
		}
		setUserVisibleHint(true);
	}
	
	@Override
	public BaseAdapter getAdapter(List<Project> list) {
		return new ExploreListProjectAdapter(mApplication, list, R.layout.exploreproject_listitem);
	}

	@Override
	public MessageData<CommonList<Project>> asyncLoadList(int page,
			boolean refresh) {
		MessageData<CommonList<Project>> msg = null;
		try {
			CommonList<Project> list = mApplication.getWatchProjectList(mUser.getId(), page, refresh);
			msg = new MessageData<CommonList<Project>>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<CommonList<Project>>(e);
		}
		return msg;
	}

	@Override
	public void onItemClick(int position, Project project) {
		UIHelper.showProjectDetail(mApplication, null, project.getId());
	}
	
}
