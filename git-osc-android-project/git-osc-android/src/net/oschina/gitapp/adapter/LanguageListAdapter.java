package net.oschina.gitapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Language;

public class LanguageListAdapter extends MyBaseAdapter<Language> {
	
	static class ViewHolder {
		public TextView text;
	} 
	
	public LanguageListAdapter(Context context, List<Language> data,
			int resource) {
		super(context, data, resource);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			viewHolder = new ViewHolder();
			
			viewHolder.text = (TextView) convertView.findViewById(R.id.language_name);
			
			//设置控件集到convertView
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		Language language = getItem(position);
		viewHolder.text.setText(language.getName());
		
		return convertView;
	}
}
