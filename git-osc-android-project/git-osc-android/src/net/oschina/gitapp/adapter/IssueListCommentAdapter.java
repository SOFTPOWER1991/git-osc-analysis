package net.oschina.gitapp.adapter;

import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.GitNote;
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
 * issue的评论列表适配器
 * @created 2014-06-16
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class IssueListCommentAdapter extends MyBaseAdapter<GitNote> {
	
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public ImageView face;//用户头像
		public TextView name;
		public TextView body;
		public TextView date;
	}
	
	public IssueListCommentAdapter(Context context, List<GitNote> data, int resource) {
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
			listItemView.face = (ImageView) convertView.findViewById(R.id.issue_comment_listitem_userface);
			listItemView.name = (TextView) convertView.findViewById(R.id.issue_comment_listitem_username);
			listItemView.body = (TextView) convertView.findViewById(R.id.issue_comment_listitem_body);
			listItemView.date = (TextView) convertView.findViewById(R.id.issue_comment_listitem_data);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		final GitNote note = listData.get(position);
		
		// 1.加载头像
		String portraitURL = note.getAuthor().getNew_portrait();
		if (portraitURL.endsWith("portrait.gif")) {
			listItemView.face.setImageResource(R.drawable.mini_avatar);
		} else {
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}
		listItemView.face.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				User user = note.getAuthor();
				if (user == null) {
					return;
				}
				UIHelper.showUserInfoDetail(context, user, null);
			}
		});
		
		// 2.显示相关信息
		listItemView.name.setText(note.getAuthor().getName());
		listItemView.body.setText(HtmlRegexpUtils.filterHtml(note.getBody()));
		listItemView.date.setText(StringUtils.friendly_time(note.getCreated_at()));
			
		return convertView;
	}
}
