package net.oschina.gitapp.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ExploreListProjectAdapter;
import net.oschina.gitapp.adapter.LanguageListAdapter;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Language;
import net.oschina.gitapp.bean.MessageData;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.DataRequestThreadHandler;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

public class LanguageActivity extends BaseActionBarActivity implements  
	android.support.v7.app.ActionBar.OnNavigationListener, OnItemClickListener{
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private AppContext mContext;
	
	private ListView mListView;
	
	private View mFooterView;
	
	private TextView mFooterTextView;
	
	private ProgressBar mFooterProgressBar;
	
	private List<Project> mProjects;
	
	private ExploreListProjectAdapter mProjectAdapter;
	
	private List<Language> mLanguages;
	
	private LanguageListAdapter mLanguageAdapter;
	
	private ProgressBar mLoading;
	
	private DataRequestThreadHandler mRequestThreadHandler = new DataRequestThreadHandler();
	
	private int mMessageState = MessageData.MESSAGE_STATE_MORE;
	
	private String mLanguageId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language);
		mContext = getGitApplication();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		initView();
		initData();
		steupList();
		loadLanguagesList();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mRequestThreadHandler.quit();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}
	
	private void initView() {
		mLoading = (ProgressBar) findViewById(R.id.loading);
		mListView = (ListView) findViewById(R.id.project_list);
		
		mFooterView = LayoutInflater.from(mContext).inflate(R.layout.listview_footer, null);
		mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.listview_foot_progress);
		mFooterTextView = (TextView) mFooterView.findViewById(R.id.listview_foot_more);
	}
	
	private void initData() {
		mProjects = new ArrayList<Project>();
		mProjectAdapter = new ExploreListProjectAdapter(mContext, mProjects, R.layout.exploreproject_listitem);
		
		mLanguages = new ArrayList<Language>();
		mLanguageAdapter = new LanguageListAdapter(mContext, mLanguages, R.layout.languages);
		mActionBar.setListNavigationCallbacks(mLanguageAdapter, this);
	}
	
	private void steupList() {
		mListView.addFooterView(mFooterView);
		mListView.setAdapter(mProjectAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	private void beforeLoading(int page) {
		if (page == 1 || page == 0) {
			mLoading.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			mProjects.clear();
		} else {
			setFooterLoadingState();
		}
	}
	
	private void afterLoading(boolean isEmpty) {
		mLoading.setVisibility(View.GONE);
		if (isEmpty) {
			
		} else {
			mListView.setVisibility(View.VISIBLE);
		}
	}
	
	private void loadProjects(final String languageId, final int page) {
		if (mLanguages.isEmpty()) {
			setFooterNotLanguages();
			return;
		}
		mRequestThreadHandler.request(page, new AsyncDataHandler(languageId, page));
	}
	
	void setFooterNotLanguages() {
		if(mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText("没有加载到语言列表");
		}
	}
	
	/** 设置底部有错误的状态*/
	void setFooterErrorState() {
		if(mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_error);
		}
	}
	
	/** 设置底部有更多数据的状态*/
	void setFooterHasMoreState() {
		if(mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_more);
		}
	}
	
	/** 设置底部已加载全部的状态*/
	void setFooterFullState() {
		if(mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_full);
		}
	}
	
	/** 设置底部无数据的状态*/
	void setFooterNoMoreState() {
		if(mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_empty);
		}
	}
	
	/** 设置底部加载中的状态*/
	void setFooterLoadingState() {
		if(mFooterView != null) {
			mFooterProgressBar.setVisibility(View.VISIBLE);
			mFooterTextView.setText(R.string.load_ing);
		}
	}
	
	class AsyncDataHandler implements DataRequestThreadHandler.AsyncDataHandler<Message> {
		
		private int page;
		
		private String languageId;
		
		AsyncDataHandler(String languageId, int page) {
			this.page = page;
			this.languageId = languageId;
		}
		
		@Override
		public void onPreExecute() {
			beforeLoading(page);
		}

		@Override
		public Message execute() {
			Message msg = new Message();
			try {
				msg.obj = mContext.getLanguageProjectList(languageId, page);
				msg.what = 1;
			} catch (AppException e) {
				msg.what = -1;
				msg.obj = e;
				e.printStackTrace();
			}
			return msg;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onPostExecute(Message msg) {
			afterLoading(false);
			if (msg.what == 1) {
				List<Project> projects = (List<Project>) msg.obj;
				if (projects != null) {
					
					if (projects.size() < 20) {
						mMessageState = MessageData.MESSAGE_STATE_FULL;
						if (page == 1 && projects.size() == 0) {
							setFooterNoMoreState();
						} else {
							setFooterFullState();
						}
						
					} else {
						setFooterHasMoreState();
					}
					mProjects.addAll(projects);
					mProjectAdapter.notifyDataSetChanged();
				}
			} else {
				setFooterErrorState();
				((AppException)msg.obj).makeToast(mContext);
			}
		}
	}
	
	// 加载语言列表
	private void loadLanguagesList() {
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					msg.obj = mContext.getLanguageList();
					msg.what = 1;
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
				beforeLoading(0);
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				afterLoading(false);
				if (msg.what == 1) {
					CommonList<Language> languages = (CommonList<Language>) msg.obj;
					if (languages != null && languages.getCount() > 0) {
						mLanguages.addAll(languages.getList());
						mLanguageAdapter.notifyDataSetChanged();
					} else {
						setFooterNotLanguages();
					}
				} else {
					
				}
			}
			
		}.execute();
	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		Language language = mLanguages.get(arg0);
		mLanguageId = language.getId();
		mMessageState = MessageData.MESSAGE_STATE_MORE;
		loadProjects(mLanguageId, 1);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (view == mFooterView) {
			if (mMessageState != MessageData.MESSAGE_STATE_FULL) {
				// 当前pageIndex
				int pageIndex = mProjects.size() / AppContext.PAGE_SIZE + 1;
				loadProjects(mLanguageId, pageIndex);
			}
			return;
		}
		Project p = mProjects.get(position);
		UIHelper.showProjectDetail(mContext, p, p.getId());
	}
}
