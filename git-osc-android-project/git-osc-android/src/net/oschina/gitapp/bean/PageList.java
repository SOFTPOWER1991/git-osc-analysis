package net.oschina.gitapp.bean;

import java.util.List;

/**
 * 类名 PageList.java</br>
 * 创建日期 2014年4月23日</br>
 * @author LeonLee (http://my.oschina.net/lendylongli)</br>
 * Email lendylongli@gmail.com</br>
 * 更新时间 2014年4月23日 下午1:36:16</br>
 * 最后更新者 LeonLee</br>
 * 
 * 说明 类的描述
 */
public interface PageList<T> {

	public int getPageSize();
	public int getCount();
	public List<T> getList();
}
