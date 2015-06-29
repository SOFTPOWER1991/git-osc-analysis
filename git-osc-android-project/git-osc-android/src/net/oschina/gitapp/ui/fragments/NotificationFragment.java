package net.oschina.gitapp.ui.fragments;

import java.util.ArrayList;
import java.util.List;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.NotificationListAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Notification;
import net.oschina.gitapp.bean.ProjectNotification;
import net.oschina.gitapp.bean.ProjectNotificationArray;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 通知列表页面
 * 1.加载未读的通知
 * 2.加载已读的通知
 * （加载什么类型的通知通过mAction来区分）
 * @created 2014-07-08
 * @author 火蚁（http://my.oschina.net/LittleDY）
 */
public class NotificationFragment extends BaseFragment implements OnClickListener, OnChildClickListener {
	
	private final int MENU_REFRESH_ID = 1;
	
	public static final String NOTIFICATION_ACTION_KEY = "notification_action";
	
	public static final int ACTION_UNREAD = 0;//未读
	
	public static final int ACTION_READED = 1;//已读
	
	private int mAction = ACTION_UNREAD;
	
	private ProgressBar mProgressBar;
	
	private View mEmpty;
	
	private ExpandableListView mListView;
	
	private List<List<Notification>> mData;
	
	private List<ProjectNotification> mGroups;
	
	private AppContext mAppContext;
	
	private NotificationListAdapter adapter;
	
	private ImageView mEmptyImage;
	
	private TextView mEmptyMsg;
	
    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.notification_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mAppContext = getGitApplication();
		initView(view);
		steupList();
		initData();
	}
	
	private void initView(View view) {
		mProgressBar = (ProgressBar) view.findViewById(R.id.notification_fragment_loading);
		mEmpty = view.findViewById(R.id.notification_fragment_empty);
		mListView = (ExpandableListView) view.findViewById(R.id.notification_fragment_list);
		
		mEmptyImage = (ImageView) view.findViewById(R.id.notification_empty_img);
		mEmptyMsg = (TextView) view.findViewById(R.id.notification_empty_msg);
	}
	
	private void initData() {
		Bundle args = getArguments();
		if (args != null) {
			mAction = args.getInt(NOTIFICATION_ACTION_KEY, 0);
		}
		if (mAction == ACTION_UNREAD) {
			mEmptyMsg.setText("没有未读的通知");
			loadData("", "", "");
		} else {
			mEmptyMsg.setText("没有已读的通知");
			loadData("", "1", "");
		}
	}
	
	private void steupList() {
		mData = new ArrayList<List<Notification>>();
		mGroups = new ArrayList<ProjectNotification>();
		adapter = new NotificationListAdapter(mAppContext, mData, mGroups);
		mListView.setAdapter(adapter);
		
		mListView.setOnChildClickListener(this);
		TextView v = new TextView(mAppContext);
		v.setText("空的数据");
		mListView.setEmptyView(v);
		mListView.setGroupIndicator(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem createOption = menu.add(0, MENU_REFRESH_ID, MENU_REFRESH_ID, "刷新");
		createOption.setIcon(R.drawable.abc_ic_menu_refresh);
		
		MenuItemCompat.setShowAsAction(createOption, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId = item.getItemId();
		switch (menuId) {
		case MENU_REFRESH_ID:
			String all = mAction == ACTION_UNREAD ? "" : "1";
			loadData("", all ,"");
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void beforeLoading() {
		mEmpty.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);
	}
	
	private void afterLoading(boolean isEmpty) {
		mProgressBar.setVisibility(View.GONE);
		if (isEmpty) {
			mEmpty.setVisibility(View.VISIBLE);
		} else {
			mListView.setVisibility(View.VISIBLE);
		}
	}

	private void loadData(final String filter, final String all, final String project_id) {
		new AsyncTask<Void, Void, Message>() {
			
			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					CommonList<ProjectNotificationArray> commonList = mAppContext.getNotification(filter, all, project_id);
					msg.what = 1;
					msg.obj = commonList;
				} catch (AppException e) {
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				beforeLoading();
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				mData.clear();
				if (msg.what == 1) {
					CommonList<ProjectNotificationArray> commonList = (CommonList<ProjectNotificationArray>) msg.obj;
					boolean isEmpty = true;
					if (commonList.getList().size() != 0) {
						isEmpty = false;
						for (ProjectNotificationArray pna : commonList.getList()) {
							mGroups.add(pna.getProject());
							List<Notification> ns = new ArrayList<Notification>();
							ns.addAll(pna.getProject().getNotifications());
							mData.add(ns);
						}
					}
					adapter.notifyDataSetChanged();
					afterLoading(isEmpty);
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		default:
			break;
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		final Notification notification = adapter.getChild(groupPosition, childPosition);
		if (notification != null) {
			// 设置未读通知为已读
			if (!notification.isRead()) {
				new Thread(){
					public void run() {
						try {
							mAppContext.setNotificationIsRead(notification.getId());
						} catch (AppException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
			if (notification.getTarget_type().equalsIgnoreCase("Issue")) {
				UIHelper.showIssueDetail(mAppContext, null, null, notification.getProject_id(), notification.getTarget_id());
			} else {
				UIHelper.showProjectDetail(mAppContext, null, notification.getProject_id());
			}
		}
		return false;
	}
}
