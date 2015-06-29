package net.oschina.gitapp.adapter;

import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Comment;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.HtmlRegexpUtils;
import net.oschina.gitapp.common.StringUtils;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
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
public class CommitListCommentdapter extends MyBaseAdapter<Comment> {
	
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public ImageView face;//用户头像
		public TextView name;
		public TextView body;
		public TextView date;
	}
	
	public CommitListCommentdapter(Context context, List<Comment> data, int resource) {
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
			listItemView.face = (ImageView) convertView.findViewById(R.id.commit_comment_listitem_userface);
			listItemView.name = (TextView) convertView.findViewById(R.id.commit_comment_listitem_username);
			listItemView.body = (TextView) convertView.findViewById(R.id.commit_comment_listitem_body);
			listItemView.date = (TextView) convertView.findViewById(R.id.commit_comment_listitem_data);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		Comment comment = listData.get(position);
		
		// 1.加载头像
		String portraitURL = comment.getAuthor().getNew_portrait();
		if (portraitURL.endsWith("portrait.gif")) {
			listItemView.face.setImageResource(R.drawable.mini_avatar);
		} else {
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}
		
		// 2.显示相关信息
		listItemView.name.setText(comment.getAuthor().getName());
		listItemView.body.setText(HtmlRegexpUtils.filterHtml(comment.getNote()));
		listItemView.date.setText(StringUtils.friendly_time(comment.getCreated_at()));
			
		return convertView;
	}
}
