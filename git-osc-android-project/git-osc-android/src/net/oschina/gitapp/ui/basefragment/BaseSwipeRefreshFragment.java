package net.oschina.gitapp.ui.basefragment;

import java.util.ArrayList;
import java.util.List;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Entity;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.PageList;
import net.oschina.gitapp.common.DataRequestThreadHandler;
import net.oschina.gitapp.widget.NewDataToast;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 说明 下拉刷新界面的基类
 */
public abstract class BaseSwipeRefreshFragment<Data extends Entity, Result extends PageList<Data>>
		extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
		OnItemClickListener, OnScrollListener {

	// 没有状态
	public static final int LISTVIEW_ACTION_NONE = -1;
	// 更新状态，不显示toast
	public static final int LISTVIEW_ACTION_UPDATE = 0;
	// 初始化时，加载缓存状态
	public static final int LISTVIEW_ACTION_INIT = 1;
	// 刷新状态，显示toast
	public static final int LISTVIEW_ACTION_REFRESH = 2;
	// 下拉到底部时，获取下一页的状态
	public static final int LISTVIEW_ACTION_SCROLL = 3;

	static final int STATE_NONE = -1;
	static final int STATE_LOADING = 0;
	static final int STATE_LOADED = 1;

	protected AppContext mApplication;

	protected SwipeRefreshLayout mSwipeRefreshLayout;
	protected ListView mListView;
	private View mHeaderView;
	private View mFooterView;
	private BaseAdapter mAdapter;
	private ProgressBar mLoading;
	private View mEmpty;
	private ImageView mEmptyImage;// 图像
	private TextView mEmptyMessage;// 消息文字

	private View mFooterProgressBar;
	private TextView mFooterTextView;

	private List<Data> mDataList = new ArrayList<Data>();
	// 当前页面已加载的数据总和
	private int mSumData;

	// 当前加载状态
	private int mState = STATE_NONE;
	// UI状态
	private int mListViewAction = LISTVIEW_ACTION_NONE;

	// 当前数据状态，如果是已经全部加载，则不再执行滚动到底部就加载的情况
	private int mMessageState = MessageData.MESSAGE_STATE_MORE;
	private boolean isPauseLife = true;

	private DataRequestThreadHandler mRequestThreadHandler = new DataRequestThreadHandler();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mApplication = getGitApplication();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = getAdapter(mDataList);
	}

	@Override
	public void onPause() {
		super.onPause();
		isPauseLife = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		isPauseLife = false;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mRequestThreadHandler.quit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mHeaderView = getHeaderView(inflater);
		mFooterView = inflater.inflate(R.layout.listview_footer, null);
		mFooterProgressBar = mFooterView
				.findViewById(R.id.listview_foot_progress);
		mFooterTextView = (TextView) mFooterView
				.findViewById(R.id.listview_foot_more);

		return inflater.inflate(R.layout.fragment_base_swiperefresh, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		initView(view);
		setupListView();

		// viewpager划动到第三页，会将第一页的界面销毁，这里判断是初始状态，还是划画后再次加载
		if (mState == STATE_LOADED && mAdapter.isEmpty()) {
			setFooterNoMoreState();
		} else if (mState == STATE_LOADED
				&& mAdapter.getCount() < AppContext.PAGE_SIZE) {
			setFooterFullState();
		}
		// 正在刷新的状态
		if (mListViewAction == LISTVIEW_ACTION_REFRESH) {
			setSwipeRefreshLoadingState();
		}
		loadList(1, LISTVIEW_ACTION_INIT);
	}

	private void initView(View view) {
		mSwipeRefreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.fragment_swiperefreshlayout);
		mListView = (ListView) view.findViewById(R.id.fragment_listview);

		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(R.color.swiperefresh_color1,
				R.color.swiperefresh_color2, R.color.swiperefresh_color3,
				R.color.swiperefresh_color4);

		mLoading = (ProgressBar) view
				.findViewById(R.id.fragment_swiperefresh_loading);
		mEmpty = view.findViewById(R.id.fragment_swiperefresh_empty);
		mEmptyImage = (ImageView) mEmpty.findViewById(R.id.data_empty_image);
		mEmptyMessage = (TextView) mEmpty.findViewById(R.id.data_empty_message);
	}

	/** 设置列表空数据时的显示信息 */
	public void setEmptyInfo(int imageResId, int messageResId) {
		mEmptyImage.setBackgroundResource(imageResId);
		mEmptyMessage.setText(messageResId);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	/** 获取HeaderView */
	protected View getHeaderView(LayoutInflater inflater) {
		return null;
	}

	/** 初始化ListView */
	protected void setupListView() {
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);
		mListView.addFooterView(mFooterView);
		mListView.setAdapter(mAdapter);
		if (mHeaderView != null) {
			mListView.addHeaderView(mHeaderView);
		}
	}

	/** 获取适配器 */
	public abstract BaseAdapter getAdapter(List<Data> list);

	/** 异步加载数据 */
	protected abstract MessageData<Result> asyncLoadList(int page,
			boolean reflash);

	@Override
	public void onRefresh() {
		loadList(1, LISTVIEW_ACTION_REFRESH);
	}

	/** 更新数据，不显示toast */
	public void update() {
		loadList(1, LISTVIEW_ACTION_UPDATE);
	}

	/** 返回是否正在加载 */
	public boolean isLoadding() {
		return mState == STATE_LOADING;
	}

	/** 加载下一页 */
	protected void onLoadNextPage() {
		// 当前pageIndex
		int pageIndex = mSumData / AppContext.PAGE_SIZE + 1;
		loadList(pageIndex, LISTVIEW_ACTION_SCROLL);
	}

	/**
	 * 加载数据
	 * 
	 * @param page
	 *            页码
	 * @param action
	 *            加载的触发事件
	 * */
	void loadList(int page, int action) {
		mListViewAction = action;
		mRequestThreadHandler.request(page, new AsyncDataHandler(page, action));
	}

	/** 设置顶部正在加载的状态 */
	void setSwipeRefreshLoadingState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(true);
			// 防止多次重复刷新
			mSwipeRefreshLayout.setEnabled(false);
		}
		onRefreshLoadingStatus();
	}

	/** 设置顶部加载完毕的状态 */
	void setSwipeRefreshLoadedState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(true);
		}
		onRefreshLoadedStatus();
	}

	/** 设置底部有错误的状态 */
	void setFooterErrorState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_error);
		}
	}

	/** 设置底部有更多数据的状态 */
	void setFooterHasMoreState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_more);
		}
	}

	/** 设置底部已加载全部的状态 */
	void setFooterFullState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_full);
		}
	}

	/** 设置底部无数据的状态 */
	void setFooterNoMoreState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_empty);
		}
	}

	/** 设置底部加载中的状态 */
	void setFooterLoadingState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.VISIBLE);
			mFooterTextView.setText(R.string.load_ing);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 点击了底部
		if (view == mFooterView) {
			return;
		}
		// 点击了顶部
		if (mHeaderView == view) {
			return;
		}
		if (mHeaderView != null) {
			position = position - 1;
		}
		Data data = getData(position);
		onItemClick(position, data);
	}

	/** 点击了某个item */
	public void onItemClick(int position, Data data) {
	}

	/** 正在加载的状态 */
	public void onRefreshLoadingStatus() {
	}

	/** 加载完毕的状态 */
	public void onRefreshLoadedStatus() {
	}

	/**
	 * 返回某项的数据
	 * 
	 * @param position
	 *            数据位置
	 * */
	public Data getData(int position) {
		return mDataList.get(position);
	}

	public void beforeLoading(int action) {
		// 开始加载
		mState = STATE_LOADING;
		if (action == LISTVIEW_ACTION_REFRESH) {
			setSwipeRefreshLoadingState();
		} else if (action == LISTVIEW_ACTION_SCROLL) {
			setFooterLoadingState();
		}
	}

	public void afterLoading() {

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mAdapter == null || mAdapter.getCount() == 0) {
			return;
		}
		// 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
		if (mMessageState == MessageData.MESSAGE_STATE_FULL
				|| mMessageState == MessageData.MESSAGE_STATE_EMPTY
				|| mState == STATE_LOADING) {
			return;
		}
		// 判断是否滚动到底部
		boolean scrollEnd = false;
		try {
			if (view.getPositionForView(mFooterView) == view
					.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}

		if (scrollEnd) {
			onLoadNextPage();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	// 加载数据
	private class AsyncDataHandler implements
			DataRequestThreadHandler.AsyncDataHandler<MessageData<Result>> {

		private int mPage;
		private int mAction;

		AsyncDataHandler(int page, int action) {
			mAction = action;
			mPage = page;
		}

		@Override
		public void onPreExecute() {
			beforeLoading(mAction);
		}

		@Override
		public MessageData<Result> execute() {
			boolean refresh = true;
			if (mAction == LISTVIEW_ACTION_INIT) {
				refresh = false;
			}
			return asyncLoadList(mPage, refresh);
		}

		@Override
		public void onPostExecute(MessageData<Result> msg) {
			// 加载结束
			mState = STATE_LOADED;
			if (mAction == LISTVIEW_ACTION_INIT) {
				mSwipeRefreshLayout.setVisibility(View.VISIBLE);
				mLoading.setVisibility(View.GONE);
			}

			// 如果动作是下拉刷新，则将刷新中的状态去掉
			if (mAction == LISTVIEW_ACTION_REFRESH) {
				setSwipeRefreshLoadedState();
			}
			// 更新全局的状态
			if (mListViewAction == mAction) {
				mListViewAction = LISTVIEW_ACTION_NONE;
			}

			// 无数据的情况下(已经加载全部数据，与一开始没有数据)
			if (msg.state == MessageData.MESSAGE_STATE_EMPTY
					&& mDataList.size() != 0) {
				msg.state = MessageData.MESSAGE_STATE_FULL;
			}
			if (msg.result != null && msg.result.getList().size() == 0) {
				msg.state = MessageData.MESSAGE_STATE_EMPTY;
			}
			// 记录最后的数据状态
			mMessageState = msg.state;

			if (msg.state == MessageData.MESSAGE_STATE_EMPTY) {
				// 底部显示“暂无数据”
				setFooterNoMoreState();
				return;
			} else if (msg.state == MessageData.MESSAGE_STATE_ERROR) {
				setFooterErrorState();
				return;
			} else if (msg.state == MessageData.MESSAGE_STATE_FULL) {
				// 当页数少于要求的加载页数的时，可以判断是已经加载完，没有更多的数据
				setFooterFullState();
			} else if (msg.state == MessageData.MESSAGE_STATE_MORE) {
				// 有数据的情况下，底部显示“正在加载...”
				setFooterHasMoreState();
			}

			Result result = msg.result;
			if (mPage == 1) {
				int newdata = 0;
				mSumData = result.getPageSize();
				if (mAction == LISTVIEW_ACTION_REFRESH
						|| mAction == LISTVIEW_ACTION_UPDATE) {
					if (mDataList.size() > 0) {
						// 计算新增数据条数
						for (Data data1 : result.getList()) {
							boolean b = false;
							for (Data data2 : mDataList) {
								if (data1.getId().equalsIgnoreCase(
										data2.getId())) {
									b = true;
									break;
								}
							}
							if (!b) {
								newdata++;
							}
						}
					} else {
						newdata = result.getPageSize();
					}
					if (mAction == LISTVIEW_ACTION_REFRESH && !isPauseLife) {
						// 提示新添加的数据条数
						if (newdata > 0) {
							NewDataToast
									.makeText(
											getActivity(),
											getString(
													R.string.new_data_toast_message,
													newdata),
											mApplication.isAppSound()).show();
						} else {
							NewDataToast.makeText(getActivity(),
									getString(R.string.new_data_toast_none),
									false).show();
						}
					}
				}
				// 先清除原有数据
				mDataList.clear();
				// 加入最新的数据
				mDataList.addAll(result.getList());
			} else {
				mSumData += result.getPageSize();
				if (mDataList.size() > 0) {
					for (Data data1 : result.getList()) {
						boolean b = false;
						for (Data data2 : mDataList) {
							if (data1.getId().equalsIgnoreCase(data2.getId())) {
								b = true;
								break;
							}
						}
						if (!b) {
							mDataList.add(data1);
						}
					}
				} else {
					// 加入新增的数据
					mDataList.addAll(result.getList());
				}
			}
			// 通知listview去刷新界面
			mAdapter.notifyDataSetChanged();
		}
	}

	// 清空数据
	public void clearData() {
		mDataList.clear();
		mAdapter.notifyDataSetChanged();
	}
}