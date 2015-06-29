package net.oschina.gitapp.adapter;

import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.HtmlRegexpUtils;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 项目Issues列表适配器
 * @created 2014-05-28 上午11：19
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class ProjectIssuesListAdapter extends MyBaseAdapter<Issue> {
	
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public ImageView face;//用户头像
		public TextView title;
		public TextView description;
		public TextView username;
		public TextView date;
		public TextView comment_count;// 评论数量
	}
	
	public ProjectIssuesListAdapter(Context context, List<Issue> data, int resource) {
		super(context, data, resource);
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.widget_dface_loading));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ListItemView  listItemView = null;
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			
			//获取控件对象
			listItemView.face = (ImageView) convertView.findViewById(R.id.projectissues_listitem_userface);
			listItemView.title = (TextView) convertView.findViewById(R.id.projectissues_listitem_title);
			listItemView.description = (TextView) convertView.findViewById(R.id.projectissues_listitem_description);
			listItemView.username = (TextView) convertView.findViewById(R.id.projectissues_listitem_author);
			listItemView.date = (TextView) convertView.findViewById(R.id.projectissues_listitem_date);
			listItemView.comment_count = (TextView) convertView.findViewById(R.id.projectissues_listitem_count);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		final Issue issue = listData.get(position);
		
		// 1.加载头像
		String portraitURL = issue.getAuthor() == null ? "" : issue.getAuthor().getNew_portrait();
		if (portraitURL.endsWith("portrait.gif") || StringUtils.isEmpty(portraitURL)) {
			listItemView.face.setImageResource(R.drawable.mini_avatar);
		} else {
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}
		listItemView.face.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				User user = issue.getAuthor();
				if (user == null) {
					return;
				}
				UIHelper.showUserInfoDetail(context, user, null);
			}
		});
		
		// 2.显示相关信息
		listItemView.title.setText(issue.getTitle());
		if (StringUtils.isEmpty(issue.getDescription())) {
			listItemView.description.setText("暂无描述");
		} else {
			listItemView.description.setText(HtmlRegexpUtils.filterHtml(issue.getDescription()));
		}
		
		listItemView.username.setText(issue.getAuthor() == null ? "" : issue.getAuthor().getName());
		listItemView.date.setText(StringUtils.friendly_time(issue.getCreatedAt()));
		
		return convertView;
	}
}
