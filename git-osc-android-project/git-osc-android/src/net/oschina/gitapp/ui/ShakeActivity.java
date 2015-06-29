package net.oschina.gitapp.ui;

import java.io.IOException;
import java.util.HashMap;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.bean.LuckMsg;
import net.oschina.gitapp.bean.RandomProject;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActionBarActivity;
import net.oschina.gitapp.util.ShakeListener;
import net.oschina.gitapp.util.ShakeListener.OnShakeListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShakeActivity extends BaseActionBarActivity implements OnClickListener {
	
	private final int DURATION_TIME = 600;
	
	private AppContext mAppContext;
	
	private ShakeListener mShakeListener = null;
	
	private Vibrator mVibrator;
	
	private TextView mLuckMsg;
	
	private RelativeLayout mImgUp;
	
	private RelativeLayout mImgDn;

	private LinearLayout mLoaging;
	
	private RelativeLayout mShakeResProject;// 摇到项目
	
	private ImageView mProjectFace;
	
	private TextView mProjectTitle;
	
	private TextView mProjectDescription;
	
	private TextView mProjectLanguage;
	
	private TextView mProjectStarNums;
	
	private TextView mProjectForkNums;
	
	private RelativeLayout mShakeResAward;// 摇到奖品
	
	private ImageView mShakeResAwardImg;
	
	private TextView mShakeResAwardMsg;
	
	private RandomProject mProject;
	
	private SoundPool sndPool;
	private HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();
	
	private Bitmap mBitmap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake);
		mAppContext = getGitApplication();
		initView();
		// 加载近期的活动信息
		loadLuckMsg();
		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);

		loadSound();
		mShakeListener = new ShakeListener(this);
		// 监听到手机摇动
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				startAnim();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.shake_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.shake_menu_share:
			showShare();
			break;
		case R.id.shake_menu_edit_shippingaddress:
			showShippingAddressActivity();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showShare() {
		if (mProject == null) {
			UIHelper.getDialog(ShakeActivity.this, "温馨提示", "请先摇一摇再分享吧", "知道了").show();
		} else {
			String title = "摇项目，等你来！";
			String url = URLs.URL_HOST + mProject.getOwner().getUsername() + URLs.URL_SPLITTER + mProject.getName();
			String shareContent = "我在Git@OSC客户端中摇到《" + mProject.getOwner().getName() + "的项目" + mProject.getName() + "》你也来试试手气吧";
			UIHelper.showShareOption(getActivity(), title, url, shareContent, mBitmap);
		}
	}

	private void initView() {
		
		mLuckMsg = (TextView) findViewById(R.id.shake_luck_msg);
		
		mImgUp = (RelativeLayout) findViewById(R.id.shakeImgUp);
		mImgDn = (RelativeLayout) findViewById(R.id.shakeImgDown);
		
		mLoaging = (LinearLayout) findViewById(R.id.shake_loading);
		
		mShakeResProject = (RelativeLayout) findViewById(R.id.shakeres_paroject);
		
		mProjectFace = (ImageView) findViewById(R.id.exploreproject_listitem_userface);
		
		mProjectTitle = (TextView) findViewById(R.id.exploreproject_listitem_title);
		
		mProjectDescription = (TextView) findViewById(R.id.exploreproject_listitem_description);
		
		mProjectLanguage = (TextView) findViewById(R.id.exploreproject_listitem_language);
		
		mProjectStarNums = (TextView) findViewById(R.id.exploreproject_listitem_star);
		
		mProjectForkNums = (TextView) findViewById(R.id.exploreproject_listitem_fork);
		
		mShakeResAward = (RelativeLayout) findViewById(R.id.shakeres_award);
		
		mShakeResAwardImg = (ImageView) findViewById(R.id.shake_award_img);
		
		mShakeResAwardMsg = (TextView) findViewById(R.id.shake_award_msg);
		
		mShakeResProject.setOnClickListener(this);
	}

	private void loadSound() {

		sndPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
		new Thread() {
			public void run() {
				try {
					soundPoolMap.put(
							0,
							sndPool.load(
									getAssets().openFd(
											"sound/shake_sound_male.mp3"), 1));

					soundPoolMap.put(1, sndPool.load(
							getAssets().openFd("sound/shake_match.mp3"), 1));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void startAnim() {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimup0.setDuration(DURATION_TIME);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimup1.setDuration(DURATION_TIME);
		mytranslateanimup1.setStartOffset(DURATION_TIME);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimdn0.setDuration(DURATION_TIME);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimdn1.setDuration(DURATION_TIME);
		mytranslateanimdn1.setStartOffset(DURATION_TIME);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);
		
		// 动画监听，开始时显示加载状态，
		mytranslateanimdn0.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				mShakeResProject.setVisibility(View.GONE);
				mShakeListener.stop();
				sndPool.play(soundPoolMap.get(0), (float) 0.2, (float) 0.2, 0, 0,
						(float) 0.6);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				loadProject();
			}
		});
	}

	public void startVibrato() {
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.shakeres_paroject:
			if (mProject != null) {
				UIHelper.showProjectDetail(ShakeActivity.this, null, mProject.getId());
			}
			break;

		default:
			break;
		}
	}
	
	private void beforeLoading() {
		mLoaging.setVisibility(View.VISIBLE);
		mShakeResProject.setVisibility(View.GONE);
		mShakeResAward.setVisibility(View.GONE);
	}
	
	private void afterLoading() {
		mLoaging.setVisibility(View.GONE);
		sndPool.play(soundPoolMap.get(1), (float) 0.2, (float) 0.2,
				0, 0, (float) 0.6);
		mVibrator.cancel();
		mShakeListener.start();
	}
	
	private void loadProject() {
		new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				try {
					msg.obj = mAppContext.getRandomProject();
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
				beforeLoading();
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				afterLoading();
				if (msg.what == 1) {
					mProject = (RandomProject) msg.obj;
					if (mProject !=null && mProject.getRand_num() == 0) {
						mShakeResProject.setVisibility(View.VISIBLE);
						// 没中奖的处理方式
						mProjectTitle.setText(mProject.getOwner().getName() + "/" + mProject.getName());
						mProjectDescription.setText(mProject.getDescription());
						mProjectStarNums.setText(mProject.getStars_count() + "");
						mProjectForkNums.setText(mProject.getForks_count() + "");
						if (mProject.getLanguage() != null) {
							mProjectLanguage.setText(mProject.getLanguage());
						} else {
							mProjectLanguage.setVisibility(View.GONE);
							findViewById(R.id.exploreproject_listitem_language_image).setVisibility(View.GONE);
						}
						mProjectFace.setImageResource(R.drawable.widget_dface_loading);
						String faceUrl = mProject.getOwner().getNew_portrait();
						if (faceUrl.endsWith(".gif") || StringUtils.isEmpty(faceUrl)) {
							mProjectFace.setImageResource(R.drawable.mini_avatar);
						} else {
							UIHelper.showUserFace(mProjectFace, faceUrl);
						}
						
						Handler handle = new Handler();
						handle.postDelayed(new Runnable() {
							@Override
							public void run() {
								mBitmap = UIHelper.takeScreenShot(getActivity());
							}
						}, 500);
						
					} else if (mProject !=null) {
						mShakeListener.stop();
						mShakeResAward.setVisibility(View.VISIBLE);
						UIHelper.showUserFace(mShakeResAwardImg, mProject.getImg());
						mShakeResAwardMsg.setText(mProject.getMsg());
						
						AlertDialog.Builder dialog = new Builder(ShakeActivity.this);
						dialog.setCancelable(false);
						dialog.setTitle("恭喜您，摇到奖品啦！！！");
						dialog.setMessage(Html.fromHtml("获得：" + mProject.getMsg() + "<br><br>温馨提示：<br>请完善您的收货信息，方便我们给您邮寄奖品"));
						dialog.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mShakeListener.start();
								showShippingAddressActivity();
							}
						});
						dialog.setPositiveButton("分享", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String url = "http://t.cn/RhLDd4k";
								Bitmap bitmap = UIHelper.takeScreenShot(getActivity());
								UIHelper.showShareOption(getActivity(), "我摇到奖品啦", url, mProject.getMsg(), bitmap);
							}
						});
						dialog.show();
					}
				} else {
					UIHelper.ToastMessage(mAppContext, "红薯跟你开了个玩笑，没有为你找到项目");
				}
			}
			
		}.execute();
	}
	
	/**
	 * 显示编辑用户收货地址的操作
	 */
	private void showShippingAddressActivity() {
		if (!mAppContext.isLogin()) {
			UIHelper.showLoginActivity(getActivity());
			return;
		}
		Intent intent = new Intent(ShakeActivity.this, ShippingAddressActivity.class);
		startActivity(intent);
	}
	
	private void loadLuckMsg() {
		new AsyncTask<Void, Void, LuckMsg>() {

			@Override
			protected LuckMsg doInBackground(Void... params) {
				LuckMsg res = null;
				try {
					res = ApiClient.getLuckMsg(mAppContext);
				} catch (AppException e) {
					e.printStackTrace();
				}
				
				return res;
			}

			@Override
			protected void onPostExecute(LuckMsg res) {
				super.onPostExecute(res);
				if (res != null && res.getMessage() != null) {
					mLuckMsg.setVisibility(View.VISIBLE);
					mLuckMsg.setText(res.getMessage());
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			
		}.execute();
	}
}