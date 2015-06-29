package net.oschina.gitapp.bean;

/**
 * more menu item的菜单实体类
 * @created 2014-08-01
 * @author 火蚁（htpp://my.oschina.net/LittleDY）
 *
 */
public class MoreMenuItem {
	
	private int viewId;// item view id
	
	private int imgId;
	
	private String text;
	
	public MoreMenuItem() {}
	
	public MoreMenuItem(int viewId, int imgId, String text) {
		this.viewId = viewId;
		this.imgId = imgId;
		this.text = text;
	}
	
	public int getViewId() {
		return viewId;
	}

	public void setViewId(int viewId) {
		this.viewId = viewId;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
