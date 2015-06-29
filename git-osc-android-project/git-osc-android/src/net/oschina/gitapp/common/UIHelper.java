package net.oschina.gitapp.common;

import static net.oschina.gitapp.common.Contanst.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

//import com.umeng.socialize.bean.SHARE_MEDIA;
//import com.umeng.socialize.controller.UMServiceFactory;
//import com.umeng.socialize.controller.UMSocialService;
//import com.umeng.socialize.media.QQShareContent;
//import com.umeng.socialize.media.SinaShareContent;
//import com.umeng.socialize.media.UMImage;
//import com.umeng.socialize.sso.SinaSsoHandler;
//import com.umeng.socialize.sso.UMQQSsoHandler;
//import com.umeng.socialize.weixin.controller.UMWXHandler;
//import com.umeng.socialize.weixin.media.CircleShareContent;
//import com.umeng.socialize.weixin.media.WeiXinShareContent;

import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.AppManager;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.ApiClient;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.ui.CodeFileDetailActivity;
import net.oschina.gitapp.ui.CommitDetailActivity;
import net.oschina.gitapp.ui.CommitFileDetailActivity;
import net.oschina.gitapp.ui.ImageZoomActivity;
import net.oschina.gitapp.ui.IssueDetailActivity;
import net.oschina.gitapp.ui.IssueEditActivity;
import net.oschina.gitapp.ui.LoginActivity;
import net.oschina.gitapp.ui.MainActivity;
import net.oschina.gitapp.ui.NotificationActivity;
import net.oschina.gitapp.ui.ProjectActivity;
import net.oschina.gitapp.ui.ProjectCodeActivity;
import net.oschina.gitapp.ui.ProjectSomeInfoListActivity;
import net.oschina.gitapp.ui.ProjectReadMeActivity;
import net.oschina.gitapp.ui.SearchActivity;
import net.oschina.gitapp.ui.MySelfInfoActivity;
import net.oschina.gitapp.ui.ShakeActivity;
import net.oschina.gitapp.ui.UserInfoActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {

	/** 表情图片匹配 */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:14px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context context,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setCancelable(false);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //模拟器
						i.setType("message/rfc822"); // 真机
						// 接收错误报告的邮箱地址
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "zhangdeyi@oschina.net" });
						i.putExtra(Intent.EXTRA_SUBJECT,
								"GIT@OSC,Android客户端 - 错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						context.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getAppManager().AppExit(context);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(context);
					}
				});
		builder.show();
	}

	/**
	 * 点击返回监听事件
	 * 
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}
	
	public static AlertDialog.Builder getDialog(Context context, String title, String message, String... buttonStrs) {
		AlertDialog.Builder dialog = new Builder(context);
		dialog.setCancelable(false);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton(buttonStrs[0], new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		return dialog;
	}

	/**
	 * 分析并组合动态的标题
	 * 
	 * @param author_name
	 *            动态作者的名称
	 * @param pAuthor_And_pName
	 *            项目的作者和项目名
	 * @param eventTitle
	 *            事件的title（Issue或者pr或分支）
	 * @return
	 */
	public static SpannableString parseEventTitle(String author_name,
			String pAuthor_And_pName, Event event) {
		String title = "";
		String eventTitle = "";
		int action = event.getAction();
		switch (action) {
		case Event.EVENT_TYPE_CREATED:// 创建了issue
			eventTitle = event.getTarget_type() + getEventsTitle(event);
			title = "在项目 " + pAuthor_And_pName + " 创建了 " + eventTitle;
			break;
		case Event.EVENT_TYPE_UPDATED:// 更新项目
			title = "更新了项目 " + pAuthor_And_pName;
			break;
		case Event.EVENT_TYPE_CLOSED:// 关闭项目
			eventTitle = event.getTarget_type() + getEventsTitle(event);
			title = "关闭了项目 " + pAuthor_And_pName + " 的 " + eventTitle;
			break;
		case Event.EVENT_TYPE_REOPENED:// 重新打开了项目
			eventTitle = event.getTarget_type() + getEventsTitle(event);
			title = "重新打开了项目 " + pAuthor_And_pName + " 的 " + eventTitle;
			break;
		case Event.EVENT_TYPE_PUSHED:// push
			eventTitle = event.getData().getRef()
					.substring(event.getData().getRef().lastIndexOf("/") + 1);
			title = "推送到了项目 " + pAuthor_And_pName + " 的 " + eventTitle + " 分支";
			break;
		case Event.EVENT_TYPE_COMMENTED:// 评论
			if (event.getEvents().getIssue() != null) {
				eventTitle = "Issues";
			} else if (event.getEvents().getPull_request() != null) {
				eventTitle = "PullRequest";
			}
			eventTitle = eventTitle + getEventsTitle(event);
			title = "评论了项目 " + pAuthor_And_pName + " 的 " + eventTitle;
			break;
		case Event.EVENT_TYPE_MERGED:// 合并
			eventTitle = event.getTarget_type() + getEventsTitle(event);
			title = "接受了项目 " + pAuthor_And_pName + " 的 " + eventTitle;
			break;
		case Event.EVENT_TYPE_JOINED:// # User joined project
			title = "加入了项目 " + pAuthor_And_pName;
			break;
		case Event.EVENT_TYPE_LEFT:// # User left project
			title = "离开了项目 " + pAuthor_And_pName;
			break;
		case Event.EVENT_TYPE_FORKED:// fork了项目
			title = "Fork了项目 " + pAuthor_And_pName;
			break;
		default:
			title = "更新了动态：";
			break;
		}
		title = author_name + " " + title;
		SpannableString sps = new SpannableString(title);

		// 设置用户名字体大小、加粗、高亮
		sps.setSpan(new AbsoluteSizeSpan(14, true), 0, author_name.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sps.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				author_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sps.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 0,
				author_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// 设置项目名字体大小和高亮
		int start = title.indexOf(pAuthor_And_pName);
		int end = start + pAuthor_And_pName.length();
		sps.setSpan(new AbsoluteSizeSpan(14, true), start, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sps.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")),
				start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// 设置动态的title字体大小和高亮
		if (!StringUtils.isEmpty(eventTitle) && eventTitle != null) {
			start = title.indexOf(eventTitle);
			end = start + eventTitle.length();
			if (start > 0 && end > 0 && start < end) {
				sps.setSpan(new AbsoluteSizeSpan(14, true), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				sps.setSpan(
						new ForegroundColorSpan(Color.parseColor("#0e5986")),
						start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return sps;
	}

	private static String getEventsTitle(Event event) {
		String title = "";
		if (event.getEvents().getIssue() != null) {
			title = " #" + event.getEvents().getIssue().getIid();
		}

		if (event.getEvents().getPull_request() != null) {
			title = " #" + event.getEvents().getPull_request().getIid();
		}
		return title;
	}

	/**
	 * 加载显示用户头像
	 * 
	 * @param imgFace
	 * @param faceURL
	 */
	public static void showUserFace(final ImageView imgFace,
			final String faceURL) {
		showLoadImage(imgFace, faceURL,
				imgFace.getContext().getString(R.string.msg_load_userface_fail));
	}

	/**
	 * 加载显示图片
	 * 
	 * @param imgFace
	 * @param faceURL
	 * @param errMsg
	 */
	public static void showLoadImage(final ImageView imgView,
			final String imgURL, final String errMsg) {
		// 读取本地图片
		if (StringUtils.isEmpty(imgURL) || imgURL.endsWith("portrait.gif")) {
			Bitmap bmp = BitmapFactory.decodeResource(imgView.getResources(),
					R.drawable.mini_avatar);
			imgView.setImageBitmap(bmp);
			return;
		}

		// 是否有缓存图片
		final String filename = FileUtils.getFileName(imgURL);
		// Environment.getExternalStorageDirectory();返回/sdcard
		String filepath = imgView.getContext().getFilesDir() + File.separator
				+ filename;
		File file = new File(filepath);
		if (file.exists()) {
			Bitmap bmp = ImageUtils.getBitmap(imgView.getContext(), filename);
			imgView.setImageBitmap(bmp);
			return;
		}

		// 从网络获取&写入图片缓存
		String _errMsg = imgView.getContext().getString(
				R.string.msg_load_image_fail);
		if (!StringUtils.isEmpty(errMsg))
			_errMsg = errMsg;
		final String ErrMsg = _errMsg;
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1 && msg.obj != null) {
					imgView.setImageBitmap((Bitmap) msg.obj);
					try {
						// 写图片缓存
						ImageUtils.saveImage(imgView.getContext(), filename,
								(Bitmap) msg.obj);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					ToastMessage(imgView.getContext(), ErrMsg);
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Bitmap bmp = ApiClient.getNetBitmap(imgURL);
					msg.what = 1;
					msg.obj = bmp;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 清除app缓存
	 * 
	 * @param activity
	 */
	public static void clearAppCache(Activity activity) {
		final AppContext ac = (AppContext) activity.getApplication();
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					ToastMessage(ac, "缓存清除成功");
				} else {
					ToastMessage(ac, "缓存清除失败");
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					ac.clearAppCache();
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 显示登录的界面
	 * 
	 * @param context
	 */
	public static void showLoginActivity(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示项目的详情
	 * 
	 * @param context
	 * @param project
	 * @param projectId
	 */
	public static void showProjectDetail(Context context, Project project,
			String projectId) {
		Intent intent = new Intent(context, ProjectActivity.class);
		Bundle bundle = new Bundle();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		bundle.putSerializable(PROJECT, project);
		bundle.putString(PROJECTID, projectId);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 显示commit详情
	 * 
	 * @param context
	 * @param project
	 * @param commit
	 */
	public static void showCommitDetail(Context context, Project project,
			Commit commit) {
		Intent intent = new Intent(context, CommitDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, project);
		bundle.putSerializable(Contanst.COMMIT, commit);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 显示commit的Diff详情
	 * 
	 * @param context
	 * @param project
	 * @param commit
	 * @param commitDiff
	 */
	public static void showCommitDiffFileDetail(Context context,
			Project project, Commit commit, CommitDiff commitDiff) {
		Intent intent = new Intent(context, CommitFileDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, project);
		bundle.putSerializable(Contanst.COMMIT, commit);
		bundle.putSerializable(Contanst.COMMITDIFF, commitDiff);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 显示issue的详情
	 * 
	 * @param context
	 * @param project
	 * @param issue
	 */
	public static void showIssueDetail(Context context, Project project,
			Issue issue, String projectId, String issueId) {
		Intent intent = new Intent(context, IssueDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, project);
		bundle.putSerializable(Contanst.ISSUE, issue);
		bundle.putString(Contanst.PROJECTID, projectId);
		bundle.putString(Contanst.ISSUEID, issueId);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 显示issue的编辑或者新增issue的界面
	 * 
	 * @param context
	 * @param project
	 * @param issue
	 */
	public static void showIssueEditOrCreate(Context context, Project project,
			Issue issue) {
		Intent intent = new Intent(context, IssueEditActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, project);
		bundle.putSerializable(Contanst.ISSUE, issue);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 显示用户信息详情
	 * 
	 * @param context
	 */
	public static void showMySelfInfoDetail(Context context) {
		Intent intent = new Intent(context, MySelfInfoActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示搜索界面
	 * 
	 * @param context
	 */
	public static void showSearch(Context context) {
		Intent intent = new Intent(context, SearchActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 显示用户详情界面
	 * 
	 * @param context
	 * @param user
	 */
	public static void showUserInfoDetail(Context context, User user,
			String user_id) {
		Intent intent = new Intent(context, UserInfoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.USER, user);
		bundle.putString(Contanst.USERID, user_id);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 点击动态显示动态详情
	 * 
	 * @param context
	 * @param event
	 */
	public static void showEventDetail(Context context, Event event) {
		if (event.getEvents().getIssue() != null) {
			showIssueDetail(context, null, null, event.getProject().getId(),
					event.getEvents().getIssue().getId());
		} else {
			showProjectDetail(context, null, event.getProject().getId());
		}
	}

	// 查看代码文件详情
	public static void showCodeFileDetail(Context context, String path,
			String fileName, String ref, Project project) {
		Intent intent = new Intent(context, CodeFileDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, project);
		bundle.putString("fileName", fileName);
		bundle.putString("path",
				path == null || StringUtils.isEmpty(path) ? fileName : path
						+ "/" + fileName);
		bundle.putString("ref", ref);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 显示项目的readme详情
	 * 
	 * @param context
	 * @param project
	 */
	public static void showProjectReadMeActivity(Context context,
			Project project) {
		Intent intent = new Intent(context, ProjectReadMeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, project);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 显示项目的一些列表信息列表
	 * 
	 * @param context
	 * @param project
	 */
	public static void showProjectListActivity(Context context,
			Project project, int type) {
		Intent intent = new Intent(context, ProjectSomeInfoListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, project);
		bundle.putInt("project_list_type", type);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 显示项目的代码列表
	 * 
	 * @param context
	 * @param project
	 */
	public static void showProjectCodeActivity(Context context, Project project) {
		Intent intent = new Intent(context, ProjectCodeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.PROJECT, project);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 进入主界面
	 * 
	 * @param context
	 */
	public static void goMainActivity(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示图片查看界面
	 * 
	 * @param context
	 * @param img_url
	 */
	public static void showImageZoomActivity(Context context, String img_url) {
		Intent intent = new Intent(context, ImageZoomActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(Contanst.IMG_URL, img_url);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			ToastMessage(context, "无法浏览此网页", 500);
		}
	}

	// 发送通知广播
	public static void sendBroadCast(Context context, int count) {
		if (!((AppContext) context.getApplicationContext()).isLogin()
				|| count == 0)
			return;
		Intent intent = new Intent("net.oschina.gitapp.action.APPWIDGET_UPDATE");
		intent.putExtra("count", count);
		context.sendBroadcast(intent);
	}

	/**
	 * 显示通知详情页面
	 * 
	 * @param context
	 */
	public static void showNotificationDetail(Context context) {
		Intent intent = new Intent(context, NotificationActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示分享操作
	 * 
	 * @param context
	 * @param shareContent
	 * @param shareImage
	 */
	public static void showShareOption(Activity context, String title,
			String url, String shareContent, Bitmap shareImage) {
//		UMImage mUMImgBitmap = new UMImage(context, shareImage);
//		// 首先在您的Activity中添加如下成员变量
//		final UMSocialService mController = UMServiceFactory
//				.getUMSocialService("com.umeng.share");
//
//		// appID是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
//		String appID = "wx850b854f6aad6764";
//		// 添加微信平台
//		UMWXHandler wxHandler = new UMWXHandler(context, appID);
//		wxHandler.addToSocialSDK();
//		// 设置分享到微信的内容
//		WeiXinShareContent weixinContent = new WeiXinShareContent(mUMImgBitmap);
//		weixinContent.setShareContent(shareContent);
//		weixinContent.setTitle(title);
//		weixinContent.setTargetUrl(url);
//		weixinContent.setShareImage(mUMImgBitmap);
//		wxHandler.mShareMedia = weixinContent;
//		mController.setShareMedia(weixinContent);
//
//		// 支持微信朋友圈
//		UMWXHandler wxCircleHandler = new UMWXHandler(context, appID);
//		// 设置朋友圈分享的内容
//		CircleShareContent circleMedia = new CircleShareContent();
//		circleMedia.setShareContent(shareContent);
//		circleMedia.setShareImage(mUMImgBitmap);
//		circleMedia.setTargetUrl(url);
//		circleMedia.setTitle(title);
//		wxCircleHandler.mShareMedia = circleMedia;
//		mController.setShareMedia(circleMedia);
//		wxCircleHandler.setToCircle(true);
//		wxCircleHandler.addToSocialSDK();
//
//		// 参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
//		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context, "1101982202",
//				"GJxJGse5cu9iH4NM");
//		QQShareContent qqShareContent = new QQShareContent();
//		qqShareContent.setTitle(title);
//		qqShareContent.setShareContent(shareContent);
//		qqShareContent.setShareImage(mUMImgBitmap);
//		qqShareContent.setTargetUrl(url);
//		qqSsoHandler.mShareMedia = mUMImgBitmap;
//		mController.setShareMedia(qqShareContent);
//		qqSsoHandler.addToSocialSDK();
//
//		SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
//		SinaShareContent sinaShareContent = new SinaShareContent();
//		String form = "  分享自GitOSC移动客户端，好项目尽在https://git.oschina.net";
//		sinaShareContent.setShareContent(shareContent + " " + url + form);
//		sinaShareContent.setTargetUrl(url);
//		sinaShareContent.setShareImage(mUMImgBitmap);
//		sinaShareContent.setTitle(title);
//		mController.setShareMedia(sinaShareContent);
//		mController.getConfig().setSsoHandler(sinaSsoHandler);
//		// 移除人人分享操作
//		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
//				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.TENCENT);
//
//		mController.openShare(context, false);
	}

	/**
	 * 获得屏幕的截图
	 * 
	 * @param activity
	 * 
	 * @return
	 */
	public static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		//图片允许最大空间   单位：KB 
        double maxSize = 100.00; 
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）   
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        b1.compress(Bitmap.CompressFormat.JPEG, 70, baos); 
        byte[] b = baos.toByteArray(); 
        //将字节换成KB 
        double mid = b.length/1024; 
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩 
        if (mid > maxSize) { 
                //获取bitmap大小 是允许最大大小的多少倍 
                double i = mid / maxSize; 
                //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小） 
                b1 = ImageUtils.zoomBitmap(b1, b1.getWidth() / Math.sqrt(i), 
                		b1.getHeight() / Math.sqrt(i));
        } 
		
		return b1;
	}
}
