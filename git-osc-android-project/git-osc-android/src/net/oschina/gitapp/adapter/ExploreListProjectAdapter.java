package net.oschina.gitapp.adapter;

import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.widget.CircleImageView;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 发现页面项目列表适配器
 * @created 2014-05-14 下午16:26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class ExploreListProjectAdapter extends MyBaseAdapter<Project> {
	
	private BitmapManager bmpManager;
	
	static class ListItemView {
		public CircleImageView face;//用户头像
		public TextView title;
		public TextView description;//项目描述
		public ImageView languageImage;
		public TextView language;//类型
		public TextView star;//加星数
		public TextView fork;//fork数
	}
	
	public ExploreListProjectAdapter(Context context, List<Project> data, int resource) {
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
			listItemView.face = (CircleImageView) convertView.findViewById(R.id.exploreproject_listitem_userface);
			listItemView.title = (TextView) convertView.findViewById(R.id.exploreproject_listitem_title);
			listItemView.description = (TextView) convertView.findViewById(R.id.exploreproject_listitem_description);
			listItemView.star = (TextView) convertView.findViewById(R.id.exploreproject_listitem_star);
			listItemView.fork = (TextView) convertView.findViewById(R.id.exploreproject_listitem_fork);
			listItemView.languageImage = (ImageView) convertView.findViewById(R.id.exploreproject_listitem_language_image);
			listItemView.language = (TextView) convertView.findViewById(R.id.exploreproject_listitem_language);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
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
		
		// 2.显示相关信息
		listItemView.title.setText(project.getOwner().getName() + " / " + project.getName());
		
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
		String language = project.getLanguage() != null ? project.getLanguage() : "";
		if (project.getLanguage() != null) {
			listItemView.language.setText(language);
		} else {
			listItemView.language.setVisibility(View.GONE);
			listItemView.languageImage.setVisibility(View.GONE);
		}
		
		return convertView;
	}
}
