package net.oschina.gitapp.adapter;

import java.util.List;

import org.codehaus.jackson.impl.Indenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

/**
 * 所有适配器基础类
 * @created 2014-05-12
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新时间：
 * 更新者：
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
	//标识LinkView上的链接，默认为false
	protected boolean isLinkViewClick = false;
	protected Context 					context;//运行上下文
	protected List<T> 					listData;//数据集合
	protected LayoutInflater 			listContainer;//视图容器
	protected int 						itemViewResource;//自定义项视图源id

	/**
	 * 实例化MyBaseAdapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public MyBaseAdapter(Context context, List<T> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listData = data;
	}
	public boolean isLinkViewClick() {
		return isLinkViewClick;
	}

	public void setLinkViewClick(boolean isLinkViewClick) {
		this.isLinkViewClick = isLinkViewClick;
	}
	
	@Override
	public int getCount() {
		return listData.size();
	}
	
	@Override
	public T getItem(int arg0) {
		return listData.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		return 0;
	}
}
