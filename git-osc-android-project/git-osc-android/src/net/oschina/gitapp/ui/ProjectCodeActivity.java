package net.oschina.gitapp.ui;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ProjectCodeTreeListAdapter;
import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.bean.Branch;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.MoreMenuItem;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.DataRequestThreadHandler;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.util.TypefaceUtils;
import net.oschina.gitapp.widget.DropDownMenu;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.MenuItemCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 项目代码列表
 * 
 * @created 2014-07-18
 * @author 火蚁（http://my.oschina.net/LittleDY）
 */
public class ProjectCodeActivity extends BaseActionBarActivity implements OnItemClickListener, OnItemLongClickListener, OnClickListener
{

	private final int					MORE_MENU_OPENDEFAULT		= 00;								// 分享
	private final int					MORE_MENU_OPEN_WITH_BROWS	= 01;								// 在浏览器中打开

	private final int					MENU_REFRESH_ID				= 0;

	private static final int			ACTION_INIT					= 0;								// 初始化
	private static final int			ACTION_REFRESH				= 1;								// 刷新
	private static final int			ACTION_LOADING_TREE			= 2;								// 加载代码层级树
	private static final int			ACTION_PRE_TREE				= 3;								// 前一级代码树

	private Menu						optionsMenu;

	private Project						mProject;

	private ListView					mCodeTree;

	private LinearLayout				mSwitch_branch;

	private TextView					mBranchIcon;

	private TextView					mBranchName;

	private ProjectCodeTreeListAdapter	mAdapter;

	private Stack<List<CodeTree>>		mCodeFolders				= new Stack<List<CodeTree>>();

	private List<CodeTree>				mTrees;

	private CodeTree					mOnCilkCodeTree;

	private String						mPath						= "";

	private String						mBranch;

	private AppContext					mAppContext;

	private LinearLayout				mContentLayout;

	// 上一级目录
	private LinearLayout				mCodeTreePreFolder;

	private TextView					mCodeFloders;

	private ProgressBar					mLoading;

	private ProgressDialog				mLoadBranch;

	private List<Branch>				mBranchList					= new ArrayList<Branch>();			// 标签和分支列表

	private int							mBranchIndex				= 0;

	private AlertDialog.Builder			dialog;

	private DataRequestThreadHandler	mRequestThreadHandler		= new DataRequestThreadHandler();

	private DropDownMenu				mMoreMenuWindow;

	private List<MoreMenuItem>			mMoreItems					= new ArrayList<MoreMenuItem>();

	private View.OnClickListener		onMoreMenuItemClick			= new OnClickListener() {

																		@Override
																		public void onClick(View v)
																		{
																			if (mMoreMenuWindow != null && mMoreMenuWindow.isShowing())
																			{
																				mMoreMenuWindow.dismiss();
																			}
																			if (mProject == null)
																			{
																				return;
																			}
																			if (!mProject.isPublic())
																			{
																				UIHelper.ToastMessage(mAppContext, "私有项目不支持该操作");
																				return;
																			}
																			int id = v.getId();
																			switch (id)
																			{
																				case MORE_MENU_OPENDEFAULT:
																					showDetail(mOnCilkCodeTree.getName(), mBranch);
																					break;
																				case MORE_MENU_OPEN_WITH_BROWS:
																					openWithBrowser(mOnCilkCodeTree);
																					break;
																				default:
																					break;
																			}
																		}
																	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.projectcode_fragment);
		mAppContext = getGitApplication();
		Intent intent = getIntent();
		if (intent != null)
		{
			mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
			mTitle = "代码列表";
			mSubTitle = mProject.getOwner().getName() + "/" + mProject.getName();
		}
		mBranch = "master";
		initView();
		setupListView();
		loadDatas("", "master", ACTION_INIT);
		TypefaceUtils.setOcticons(mBranchIcon);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mRequestThreadHandler.quit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		optionsMenu = menu;
		// 刷新按钮
		MenuItem refreshItem = menu.add(0, MENU_REFRESH_ID, MENU_REFRESH_ID, "刷新");
		refreshItem.setIcon(R.drawable.abc_ic_menu_refresh);
		MenuItemCompat.setShowAsAction(refreshItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	private void initMoreOption()
	{
		MoreMenuItem openDefault = new MoreMenuItem(MORE_MENU_OPENDEFAULT, R.drawable.more_menu_icon_app, "在该app中打开");
		mMoreItems.add(openDefault);

		MoreMenuItem open_with_brows = new MoreMenuItem(MORE_MENU_OPEN_WITH_BROWS, R.drawable.more_menu_icon_browser, "在浏览器中打开");
		mMoreItems.add(open_with_brows);

		mMoreMenuWindow.addItems(mMoreItems);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		int id = item.getItemId();
		switch (id)
		{
			case MENU_REFRESH_ID:
				loadDatas(mPath, mBranch, ACTION_REFRESH);
				break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (!StringUtils.isEmpty(mPath))
			{
				loadPreFolder();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView()
	{

		mContentLayout = (LinearLayout) findViewById(R.id.projectcode_content_layout);

		mCodeTreePreFolder = (LinearLayout) findViewById(R.id.projectcode_tree_prefolder);

		mCodeFloders = (TextView) findViewById(R.id.projectcode_floders);

		mCodeFloders.setMovementMethod(LinkMovementMethod.getInstance());

		TypefaceUtils.setOcticons((TextView) findViewById(R.id.projectcode_pre_img));

		// mCodeTreePreFolder.setOnClickListener(this);

		mLoading = (ProgressBar) findViewById(R.id.projectcode_loading);

		mCodeTree = (ListView) findViewById(R.id.projectcode_tree);
		mSwitch_branch = (LinearLayout) findViewById(R.id.projectcode_switch_branch);
		mBranchIcon = (TextView) findViewById(R.id.projectcode_branch_icon);
		mBranchName = (TextView) findViewById(R.id.projectcode_branch_name);
	}

	private void setupListView()
	{
		mTrees = new ArrayList<CodeTree>();
		mAdapter = new ProjectCodeTreeListAdapter(getGitApplication(), mTrees, R.layout.projectcodetree_listitem);
		mCodeTree.setAdapter(mAdapter);
		mCodeTree.setOnItemClickListener(this);
		mCodeTree.setOnItemLongClickListener(this);
		mSwitch_branch.setOnClickListener(this);
	}

	private void loadDatas(final String path, final String ref_name, int action)
	{
		mRequestThreadHandler.request(0, new AsyncDataHandler(path, ref_name, action));
	}

	/**
	 * 加载上一级代码树
	 */
	private void loadPreFolder()
	{
		mTrees.clear();
		mCodeFolders.pop();
		mTrees.addAll(mCodeFolders.peek());
		mAdapter.notifyDataSetChanged();
		savePathAndBranch(getPrePath(), mBranch);
	}

	private void beforeLoading(int action)
	{
		if (action == ACTION_REFRESH)
		{
			mContentLayout.setVisibility(View.GONE);
			mSwitch_branch.setVisibility(View.GONE);
			mLoading.setVisibility(View.VISIBLE);
		}
		else if (action != ACTION_INIT)
		{
			MenuItemCompat.setActionView(optionsMenu.findItem(MENU_REFRESH_ID), R.layout.actionbar_indeterminate_progress);
		}
	}

	private void afterLoading(int action)
	{
		if (action == ACTION_INIT || action == ACTION_REFRESH)
		{
			mContentLayout.setVisibility(View.VISIBLE);
			mSwitch_branch.setVisibility(View.VISIBLE);
			mLoading.setVisibility(View.GONE);
		}
		else
		{
			MenuItemCompat.setActionView(optionsMenu.findItem(MENU_REFRESH_ID), null);
		}
	}

	/**
	 * 记录路径和分支
	 * 
	 * @param _mPath
	 * @param _mRef_name
	 */
	private void savePathAndBranch(String path, String branch)
	{
		mPath = path;
		mBranch = branch;
		if (StringUtils.isEmpty(mPath))
		{
			mCodeTreePreFolder.setVisibility(View.GONE);
		}
		else
		{
			mCodeTreePreFolder.setVisibility(View.VISIBLE);
		}
		String floders = mProject.getName() + (StringUtils.isEmpty(mPath) ? "" : "/" + mPath);
		PathString ps = new PathString(floders);
		mCodeFloders.setText(ps);
	}

	// 加载代码树
	private class AsyncDataHandler implements DataRequestThreadHandler.AsyncDataHandler<Message>
	{

		private String	_mPath;
		private String	_mRef_name;
		private int		_mAction;

		AsyncDataHandler(String path, String ref_name, int action)
		{
			_mPath = path;
			_mRef_name = ref_name;
			_mAction = action;
		}

		// 加载前
		@Override
		public void onPreExecute()
		{
			beforeLoading(_mAction);
		}

		// 加载ing
		@Override
		public Message execute()
		{
			Message msg = new Message();
			try
			{
				boolean refresh = true;
				if (_mAction == ACTION_INIT)
				{
					refresh = false;
				}

				CommonList<CodeTree> list = mAppContext.getProjectCodeTree(StringUtils.toInt(mProject.getId()), _mPath, _mRef_name, refresh);
				List<CodeTree> tree = list.getList();

				msg.what = 1;
				msg.obj = tree;
			}
			catch (Exception e)
			{
				msg.what = -1;
				msg.obj = e;
			}
			return msg;
		}

		// 加载完成
		@SuppressWarnings("unchecked")
		@Override
		public void onPostExecute(Message result)
		{
			afterLoading(_mAction);
			if (result.what == 1 && result.obj != null)
			{
				mTrees.clear();
				mTrees.addAll((List<CodeTree>) result.obj);
				// 加载成功，记录相关信息
				if (_mAction == ACTION_LOADING_TREE || _mAction == ACTION_PRE_TREE)
				{
					savePathAndBranch(_mPath, _mRef_name);
				}
				mAdapter.notifyDataSetChanged();
				mCodeFolders.push((List<CodeTree>) result.obj);
			}
			else
			{
				if (result.obj instanceof AppException)
				{
					((AppException) result.obj).makeToast(mAppContext);
				}
				else
				{
					UIHelper.ToastMessage(getActivity(), ((Exception) result.obj).getMessage());
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{

		position -= mCodeTree.getHeaderViewsCount();

		mOnCilkCodeTree = mTrees.get(position);
		if (mOnCilkCodeTree.getType().equalsIgnoreCase(CodeTree.TYPE_TREE))
		{
			loadDatas(getPath(mOnCilkCodeTree.getName()), mBranch, ACTION_LOADING_TREE);
		}
		else
		{
			checkShow(mOnCilkCodeTree);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		position -= mCodeTree.getHeaderViewsCount();
		// 震动
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(80);
		mOnCilkCodeTree = mTrees.get(position);
		if (mOnCilkCodeTree.getType().equalsIgnoreCase(CodeTree.TYPE_BLOB))
		{
			if (mMoreMenuWindow == null)
			{
				mMoreMenuWindow = new DropDownMenu(ProjectCodeActivity.this, onMoreMenuItemClick);
				initMoreOption();
				mMoreMenuWindow.getViewGroup().setPadding(0, 0, 0, 0);
			}
			mMoreMenuWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
		}
		return true;
	}

	// 获得访问的路径
	private String getPath(String path)
	{
		if (mPath == null || StringUtils.isEmpty(mPath))
		{
			return path;
		}
		else
		{
			return mPath + "/" + path;
		}
	}

	// 获得上一级路径
	private String getPrePath()
	{
		int index = mPath.lastIndexOf("/");

		return index != -1 ? mPath.substring(0, index) : "";
	}

	/**
	 * 判断code的文件的类型显示不同的操作
	 * 
	 * @param codeTree
	 */
	@SuppressWarnings("deprecation")
	private void checkShow(CodeTree codeTree)
	{

		String fileName = codeTree.getName();

		if (isCodeTextFile(fileName))
		{

			showDetail(fileName, mBranch);

		}
		else if (isImage(fileName))
		{
			String url = URLs.URL_HOST + mProject.getOwner().getUsername() + URLs.URL_SPLITTER + mProject.getPath() + URLs.URL_SPLITTER + "raw" + URLs.URL_SPLITTER + mBranch + URLs.URL_SPLITTER + URLEncoder.encode(getFilePath(fileName)) + "?private_token=" + ApiClient.getToken(mAppContext);
			UIHelper.showImageZoomActivity(ProjectCodeActivity.this, url);
		}
		else
		{
			openWithBrowser(codeTree);
		}
	}

	/**
	 * 在浏览器中在打开
	 * 
	 * @param codeTree
	 */
	@SuppressWarnings("deprecation")
	private void openWithBrowser(CodeTree codeTree)
	{
		String url = URLs.URL_HOST + mProject.getOwner().getUsername() + URLs.URL_SPLITTER + mProject.getPath() + URLs.URL_SPLITTER + "blob" + URLs.URL_SPLITTER + mBranch + URLs.URL_SPLITTER + URLEncoder.encode(getFilePath(codeTree.getName())) + "?private_token=" + ApiClient.getToken(mAppContext);
		UIHelper.openBrowser(ProjectCodeActivity.this, url);
	}

	// 判断是不是代码文件
	private boolean isCodeTextFile(String fileName)
	{
		boolean res = false;
		// 文件的后缀
		int index = fileName.lastIndexOf(".");
		if (index > 0)
		{
			fileName = fileName.substring(index);
		}
		String codeFileSuffix[] = new String[]
		{
				".java",
				".confg",
				".ini",
				".xml",
				".json",
				".txt",
				".go",
				".php",
				".php3",
				".php4",
				".php5",
				".js",
				".css",
				".html",
				".properties",
				".c",
				".hpp",
				".h",
				".hh",
				".cpp",
				".cfg",
				".rb",
				".example",
				".gitignore",
				".project",
				".classpath",
				".m",
				".md",
				".rst",
				".vm",
				".cl",
				".py",
				".pl",
				".haml",
				".erb",
				".scss",
				".bat",
				".coffee",
				".as",
				".sh",
				".m",
				".pas",
				".cs",
				".groovy",
				".scala",
				".sql",
				".bas",
				".xml",
				".vb",
				".xsl",
				".swift",
				".ftl",
				".yml",
				".ru",
				".jsp",
				".markdown",
				".cshap",
				".apsx",
				".sass",
				".less",
				".ftl",
				".haml",
				".log",
				".tx",
				".csproj",
				".sln",
				".clj",
				".scm",
				".xhml",
				".xaml",
				".lua"
		};
		for (String string : codeFileSuffix)
		{
			if (fileName.equalsIgnoreCase(string))
			{
				res = true;
			}
		}

		// 特殊的文件
		String fileNames[] = new String[]
		{
				"LICENSE", "TODO", "README", "readme", "makefile", "gemfile", "gemfile.*", "gemfile.lock", "CHANGELOG"
		};

		for (String string : fileNames)
		{
			if (fileName.equalsIgnoreCase(string))
			{
				res = true;
			}
		}

		return res;
	}

	// 判断是否是图片
	private boolean isImage(String fileName)
	{
		boolean res = false;
		// 图片后缀
		int index = fileName.lastIndexOf(".");
		if (index > 0)
		{
			fileName = fileName.substring(index);
		}
		String imageSuffix[] = new String[]
		{
				".png", ".jpg", ".jpeg", ".jpe", ".bmp", ".exif", ".dxf", ".wbmp", ".ico", ".jpe", ".gif", ".pcx", ".fpx", ".ufo", ".tiff", ".svg", ".eps", ".ai", ".tga", ".pcd", ".hdri"
		};
		for (String string : imageSuffix)
		{
			if (fileName.equalsIgnoreCase(string))
			{
				res = true;
			}
		}
		return res;
	}

	// 查看代码文件详情
	private void showDetail(String fileName, String ref)
	{
		Intent intent = new Intent(getGitApplication(), CodeFileDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, mProject);
		bundle.putString("fileName", fileName);
		bundle.putString("path", getFilePath(fileName));
		bundle.putString("ref", ref);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 获得文件的访问路径
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFilePath(String fileName)
	{
		return mPath == null || StringUtils.isEmpty(mPath) ? fileName : mPath + "/" + fileName;
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
			case R.id.projectcode_switch_branch:
				switchBranch();
				break;
			// case R.id.projectcode_tree_prefolder:
			// if (StringUtils.isEmpty(mPath))
			// {
			// return;
			// }
			// loadPreFolder();
			// break;
			default:
				break;
		}
	}

	private void switchBranch()
	{
		if (mProject == null)
		{
			return;
		}
		if (dialog == null || mBranchList.isEmpty())
		{
			loadBranchAndTags(true);
		}
		else
		{
			loadBranchAndTags(false);
		}
	}

	// 加载分支和标签数据
	private void loadBranchAndTags(final boolean isRefalsh)
	{

		// 异步
		new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params)
			{
				Message msg = new Message();
				try
				{
					msg.what = 1;
					if (!isRefalsh)
					{
						return msg;
					}
					// 1.加载分支
					CommonList<Branch> branchs = mAppContext.getProjectBranchsOrTagsLsit(mProject.getId(), 1, Branch.TYPE_BRANCH, isRefalsh);
					for (Branch branch : branchs.getList())
					{
						// 设置为分支类型
						branch.setType(Branch.TYPE_BRANCH);
						mBranchList.add(branch);
					}

					// 2.加载标签
					CommonList<Branch> tags = mAppContext.getProjectBranchsOrTagsLsit(mProject.getId(), 1, Branch.TYPE_TAG, isRefalsh);
					for (Branch branch : tags.getList())
					{
						// 设置为标签类型
						branch.setType(Branch.TYPE_TAG);
						mBranchList.add(branch);
					}
				}
				catch (Exception e)
				{
					msg.what = -1;
					msg.obj = e;
				}
				return msg;
			}

			@Override
			protected void onPreExecute()
			{
				if (mLoadBranch == null)
				{
					mLoadBranch = new ProgressDialog(getActivity());
					mLoadBranch.setCancelable(true);
					mLoadBranch.setCanceledOnTouchOutside(false);
					mLoadBranch.setMessage("加载分支和标签...");
					mLoadBranch.setProgressStyle(R.style.Spinner);
					mLoadBranch.show();
				}
				if (dialog == null)
				{
					dialog = new AlertDialog.Builder(getActivity()).setTitle("选择分支或标签");
				}
			}

			@Override
			protected void onPostExecute(Message msg)
			{
				if (mLoadBranch != null) mLoadBranch.dismiss();
				if (msg.what == 1)
				{
					final String baArrays[] = new String[mBranchList.size()];
					for (int i = 0; i < mBranchList.size(); i++)
					{
						baArrays[i] = mBranchList.get(i).getName();
						if (baArrays[i].equals(mBranch))
						{
							mBranchIndex = i;
						}
					}
					dialog.setSingleChoiceItems(baArrays, mBranchIndex, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							if (which == mBranchIndex) return;
							mBranchIndex = which;
							mBranch = baArrays[which];
							mPath = "";
							loadDatas(mPath, mBranch, ACTION_REFRESH);
							mBranchName.setText(mBranch);
						}
					}).setNegativeButton("取消", null);
					dialog.show();
				}
				else
				{
					if (msg.obj instanceof AppException)
					{
						((AppException) msg.obj).makeToast(getActivity());
					}
					else
					{
						UIHelper.ToastMessage(mAppContext, "加载分支和标签失败");
					}
				}
			}
		}.execute();
	}

	public void onPathClick(String path, int index)
	{
		// loadDatas(path, mBranch, ACTION_LOADING_TREE);
		// 使用缓存数据
		mTrees.clear();

		// 移除当前层级之后的数据
		for (int i = 0, count = mCodeFolders.size() - index; i < count - 1; i++)
		{
			mCodeFolders.pop();
		}

		mTrees.addAll(mCodeFolders.peek());
		mAdapter.notifyDataSetChanged();
		savePathAndBranch(path, mBranch);
	}

	class PathString extends SpannableString
	{
		public PathString(String text)
		{
			super(text);
			setup(text);
		}

		private void setup(String text)
		{
			int start = 0;
			int chatIndex = text.indexOf(File.separatorChar);
			int pathStart = chatIndex + 1;// 路径String位置，text最开始为工程名称，不包含在Path内，所以标注开始位置用于截取Path
			int pathIndex = 0;// 标注层级，用于获取缓存
			while (chatIndex >= 0)
			{
				String path = chatIndex > pathStart ? text.substring(pathStart, chatIndex) : "";
				setSpan(new Clickable(path, pathIndex), start, chatIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				pathIndex++;
				start = chatIndex + 1;
				chatIndex = text.indexOf(File.separatorChar, start);
			}
		}

		class Clickable extends ClickableSpan
		{
			private final String	mPath;
			private final int		mIndex;

			public Clickable(String path, int index)
			{
				mPath = path;
				mIndex = index;
			}

			@Override
			public void onClick(View widget)
			{
				onPathClick(mPath, mIndex);
			}
		}
	}
}
