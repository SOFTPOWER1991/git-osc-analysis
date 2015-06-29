package net.oschina.gitapp.adapter;

import java.util.List;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.StringUtils;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 项目Commit列表适配器
 * @created 2014-05-26 下午14:43
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class ProjectCommitListAdapter extends MyBaseAdapter<Commit> {
	
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public ImageView face;//用户头像
		public TextView username;
		public TextView content;
		public TextView date;
	}
	
	public ProjectCommitListAdapter(Context context, List<Commit> data, int resource) {
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
			listItemView.face = (ImageView) convertView.findViewById(R.id.projectcommit_listitem_userface);
			listItemView.username = (TextView) convertView.findViewById(R.id.projectcommit_listitem_username);
			listItemView.content = (TextView) convertView.findViewById(R.id.projectcommit_listitem_content);
			listItemView.date = (TextView) convertView.findViewById(R.id.projectcommit_listitem_date);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		Commit commit = listData.get(position);
		
		// 1.加载头像
		String portraitURL = commit.getAuthor() == null ? "" : commit.getAuthor().getNew_portrait();
		if (portraitURL.endsWith("portrait.gif") || StringUtils.isEmpty(portraitURL)) {
			listItemView.face.setImageResource(R.drawable.mini_avatar);
		} else {
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}
		
		// 2.显示相关信息
		listItemView.username.setText(commit.getAuthor() == null ? commit.getAuthor_name() : commit.getAuthor().getName());
		listItemView.content.setText(commit.getTitle());
		listItemView.date.setText(StringUtils.friendly_time(commit.getCreatedAt()));
		
		return convertView;
	}
}
