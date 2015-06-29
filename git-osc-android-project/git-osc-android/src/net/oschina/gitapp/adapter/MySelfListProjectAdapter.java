package net.oschina.gitapp.adapter;

import java.util.Date;
import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 个人项目列表适配器
 * @created 2014-05-12
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class MySelfListProjectAdapter extends MyBaseAdapter<Project> {
	
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public int index;
		public ImageView face;
		public ImageView flag;// 项目标识
		public TextView project_name;
		public TextView description;//项目描述
		public TextView updateData;//日期
		public ImageView languageImage;
		public TextView language;//类型
		public TextView star;//加星数
		public TextView fork;//fork数
	}
	
	public MySelfListProjectAdapter(Context context, List<Project> data, int resource) {
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
			listItemView.face = (ImageView) convertView.findViewById(R.id.myself_project_listitem_userface);
			listItemView.project_name = (TextView) convertView.findViewById(R.id.myself_project_listitem_name);
			listItemView.flag = (ImageView) convertView.findViewById(R.id.myself_project_listitem_flag);
			listItemView.description = (TextView) convertView.findViewById(R.id.myself_project_listitem_description);
			listItemView.updateData = (TextView) convertView.findViewById(R.id.myself_project_listitem_date);
			listItemView.languageImage = (ImageView) convertView.findViewById(R.id.myself_project_listitem_language_image);
			listItemView.language = (TextView) convertView.findViewById(R.id.myself_project_listitem_language);
			listItemView.star = (TextView) convertView.findViewById(R.id.myself_project_listitem_star);
			listItemView.fork = (TextView) convertView.findViewById(R.id.myself_project_listitem_fork);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		initInfo(listItemView, position);
		return convertView;
	}
	
	private void initInfo(ListItemView listItemView, int position) {
		final Project project = listData.get(position);
		
		// 1.加载头像
		String portraitURL = project.getOwner().getNew_portrait();
		if (portraitURL.endsWith("portrait.gif")) {
			listItemView.face.setImageResource(R.drawable.mini_avatar);
		} else {
			bmpManager.loadBitmap(portraitURL, listItemView.face);
		}
		
		listItemView.face.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				User user = project.getOwner();
				if (user == null) {
					return;
				}
				UIHelper.showUserInfoDetail(context, user, null);
			}
		});
		
		listItemView.project_name.setText(project.getOwner().getName() + " / " + project.getName());
		
		Date last_push_at = project.getLast_push_at() != null ? project.getLast_push_at() : project.getCreatedAt();
		listItemView.updateData.setText("更新于: " + StringUtils.friendly_time(last_push_at));
		
		// 判断项目的类型，显示不同的图标（私有项目、公有项目、fork项目）
		if (project.getParent_id() != null) {
			listItemView.flag.setBackgroundResource(R.drawable.project_flag_fork);
		} else if (project.isPublic()) {
			listItemView.flag.setBackgroundResource(R.drawable.project_flag_public);
		} else {
			listItemView.flag.setBackgroundResource(R.drawable.project_flag_private);
		}
		
		// 判断是否有项目的介绍
		String descriptionStr = project.getDescription();
		if (!StringUtils.isEmpty(descriptionStr)) {
			listItemView.description.setText(descriptionStr);
		} else {
			listItemView.description.setText(R.string.msg_project_empty_description);
		}
		
		// 显示项目的star、fork、language信息
		listItemView.star.setText(project.getStars_count().toString());
		listItemView.fork.setText(project.getForks_count().toString());
		listItemView.language.setVisibility(View.GONE);
		listItemView.languageImage.setVisibility(View.GONE);
		String language = project.getLanguage() != null ? project.getLanguage() : "";
		if (project.getLanguage() != null) {
			listItemView.language.setText(language);
			listItemView.language.setVisibility(View.VISIBLE);
			listItemView.languageImage.setVisibility(View.VISIBLE);
		}
	}
}
