package net.oschina.gitapp.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ExploreListProjectAdapter;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

/**
 * 搜索项目界面
 * @created 2014-07-10
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
public class SearchActivity extends BaseActionBarActivity implements
		OnQueryTextListener, OnItemClickListener {

	private final int MESSAGESTATEFULL = 0;//已加载全部状态
	private final int MESSAGESTATEMORE = 1;//可以加载更多状态
	
	private SearchView mSearchView;

	private AppContext mAppContext;
	
	private InputMethodManager imm;

	private ProgressBar mLoading;

	private ListView mListView;

	private View mEmpty;

	private View mFooterView;
	
	private ProgressBar mFooterLoading;
	
	private TextView mFooterMsg;

	private List<Project> mData;

	private ExploreListProjectAdapter adapter;
	
	private String mKey;
	
	private int mMessageState = MESSAGESTATEMORE;
	
	private boolean isLoading = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		mAppContext = getGitApplication();
		initView();
		steupList();
	}

	private void initView() {
		mSearchView = (SearchView) findViewById(R.id.search_view);
		mLoading = (ProgressBar) findViewById(R.id.search_loading);
		mListView = (ListView) findViewById(R.id.search_list);
		mEmpty = findViewById(R.id.search_empty);
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mSearchView.setOnQueryTextListener(this);
		mSearchView.setIconifiedByDefault(false);
	}

	private void steupList() {
		mData = new ArrayList<Project>();
		adapter = new ExploreListProjectAdapter(mAppContext, mData,
				R.layout.exploreproject_listitem);
		mFooterView = LayoutInflater.from(mAppContext).inflate(
				R.layout.listview_footer, null);
		mFooterLoading = (ProgressBar) mFooterView.findViewById(R.id.listview_foot_progress);
		mFooterMsg = (TextView) mFooterView.findViewById(R.id.listview_foot_more);
		mFooterLoading.setVisibility(View.GONE);
		mFooterMsg.setText(R.string.load_more);
		mListView.addFooterView(mFooterView);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		mKey = arg0;
		load(arg0, 1);
		imm.hideSoftInputFromWindow(mListView.getWindowToken(), 0);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (adapter == null || mData == null || mData.isEmpty()) {
			return;
		}
		
		//点击了底部
		if(view == mFooterView) {
			//数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
			if(mMessageState == MessageData.MESSAGE_STATE_FULL
					|| mMessageState == MessageData.MESSAGE_STATE_EMPTY
					|| isLoading) {
				return;	
			}
			onLoadNextPage();
			return;
		}
		UIHelper.showProjectDetail(mAppContext, mData.get(position), null);
	}
		
	/** 加载下一页*/
	protected void onLoadNextPage() {
		// 当前pageIndex
		int pageIndex = mData.size() / 15 + 1;
		load(mKey, pageIndex);
	}
	
	/** 设置底部有更多数据的状态*/
	private void setFooterHasMoreState() {
		if(mFooterView != null) {
			mFooterLoading.setVisibility(View.GONE);
			mFooterMsg.setText(R.string.load_more);
		}
	}
	
	/** 设置底部已加载全部的状态*/
	private void setFooterFullState() {
		if(mFooterView != null) {
			mMessageState = MESSAGESTATEFULL;
			mFooterLoading.setVisibility(View.GONE);
			mFooterMsg.setText(R.string.load_full);
		}
	}
	
	/** 设置底部加载中的状态*/
	private void setFooterLoadingState() {
		if(mFooterView != null) {
			mFooterLoading.setVisibility(View.VISIBLE);
			mFooterMsg.setText(R.string.load_ing);
			mEmpty.setVisibility(View.GONE);
		}
	}
	
	private void load(final String key, final int page) {
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					msg.obj = mAppContext.getSearcheProject(key, page);
					msg.what = 1;
				} catch (AppException e) {
					isLoading = false;
					msg.what = -1;
					msg.obj = e;
					e.printStackTrace();
				}
				return msg;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				isLoading = true;
				mEmpty.setVisibility(View.GONE);
				if (page != 0 && page != 1) {
					setFooterLoadingState();
				} else {
					mLoading.setVisibility(View.VISIBLE);
					mListView.setVisibility(View.GONE);
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				mLoading.setVisibility(View.GONE);
				isLoading = false;
				if (msg.what == 1) {
					if (page == 1 || page == 0)
						mData.clear();
					List<Project> resuleData = (List<Project>) msg.obj;
					if (resuleData.size() > 0) {
						mEmpty.setVisibility(View.GONE);
						mData.addAll(resuleData);
						if (resuleData.size() != 15) {
							setFooterFullState();
						} else {
							setFooterHasMoreState();
						}
						adapter.notifyDataSetChanged();
						mListView.setVisibility(View.VISIBLE);
					} else {
						if (page == 1 || page == 0) {
							mListView.setVisibility(View.GONE);
							mEmpty.setVisibility(View.VISIBLE);
						}
					}
				} else {
					((AppException) msg.obj).makeToast(mAppContext);
				}
			}
		}.execute();
	}

}
