package net.oschina.gitapp.interfaces;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * list分类item接口
 * 
 * @author deyi
 * 
 */
public interface ListItems {
	
	public int getLayout();

	public boolean isClickable();

	public View getView(Context context, View convertView, LayoutInflater inflater);
	
}
