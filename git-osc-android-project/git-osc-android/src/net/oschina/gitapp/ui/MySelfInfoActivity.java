package net.oschina.gitapp.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.bean.UpLoadFile;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.FileUtils;
import net.oschina.gitapp.common.ImageUtils;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;

/**
 * 用户信息详情页面
 * @created 2014-07-01
 * @author 火蚁(http://my.oschina.net/LittleDY)
 *
 */
public class MySelfInfoActivity extends BaseActionBarActivity implements View.OnClickListener {
	
	private final static String FILE_SAVEPATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/OSChina/Git/Portrait/";
	private final static int CROP = 200;
	private Uri origUri;
	private Uri cropUri;
	private File protraitFile;
	private Bitmap protraitBitmap;
	private String protraitPath;
	
	private User mUser;
	
	private ImageView mUserFace;
	
	private TextView mUserName;
	
	private Button mEditFace;
	
	private View mFollowersLL;
	
	private TextView mFollowers;
	
	private View mStarredLL;
	
	private TextView mStarred;
	
	private View mFolloweringLL;
	
	private TextView mFollowering;
	
	private View mWatchedLL;
	
	private TextView mWatched;
	
	private TextView mJoinTime;
	
	private TextView mWeiBo;
	
	private TextView mBlog;
	
	private TextView mIntro;
	
	private Button mLoginOut;
	
	private AppContext mAppContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myselfinfo_detail);
		mAppContext = getGitApplication();
		initView();
		initData();
	}
	
	private void initView() {
		mUserFace = (ImageView) findViewById(R.id.user_info_userface);
		mUserName = (TextView) findViewById(R.id.user_info_username);
		mEditFace = (Button) findViewById(R.id.user_info_editer);
		
		mFollowersLL = findViewById(R.id.user_info_followers_ll);
		mFollowers = (TextView) findViewById(R.id.user_info_followers);
		
		mStarredLL = findViewById(R.id.user_info_starred_ll);
		mStarred = (TextView) findViewById(R.id.user_info_starred);
		
		mFolloweringLL = findViewById(R.id.user_info_following_ll);
		mFollowering = (TextView) findViewById(R.id.user_info_following);
		
		mWatchedLL = findViewById(R.id.user_info_watched_ll);
		mWatched = (TextView) findViewById(R.id.user_info_watched);
		
		mJoinTime = (TextView) findViewById(R.id.user_info_jointime);
		
		mWeiBo = (TextView) findViewById(R.id.user_info_weibo);
		
		mBlog = (TextView) findViewById(R.id.user_info_blog);
		
		mIntro = (TextView) findViewById(R.id.user_info_intro);
		
		mLoginOut = (Button) findViewById(R.id.user_info_logout);
		
		mEditFace.setOnClickListener(this);
		
//		mFollowersLL.setOnClickListener(this);
//		mStarredLL.setOnClickListener(this);
//		mFolloweringLL.setOnClickListener(this);
//		mWatchedLL.setOnClickListener(this);
		mLoginOut.setOnClickListener(this);
	}
	
	private void initData() {
		mUser = getGitApplication().getLoginInfo();
		if (mUser != null) {
			mUserName.setText(mUser.getName());
			
			mJoinTime.setText(mUser.getCreated_at().substring(0, 10));
			if (mUser.getWeibo().equals("null")) {
				
			} else {
				mWeiBo.setText(mUser.getWeibo());
			}
			if (mUser.getBlog().equals("null")) {
				
			} else {
				mBlog.setText(mUser.getBlog());
			}
			
			if (mUser.getBio().equals("null")) {
				
			} else {
				mIntro.setText(mUser.getBio());
			}
			
			String portrait = mUser.getPortrait() == null || mUser.getPortrait().equals("null") ? "" : mUser.getPortrait();
			if (portrait.endsWith("portrait.gif") || StringUtils.isEmpty(portrait)) {
				mUserFace.setImageResource(R.drawable.widget_dface);
			} else {
				// 加载用户头像
				String faceUrl = URLs.HTTPS + URLs.HOST + URLs.URL_SPLITTER + mUser.getPortrait();
				UIHelper.showUserFace(mUserFace, faceUrl);
			}
			mFollowers.setText(mUser.getFollow().getFollowers() + "");
			mStarred.setText(mUser.getFollow().getStarred() + "");
			mFollowering.setText(mUser.getFollow().getFollowing() + "");
			mWatched.setText(mUser.getFollow().getWatched() + "");
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		// 编辑用户头像
		case R.id.user_info_editer:
			CharSequence[] items = { getString(R.string.img_from_album),
					getString(R.string.img_from_camera) };
			imageChooseItem(items);
			break;
		case R.id.user_info_logout:
			loginOut();
			break;
		// 其他
		default:
			break;
		}
	}
	
	private void loginOut() {
		getGitApplication().loginout();
		BroadcastController.sendUserChangeBroadcase(getGitApplication());
		this.finish();
	}
	
	private void imageChooseItem(CharSequence[] items) {
		AlertDialog imageDialog = new AlertDialog.Builder(this)
		.setTitle("上传头像").setIcon(android.R.drawable.btn_star)
		.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// 相册选图
				if (item == 0) {
					startImagePick();
				}
				// 手机拍照
				else if (item == 1) {
					startActionCamera();
				}
			}
		}).create();

		imageDialog.show();
	}
	
	/**
	 * 选择图片裁剪
	 * 
	 * @param output
	 */
	private void startImagePick() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "选择图片"),
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}
	
	/**
	 * 相机拍照
	 * 
	 * @param output
	 */
	private void startActionCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getCameraTempFile());
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}
	
	// 拍照保存的绝对路径
	private Uri getCameraTempFile() {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			UIHelper.ToastMessage(getGitApplication(), "无法保存上传的头像，请检查SD卡是否挂载");
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		// 照片命名
		String cropFileName = "osc_camera_" + timeStamp + ".jpg";
		// 裁剪头像的绝对路径
		protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);
		cropUri = Uri.fromFile(protraitFile);
		this.origUri = this.cropUri;
		return this.cropUri;
	}
	
	// 裁剪头像的绝对路径
	private Uri getUploadTempFile(Uri uri) {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			UIHelper.ToastMessage(getGitApplication(), "无法保存上传的头像，请检查SD卡是否挂载");
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

		// 如果是标准Uri
		if (StringUtils.isEmpty(thePath)) {
			thePath = ImageUtils.getAbsoluteImagePath(MySelfInfoActivity.this, uri);
		}
		String ext = FileUtils.getFileFormat(thePath);
		ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
		// 照片命名
		String cropFileName = "osc_crop_" + timeStamp + "." + ext;
		// 裁剪头像的绝对路径
		protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);

		cropUri = Uri.fromFile(protraitFile);
		return this.cropUri;
	}
	
	/**
	 * 拍照后裁剪
	 * 
	 * @param data
	 *            原始图片
	 * @param output
	 *            裁剪后图片
	 */
	private void startActionCrop(Uri data) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", this.getUploadTempFile(data));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
	}
	
	/**
	 * 上传新照片
	 */
	private void uploadNewPhoto() {
		
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("正在上传头像...");
		
		new AsyncTask<Void, Void, Message>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				dialog.dismiss();
				if (msg.what == 1) {
					UpLoadFile file = (UpLoadFile) msg.obj;
					if (file == null) {
						Log.i("Test", "返回的结果为空");
					}
					//UIHelper.ToastMessage(mAppContext, file.getMsg());
					mUserFace.setImageBitmap(protraitBitmap);
				} else {
					UIHelper.ToastMessage(getGitApplication(), "上次头像失败");
				}
			}

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				// 获取头像缩略图
				if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
					protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath,
							200, 200);
				} else {
					UIHelper.ToastMessage(getActivity(), "图像不存在");
				}
				if (protraitBitmap != null && protraitFile != null) {
					try {
						UpLoadFile file = mAppContext.upLoad(protraitFile);
						msg.what = 1;
						msg.obj = file;
					} catch (AppException e) {
						dialog.dismiss();
						msg.what = -1;
						msg.obj = e;
					}
				}
				return msg;
			}
			
		}.execute();
	}
	
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
			startActionCrop(origUri);// 拍照后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
			startActionCrop(data.getData());// 选图后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
			uploadNewPhoto();// 上传新照片
			break;
		}
	}
}
