package net.oschina.gitapp.ui.fragments;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.BaseAdapter;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.MySelfListProjectAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseSwipeRefreshFragment;

/**
 * 个人项目列表Fragment
 * @created 2014-05-12 下午14：24
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新
 * 更新者
 */
public class MySelfListProjectFragment extends BaseSwipeRefreshFragment<Project, CommonList<Project>> {
	
	public static MySelfListProjectFragment newInstance() {
		return new MySelfListProjectFragment();
	}
	
	@Override
	public BaseAdapter getAdapter(List<Project> list) {
		return new MySelfListProjectAdapter(getActivity(), list, R.layout.myselfproject_listitem);
	}

	@Override
	public MessageData<CommonList<Project>> asyncLoadList(int page,
			boolean refresh) {
		MessageData<CommonList<Project>> msg = null;
		try {
			CommonList<Project> list = mApplication.getMySelfProjectList(page, refresh);
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
		UIHelper.showProjectDetail(getActivity(), null, project.getId());
	}
}
