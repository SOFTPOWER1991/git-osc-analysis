package net.oschina.gitapp.adapter;

import java.util.List;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BitmapManager;
import net.oschina.gitapp.common.HtmlRegexpUtils;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 个人动态列表适配器
 * 
 * @created 2014-05-20 下午15：28
 * @author 火蚁（http://my.oschina.net/LittleDY）
 */
public class MySelfEventListAdapter extends MyBaseAdapter<Event> {

    // 图像管理线程类
    private final BitmapManager bmpManager;

    static class ListItemView {
        public ImageView face;// 用户头像
        public TextView user_name;
        public TextView content;// 更新内容
        public LinearLayout commitLists;// commits的列表
        public TextView date;// 更新时间
    }

    public MySelfEventListAdapter(Context context, List<Event> data,
            int resource) {
        super(context, data, resource);
        this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.widget_dface_loading));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView listItemView = null;
        Event event = listData.get(position);
        if (convertView == null) {
            // 获取list_item布局文件的视图
            convertView = listContainer.inflate(this.itemViewResource, null);

            listItemView = new ListItemView();

            // 获取控件对象
            listItemView.face = (ImageView) convertView
                    .findViewById(R.id.event_listitem_userface);
            listItemView.user_name = (TextView) convertView
                    .findViewById(R.id.event_listitem_username);
            listItemView.content = (TextView) convertView
                    .findViewById(R.id.event_listitem_content);
            listItemView.date = (TextView) convertView
                    .findViewById(R.id.event_listitem_date);
            listItemView.commitLists = (LinearLayout) convertView
                    .findViewById(R.id.event_listitem_commits_list);

            // 设置控件集到convertView
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        displayContent(listItemView, event);
        return convertView;
    }

    private void displayContent(ListItemView listItemView, final Event event) {

        // 1.加载头像
        String portraitURL = event.getAuthor().getNew_portrait();
        if (portraitURL.endsWith("portrait.gif")) {
            listItemView.face.setImageResource(R.drawable.mini_avatar);
        } else {
            bmpManager.loadBitmap(portraitURL, listItemView.face);
        }

        listItemView.face.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = event.getAuthor();
                if (user == null) {
                    return;
                }
                UIHelper.showUserInfoDetail(context, user, null);
            }
        });

        // 2.显示相关信息
        listItemView.user_name.setText(UIHelper.parseEventTitle(event
                .getAuthor().getName(), event.getProject().getOwner().getName()
                + " / " + event.getProject().getName(), event));

        // commits信息的显示
        listItemView.commitLists.setVisibility(View.GONE);
        listItemView.commitLists.removeAllViews();
        if (event.getData() != null) {
            List<Commit> commits = event.getData().getCommits();
            if (commits != null && commits.size() > 0) {
                showCommitInfo(listItemView.commitLists, commits);
                listItemView.commitLists.setVisibility(View.VISIBLE);
            }
        }

        listItemView.content.setVisibility(View.GONE);

        // 评论的内容
        if (event.getEvents().getNote() != null
                && event.getEvents().getNote().getNote() != null) {
            listItemView.content.setText(HtmlRegexpUtils.filterHtml(event
                    .getEvents().getNote().getNote()));
            listItemView.content.setVisibility(View.VISIBLE);
        } else

        // issue的title
        if (event.getEvents().getIssue() != null
                && event.getEvents().getNote() == null) {
            listItemView.content.setText(event.getEvents().getIssue()
                    .getTitle());
            listItemView.content.setVisibility(View.VISIBLE);
        }

        // pr的title
        if (event.getEvents().getPull_request() != null
                && event.getEvents().getNote() == null) {
            listItemView.content.setText(event.getEvents().getPull_request()
                    .getTitle());
            listItemView.content.setVisibility(View.VISIBLE);
        }

        listItemView.date.setText(StringUtils.friendly_time(event
                .getUpdated_at()));
    }

    private void showCommitInfo(LinearLayout layout, List<Commit> commits) {
        if (commits.size() >= 2) {
            addCommitItem(layout, commits.get(0));
            addCommitItem(layout, commits.get(1));
        } else {
            for (Commit commit : commits) {
                addCommitItem(layout, commit);
            }
        }
    }

    /**
     * 添加commit项
     * 
     * @param layout
     * @param commit
     */
    private void addCommitItem(LinearLayout layout, Commit commit) {
        View v = listContainer.inflate(R.layout.event_commits_listitem, null);
        ((TextView) v.findViewById(R.id.event_commits_listitem_commitid))
                .setText(commit.getId());
        ((TextView) v.findViewById(R.id.event_commits_listitem_username))
                .setText(commit.getAuthor().getName());
        ((TextView) v.findViewById(R.id.event_commits_listitem_message))
                .setText(commit.getMessage());
        layout.addView(v);
    }
}
