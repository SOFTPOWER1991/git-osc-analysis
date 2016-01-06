# git-osc自定义控件之：PagerSlidingTabStrip
在应用中可以发现各个栏目之间就是靠滑动选项卡来进行区分的，比如：`发现`中的`推荐项目`、`热门栏目`、`最近通知`。在使用的时候，我们可以左右滑动屏幕来进行切换，这种操作非常的优雅。经过学习源码，发现这是使用了自定义控件`PagerSlidingTabStrip`来完成的。因此决定学习一下这个自定义控件。

**开源项目地址：** [PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)

##先学习一下这个控件如何使用：

1. 声明`PagerSlidingTabStrip`用到的一些属性，在`res/values/attrs.xml`中声明：

	```
	<?xml version="1.0" encoding="utf-8"?>
	<resources>
    <declare-styleable name="PagerSlidingTabStrip">
        <attr name="slidingBlock" format="reference|color"/>
        <attr name="allowWidthFull" format="boolean"/>
        <attr name="disableViewPager" format="boolean"/>
        <attr name="pstsIndicatorColor" format="color" />
        <attr name="pstsUnderlineColor" format="color" />
        <attr name="pstsDividerColor" format="color" />
        <attr name="pstsIndicatorHeight" format="dimension" />
        <attr name="pstsUnderlineHeight" format="dimension" />
        <attr name="pstsDividerPadding" format="dimension" />
        <attr name="pstsTabPaddingLeftRight" format="dimension" />
        <attr name="pstsScrollOffset" format="dimension" />
        <attr name="pstsTabBackground" format="reference" />
        <attr name="pstsShouldExpand" format="boolean" />
        <attr name="pstsTextAllCaps" format="boolean" />
    </declare-styleable>
	</resources>
	```
	
2. 在布局文件中引入并配合`viewpager`一起使用：

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="fill_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" >
	
    <!-- 导航标题栏 -->
    <net.oschina.gitapp.widget.PagerSlidingTabStrip
        android:id="@+id/pager_tabstrip"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:allowWidthFull="true"
        app:slidingBlock="@drawable/image_sliding_block"
        android:background="@drawable/sliding_tab_strip_background" >
        
    </net.oschina.gitapp.widget.PagerSlidingTabStrip>

    <android.support.v4.view.ViewPager
        android:id="@+id/pager"
        android:layout_width="fill_parent"
        android:layout_height="0dp"
        android:layout_weight="1" >

    </android.support.v4.view.ViewPager>
    
</LinearLayout>
```
3.在代码中使用：将`PagerSlidingTabStrip`代码引入到工程中，然后在代码中使用。

## PagerSlidingTabStrip的源码分析：

`PagerSlidingTabStrip`源码的分析：参见 [codekk](http://www.codekk.com/open-source-project-analysis) 上的分析 [PagerSlidingTabStrip 源码解析](http://www.codekk.com/open-source-project-analysis/detail/Android/ayyb1988/PagerSlidingTabStrip%20%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90)


在程序源码中涉及到了`View`的绘制，使用到的方法有：

* onMeasure(...)
* onLayout(...)
* onDraw(...)

关于`View`的绘制，参见 [codekk](http://www.codekk.com/open-source-project-analysis) 上的分析 [公共技术点之 View 绘制流程](http://www.codekk.com/open-source-project-analysis/detail/Android/lightSky/%E5%85%AC%E5%85%B1%E6%8A%80%E6%9C%AF%E7%82%B9%E4%B9%8B%20View%20%E7%BB%98%E5%88%B6%E6%B5%81%E7%A8%8B)

##总结：

至此看到了一个复写了 onMeasure、onLayout、onDraw 的自定义控件。








