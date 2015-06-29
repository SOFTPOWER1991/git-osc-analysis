package net.oschina.gitapp.widget;

import java.util.List;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.MoreMenuItem;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 弹出菜单 说明：所有的的DropDownMenu的根的ID都需要时set_up(用于设置在menu外点击关闭)
 * 
 * @created 2014-08-01
 * @author 火蚁(http://my.oschina.net/LittleDY)
 * 
 */
public class DropDownMenu extends PopupWindow {
	
	private ViewGroup menuView;
	
	private OnClickListener itemClickListener;

	private LayoutInflater inflater;

	public DropDownMenu(final Activity context,
			OnClickListener itemClickListener) {
		super(context);
		this.itemClickListener = itemClickListener;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menuView = (ViewGroup) inflater.inflate(R.layout.more_menu_container, null);
		this.setContentView(menuView);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0000000000);
		this.getContentView().setPadding(0, 0, 25, 0);
		this.setBackgroundDrawable(dw);
		menuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// 所有的的DropDownMenu的根的ID都需要时set_up
				int height = menuView.findViewById(R.id.set_pop).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}
	
	public ViewGroup getViewGroup() {
		return this.menuView;
	}

	// 添加菜单项
	private void addItem(MoreMenuItem moreMenuItem, boolean isAddLine) {
		View item = inflater.inflate(R.layout.more_menu_item, null);
		item.setId(moreMenuItem.getViewId());
		((ImageView) item.findViewById(R.id.more_menu_item_img))
				.setBackgroundResource(moreMenuItem.getImgId());
		((TextView) item.findViewById(R.id.more_menu_item_text))
				.setText(moreMenuItem.getText());
		item.setOnClickListener(itemClickListener);
		menuView.addView(item);
		if (isAddLine) {
			View line = inflater
					.inflate(R.layout.horizontal_divider_view, null);
			menuView.addView(line);
		}
	}

	public void addItems(List<MoreMenuItem> mMoreItems) {
		for (int i = 0; i < mMoreItems.size(); i++) {
			if (this != null) {
				if (i != mMoreItems.size() - 1) {
					this.addItem(mMoreItems.get(i), true);
				} else {
					this.addItem(mMoreItems.get(i), false);
				}
			}
		}
	}
}
