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




