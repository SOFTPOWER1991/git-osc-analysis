# Git-OSC源码学习：语言页面

该页面学到的东西：

> 1. TipInfoLayout.java 一个自定义的提示控件，会根据不同的加载状态进行信息提示。这个可以抽取出来使用。
> 2. EnhanceListView.java 一个自定义的增强型Listview，可以进行下来刷新、上拉加载更多
> 3. 通用Adapter的使用
> 4. ButterKnife的使用
> 5. JsonUtils进行Json解析
> 6. ActionBar中的导航列表模式的使用（这个可以改进的，使用Toolbar）

下面针对上面6个知识点，进行逐一学习：

### TipInfoLayout.java 

> 这个控件可以根据不同的加载状态，进行相应信息的提示。
> 1. 刚进入页面时，显示Progressbar 提示正在加载，加载完成后隐藏；
> 2. 加载数据为空，提示暂无数据；
> 3. 加载数据失败，提示加载失败；
> 4. 网络不好时，提示网络断开；
> 同时，可以给该控件添加相应监听事件，在不同状态完成相应任务。

该控件源码位置：[TipInfoLayout.java](http://git.oschina.net/oschina/git-osc-android/blob/master/gitoscandroid/src/main/java/net/oschina/gitapp/widget/TipInfoLayout.java/?dir=0&filepath=gitoscandroid/src/main/java/net/oschina/gitapp/widget/TipInfoLayout.java&oid=3929b0f121599a2438f81f7ebcb19bcf6389d597&sha=b7588d3d31b8a705582c660a69fb71363f788d9b)：

在布局文件中的引入方式，只在代码文件中配置即可：

```xml
<net.oschina.gitapp.widget.TipInfoLayout
        android:id="@+id/tip_info"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

在代码中的使用

```java

//1. 通过 ButterKinfe注解引入（ButterKinfe不同版本绑定控件方法不一样）：
@InjectView(R.id.tip_info)
    TipInfoLayout tipInfo;
    
//2. 设置相应监听事件，可选，在tipInfo提示相应信息后，点击该控件刷新界面：
tipInfo.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLanguageAdapter.getCount() == 0) {
                    loadLanguagesList();
                } else {
                    loadProjects(mLanguageId, 1);
                }
            }
        });

//3. 加载数据前，设置tipInfo为正在加载中：
tipInfo.setLoading();

//4. 数据成功加载后，隐藏tipInfo：
tipInfo.setHiden();

//5. 加载数据失败后，提示加载数据出错：
tipInfo.setLoadError();

//6. 加载数据为空时，提示加载数据为空：
tipInfo.setEmptyData("加载语言列表失败");
```

**自定义字体的使用**

在该文件中有如下代码：

```java
TypefaceUtils.setFontAwsome(this.mTvTipState);
```
在网上搜了下，大致意思是说：使用这个可以在Android平台上使用自定义字体更方便。具体的也不深究了。


###EnhanceListView.java

> 一个自定义的ListView，可以完成上拉加载更多、下拉刷新。
> 有分页加载功能：通过mPageNum 、mPageSize完成

经常用的就不多啰嗦了。

代码位置：

[]()

###通用Adapter的使用

在上一篇文章中已经学习了。不再啰嗦。

###ButterKnife的使用

在学习开源中国源码时翻译过ButterKnife的文档，在此不再赘述。

###JsonUtils进行Json解析

> 一个Json解析工具，借助commons-io 和jackson-mapper-asl 两个jar包来完成json解析

```
package net.oschina.gitapp.util;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;


/***
 * json工具解析类
 */
public class JsonUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper().configure(
            DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static <T> T toBean(Class<T> type, InputStream is) {
        T obj = null;
        try {
            obj = parse(is, type, null);
        } catch (IOException e) {
        }
        return obj;
    }

    /**
     * 对获取到的网络数据进行处理
     *
     * @param inputStream
     * @param type
     * @param instance
     * @return
     * @throws java.io.IOException
     */
    private static <T> T parse(InputStream inputStream, Class<T> type, T instance) throws
            IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(inputStream);
            String data = IOUtils.toString(reader);

            L.et("data" , data);

            if (type != null) {
                return MAPPER.readValue(data, type);
            } else if (instance != null) {
                return MAPPER.readerForUpdating(instance).readValue(data);
            } else {
                return null;
            }
        } catch (SSLHandshakeException e) {
            throw new SSLHandshakeException("You can disable certificate checking by setting " +
                    "ignoreCertificateErrors on GitlabHTTPRequestor");
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    public static <T> T toBean(Class<T> type, byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return toBean(type, new ByteArrayInputStream(bytes));
    }
	
    public static <T> List<T> getList(Class<T[]> type, byte[] bytes) {
        if (bytes == null) return null;
        List<T> results = new ArrayList<T>();
        try {
            T[] _next = toBean(type, bytes);
            if (_next != null)
                Collections.addAll(results, _next);
        } catch (Exception e) {
            return null;
        }
        return results;
    }
}

```

在项目中的使用：

```
List<Language> languageList = JsonUtils.getList(Language[].class, t);
```
> 1.传进来的参数：Language[].class 和 byte[].这个list里放的type为Language.
> 2.在getList（）中走到了 T[] _next = toBean(type, bytes)；
> 3.toBean 最终走到了parse（xxx）在parse中完成了解析。
> 4.在parse中  String data = IOUtils.toString(reader);将byte[]转为了标准的json
> 5. 然后再通过刚开始设置的  ObjectMapper MAPPER = new ObjectMapper().configure(
            DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); 完成解析。
            
感觉还是挺麻烦的。感觉不会再爱了，这个json解析。


###ActionBar中的导航列表模式的使用

在BaseActivity中进行了一系列的ActionBar的设置：

```
 // 初始化ActionBar
    private void initActionBar() {
        mActionBar = getSupportActionBar();
        int flags = ActionBar.DISPLAY_HOME_AS_UP;
        int change = mActionBar.getDisplayOptions() ^ flags;
        // 设置返回的图标
        mActionBar.setDisplayOptions(change, flags);
        if (mTitle != null && !StringUtils.isEmpty(mTitle)) {
            mActionBar.setTitle(mTitle);
        }
        if (mSubTitle != null && !StringUtils.isEmpty(mSubTitle)) {
            mActionBar.setSubtitle(mSubTitle);
        }
    }

    public void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }

    public void setActionBarSubTitle(String subTitle) {
        mActionBar.setSubtitle(subTitle);
    }

```

在LanguageActivity中通过如下代码设置导航模式为List模式：

```
mActionBar.setDisplayShowTitleEnabled(false);
mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
```

同时LanguageActivity implements ActionBar.OnNavigationListener，override了如下方法：

```
@Override
    public boolean onNavigationItemSelected(int arg0, long arg1) {
        Language language = mLanguageAdapter.getItem(arg0);
        if (language != null) {
            mProjectAdapter.clear();
            mLanguageId = language.getId();
            L.et("mLanguageId" , mLanguageId);
            loadProjects(mLanguageId, 1);
            return true;
        }

        return false;
    }

```

当我们每更改一个选项时，就会根据当前language 调用loadProjects（xxxx）发起一个查询project的请求。

以上就是git-osc中关于Language部分的学习。








