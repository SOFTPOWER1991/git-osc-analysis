package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.os.Bundle;
import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ExploreListProjectAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 发现页面推荐项目列表Fragment
 * @created 2014-05-19 上午10：43
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class ExploreListProjectFragment extends BaseSwipeRefreshFragment<Project, CommonList<Project>> {
	
	public final static String EXPLORE_TYPE = "explore_type";
	
	public final static byte TYPE_FEATURED = 0x0;
	
	public final static byte TYPE_POPULAR = 0x1;
	
	public final static byte TYPE_LATEST = 0x2;
	
	private byte type = 0;
	
	/**
	 * 获取ExploreListProjectFragment 实例。
	 * @param type
	 * @return
	 */
	public static ExploreListProjectFragment newInstance(byte type) {
		ExploreListProjectFragment exploreFeaturedListProjectFragment = new ExploreListProjectFragment();
		Bundle bundle = new Bundle();
		bundle.putByte(EXPLORE_TYPE, type);
		exploreFeaturedListProjectFragment.setArguments(bundle);
		return exploreFeaturedListProjectFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		type = args.getByte(EXPLORE_TYPE);
	}

	@Override
	public BaseAdapter getAdapter(List<Project> list) {
		return new ExploreListProjectAdapter(getActivity(), list, R.layout.exploreproject_listitem);
	}

	@Override
	public MessageData<CommonList<Project>> asyncLoadList(int page,
			boolean refresh) {
		MessageData<CommonList<Project>> msg = null;
		try {
			CommonList<Project> list = getList(type, page, refresh);
			msg = new MessageData<CommonList<Project>>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<CommonList<Project>>(e);
		}
		return msg;
	}
	
	private CommonList<Project> getList(byte type, int page, boolean refresh) throws AppException {
		CommonList<Project> list = null;
		switch (type) {
		case TYPE_FEATURED:
			list = mApplication.getExploreFeaturedProject(page, refresh);
			break;
		case TYPE_POPULAR:
			list = mApplication.getExplorePopularProject(page, refresh);
			break;
		case TYPE_LATEST:
			list = mApplication.getExploreLatestProject(page, refresh);
		
			
			break;
		}
		return list;
	}
	
	@Override
	public void onItemClick(int position, Project project) {
		UIHelper.showProjectDetail(getActivity(), null, project.getId());
	}
	
}
