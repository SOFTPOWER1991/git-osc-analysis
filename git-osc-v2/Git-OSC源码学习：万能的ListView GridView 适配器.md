# Git-OSC源码学习：万能的ListView GridView 适配器

很久之前读过一篇博文[Android 快速开发系列 打造万能的ListView GridView 适配器](http://blog.csdn.net/lmj623565791/article/details/38902805) 那个时候读完便在项目中运用了，感觉非常爽。

当我再一次读Git-osc源码时，发现项目中也这样做了，因此我想尝试着用自己的语言，将这种写法描述一下。

本章中将从以下几方面描述这种万能适配器的抽取：

> 1. 之前的适配器写法；
> 2. 通用ViewHolder;
> 3. 通用Adapter；
> 4. git-osc中的运用


*****

####1.之前的适配器写法

```
 @Override  
    public View getView(int position, View convertView, ViewGroup parent)  
    {  
        ViewHolder viewHolder = null;  
        if (convertView == null)  
        {  
            convertView = mInflater.inflate(R.layout.item_single_str, parent,  
                    false);  
            viewHolder = new ViewHolder();  
            viewHolder.mTextView = (TextView) convertView  
                    .findViewById(R.id.id_tv_title);  
            convertView.setTag(viewHolder);  
        } else  
        {  
            viewHolder = (ViewHolder) convertView.getTag();  
        }  
        viewHolder.mTextView.setText(mDatas.get(position));  
        return convertView;  
    }  
  
    private final class ViewHolder  
    {  
        TextView mTextView;  
    }  
```

上面这段Adapter的代码，已经是模板代码了。

可是，如果项目中有很多个Adapter的话，这样的模板代码就会一遍遍的出现；那么该怎么优化呢？请看下文分解；

###2.通用ViewHolder

上回说到，我们要优化Adapter中一遍遍出现模板代码，那么具体该怎么做呢？

首先我们得明白ViewHolder的作用？
> 通过convertView.setTag将viewholder与convertView进行绑定，然后在复用convertView的时候，通过convertView.getTag()直接从与之对应的ViewHolder中拿到convertView布局中的控件，省去了findViewById()带来的开销。
> 每个convertView会绑定一个ViewHolder对象，这个viewHolder主要用来为convertView存储布局中的控件。因此，我们只要搞一个通用的ViewHolder，然后对于任意的convertView，提供一个对象让其setTag即可。


下面结合git-osc中的原来进行讲解：

```
package net.oschina.gitapp.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kymjs.core.bitmap.client.BitmapCore;

import net.oschina.gitapp.R;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.util.TypefaceUtils;


/**
 * 通用性极高的ViewHolder
 * Created by 火蚁 on 15/4/8.
 */
public class ViewHolder {
    // 用于存储listView item的容器，SparseArray 相对于Map效率更高的容器
    private SparseArray<View> mViews;

    // item根view
    private View mConvertView;

    protected Context mContext;

    private int position;

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mViews = new SparseArray<>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);
        this.mContext = context;
        this.position = position;
    }

    /**
     * 获取一个viewHolder
     *
     * @param context     context
     * @param convertView view
     * @param parent      parent view
     * @param layoutId    布局资源id
     * @param position    索引
     * @return
     */
    public static ViewHolder getViewHolder(Context context, View convertView, ViewGroup parent,
                                           int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }

        return (ViewHolder) convertView.getTag();
    }

    public int getPosition() {
        return this.position;
    }

    // 通过一个viewId来获取一个view
    public <T extends View> T getView(int viewId) {

        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    // 返回viewHolder的容器类
    public View getConvertView() {
        return this.mConvertView;
    }

    // 给TextView设置文字
    public void setText(int viewId, String text) {
        if (StringUtils.isEmpty(text)) return;
        TextView tv = getView(viewId);
        tv.setText(text);
    }

    // 给TextView设置文字
    public void setText(int viewId, SpannableString text) {
        if (text == null) return;
        TextView tv = getView(viewId);
        tv.setText(text);
    }

    // 给TextView设置文字
    public void setText(int viewId, int textRes) {
        TextView tv = getView(viewId);
        tv.setText(textRes);
    }

    public void setText(int viewId, String text, int emptyRes) {
        TextView tv = getView(viewId);
        if (StringUtils.isEmpty(text)) {
            tv.setText(emptyRes);
        } else {
            tv.setText(text);
        }
    }

    public void setText(int viewId, String text, String emptyText) {
        TextView tv = getView(viewId);
        if (StringUtils.isEmpty(text)) {
            tv.setText(emptyText);
        } else {
            tv.setText(text);
        }
    }

    /**
     * @param viewId      id
     * @param text        内容
     * @param semanticRes 资源
     */
    public void setTextWithSemantic(int viewId, String text, int semanticRes) {
        TextView tv = getView(viewId);
        TypefaceUtils.setSemantic(tv, text, semanticRes);
    }

    public void setTextWithOcticon(int viewId, String text, int iconRes) {
        TextView tv = getView(viewId);
        TypefaceUtils.setOcticons(tv, text, iconRes);
    }

    // 给ImageView设置图片资源
    public void setImageResource(int viewId, int resId) {
        ImageView iv = getView(viewId);
        iv.setImageResource(resId);
    }

    public void setImageForUrl(int viewId, String imgUrl) {

        ImageView iv = getView(viewId);
        if (iv.getTag() == null || !iv.getTag().equals(imgUrl)) {
            iv.setImageResource(R.drawable.mini_avatar);
        }
        //Git-osc中图片的加载使用的是bitmapcore这个框架
        new BitmapCore.Builder().url(imgUrl).view(iv).doTask();
        iv.setTag(imgUrl);
    }
}

```

至此一个通用的ViewHolder设置完毕；

###3.通用Adapter

Adapter一般需要保持一个List对象，存储一个Bean的集合，不同的ListView，Bean肯定是不同的，因此就决定了通用的Adapter肯定需要支持泛型，内部维持一个List<T>，就解决我们的问题了

git-osc中源码如下：

```
package net.oschina.gitapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的ViewHolder
 * <p/>
 * Created by 火蚁 on 15/4/8.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    //维护一个泛型list，用来满足各种不同的bean
    private List<T> mDatas;
    //各种Adapter的布局文件
    private int mLayoutId;

    public CommonAdapter(Context context, int layoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mDatas = new ArrayList<T>();
        this.mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

		
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh = ViewHolder.getViewHolder(this.mContext, convertView, parent, this.mLayoutId, position);
        //对外公布了一个convert方法，并且还把viewHolder和本Item对于的Bean对象给传出去
        convert(vh, getItem(position));
        return vh.getConvertView();
    }

    public List<T> getDatas() {
        return this.mDatas;
    }

    // 获取ViewHodler
    public ViewHolder getViewHodler(int position, View convertView, ViewGroup parent) {

        return ViewHolder.getViewHolder(this.mContext, convertView, parent, this.mLayoutId, 
                position);
    }

    // 提供给外部填充实际的显示数据，以及可以一些其他的操作，如：隐藏＝＝
    public abstract void convert(ViewHolder vh, T item);

    public void addItem(T item) {
        checkListNull();
        mDatas.add(item);
        notifyDataSetChanged();
    }

    public void addItem(int location, T item) {
        checkListNull();
        mDatas.add(location, item);
        notifyDataSetChanged();
    }

    public void addItem(List<T> items) {
        checkListNull();
        mDatas.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(int location) {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.remove(location);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void checkListNull() {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
    }
}

```

###4. git-osc中的运用

在issue的评论列表适配器中，这个万能的适配器是这样用的，详情看如下代码：

```
package net.oschina.gitapp.adapter;

import android.content.Context;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Comment;
import net.oschina.gitapp.common.HtmlRegexpUtils;

/**
 * issue的评论列表适配器
 * @created 2014-06-16
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新者：
 */
public class CommitCommentdapter extends CommonAdapter<Comment> {
	
	public CommitCommentdapter(Context context, int resource) {
		super(context, resource);
	}

    @Override
    public void convert(ViewHolder vh, Comment comment) {
        // 1.加载头像
        String portraitURL = comment.getAuthor().getNew_portrait();
        if (portraitURL.endsWith("portrait.gif")) {
            vh.setImageResource(R.id.commit_comment_listitem_userface, R.drawable.mini_avatar);
        } else {
            vh.setImageForUrl(R.id.commit_comment_listitem_userface, portraitURL);
        }

        // 2.显示相关信息
        vh.setText(R.id.commit_comment_listitem_username, comment.getAuthor().getName());
        vh.setText(R.id.commit_comment_listitem_body, HtmlRegexpUtils.filterHtml(comment.getNote()));
        vh.setText(R.id.commit_comment_listitem_data, comment.getAuthor().getName());
    }
}

```

这样的Adapter代码清晰，用起来也爽。




