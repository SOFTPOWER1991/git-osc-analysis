#git-osc发现页面"推荐栏目"学习
涉及到的类有：
1. MainActivity.java ——> setExploreShow()
2. ExploreViewPagerFragment.java ——>onSetupTabAdapter(...)
3. ExploreListProjectFragment.java ——>...——>getList(...)
4. AppContext.java——>getExploreFeaturedProject(...)
5. ApiClient.java——>getgetExploreFeaturedProject(...)
6. URLs.java——>进行一系列的url拼装
7. HTTPRequestor.java——>...——>getList(...)

#具体代码流程分析

1. 程序入口`MainActivity`中`setExploreShow`,在该方法中将`ExploreViewPagerFragment`添加进来，在`ExploreViewPagerFragment`中承载了`发现页面`的三个栏目： `推荐项目`、`热门项目`、`最近更新`，因为这三个获取数据方式相似，展现数据方式也一样，因此在这里着重学习一种即可。

	```
	private void setExploreShow() {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.main_slidingmenu_frame,
				mMenu, DRAWER_MENU_TAG)
				.replace(R.id.main_content,
						ExploreViewPagerFragment.newInstance(),
						DRAWER_CONTENT_TAG).commit();

		mTitle = "发现";
		mActionBar.setTitle(mTitle);
		mCurrentContentTag = CONTENT_TAG_EXPLORE;
	}

	```
	
2. `ExploreViewPagerFragment`中的`onSetupTabAdapter`为页面添加了3个栏目，该类继承自`BaseViewPagerFragment`，并实现了`onSetupTabAdapter` 方法，在该方法中将三个选项卡添加进页面：

**这种方式，很优雅，根据不同的类型来进行添加**

```
@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.explore_title_array);
		
		//推荐项目
		Bundle featuredBundle = new Bundle();
		//静态导入
		featuredBundle.putByte(EXPLORE_TYPE, TYPE_FEATURED);
		adapter.addTab(title[0], "featured", ExploreListProjectFragment.class, featuredBundle);
		//热门项目
		Bundle popularBundle = new Bundle();
		popularBundle.putByte(EXPLORE_TYPE, TYPE_POPULAR);
		adapter.addTab(title[1], "popular", ExploreListProjectFragment.class, popularBundle);
		//最近更新
		Bundle latestdBundle = new Bundle();
		latestdBundle.putByte(EXPLORE_TYPE, TYPE_LATEST);
		adapter.addTab(title[2], "latest", ExploreListProjectFragment.class, latestdBundle);
	}
```

3 .   `ExploreListProjectFragment` 继承自`BaseSwipeRefreshFragment`同时复写了`asyncLoadList`方法，最终走到了`getList`方法中，在`getList`方法中进行不同类型数据的加载。（`BaseSwipeRefreshFragment`实现了波浪式的加载数据）

```
//复写了BaseSwipeRefreshFragment中的asyncLoadList
@Override
	public MessageData<CommonList<Project>> asyncLoadList(int page,
			boolean refresh) {
		MessageData<CommonList<Project>> msg = null;
		try {
			CommonList<Project> list = getList(type, page, refresh);
			msg = new MessageData<CommonList<Project>>(list);
		} catch (AppException e) {
			e.makeToast(mApplication);
			e.printStackTrace();
			msg = new MessageData<CommonList<Project>>(e);
		}
		return msg;
	}
	//该方法真正的加载数据的方法，其实还是走到了AppContext中，通过ApiClient来做的
	private CommonList<Project> getList(byte type, int page, boolean refresh) throws AppException {
		CommonList<Project> list = null;
		switch (type) {
		case TYPE_FEATURED:
			list = mApplication.getExploreFeaturedProject(page, refresh);
			break;
		case TYPE_POPULAR:
			list = mApplication.getExplorePopularProject(page, refresh);
			break;
		case TYPE_LATEST:
			list = mApplication.getExploreLatestProject(page, refresh);
		
			
			break;
		}
		return list;
	}
```

4 . 接下来也就是最重要的，获取`推荐栏目`的数据：
> 在`getExploreFeaturedProject`方法中，可以看到:
> 1. 如果本地没有数据或者进行了刷新操作，就会从服务器请求数据，如果获取到了数据那么就会缓存下来，请求失败时会从本地缓存加载数据。
> 2. 如果本地有混存数据那么就先加载缓存中的数据。

缓存的设置：

```
list.setCacheKey(cacheKey);
saveObject(list, cacheKey);
```

缓存的读取：

```
list = (CommonList<Project>) readObject(cacheKey);
```
具体代码：

```
/**
	 * 获取推荐项目
	 * 
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	public CommonList<Project> getExploreFeaturedProject(int page,
			boolean isRefresh) throws AppException {
		CommonList<Project> list = null;
		//缓存key
		String cacheKey = "faturedProjectList_" + page + "_" + PAGE_SIZE;
		//如果本地没有缓存数据或者用户触发了刷新操作，进入该方法
		if (!isReadDataCache(cacheKey) || isRefresh) {
			try {
				//最后还是走到了ApiClient的getExploreFeaturedProject中来进行获取数据。
				list = ApiClient.getExploreFeaturedProject(this, page);
				//如果获取的数据 !=null 就将数据进行缓存(这块的 page == 1 是什么意思呢？)
				if (list != null && page == 1) {
				//设置缓存
					list.setCacheKey(cacheKey);
					saveObject(list, cacheKey);
				}
			} catch (AppException e) {
				e.printStackTrace();
				list = (CommonList<Project>) readObject(cacheKey);
				if (list == null)
					throw e;
			}
		} else {
			// 从缓存中读取
			list = (CommonList<Project>) readObject(cacheKey);
			if (list == null)
				list = new CommonList<Project>();
		}
		return list;
	}

```

5 . 接下来就真正到了网络部分了`ApiClient`中的`getExploreFeaturedProject`：

```
/**
	 * 获得发现页面推荐项目列表
	 * 
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("serial")
	public static CommonList<Project> getExploreFeaturedProject(
			final AppContext appContext, final int page) throws AppException {
		CommonList<Project> projects = new CommonList<Project>();
		String url = makeURL(URLs.EXPLOREFEATUREDPROJECT,
				new HashMap<String, Object>() {
					{
						put("page", page);
						put(PRIVATE_TOKEN, getToken(appContext));
					}
				});
		List<Project> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}
```

在上述方法中，共做了以下动作：
> 1. 声明一个局部变量`CommonList<Project>`，用来存放从服务器返回的数据;
> 2. 拼接获取`推荐项目`的	`url`，携带两个参数：`page` 和 `token` ;
> 3. 通过`HTTPRequestor`中的`getList`获取请求数据；
> 4. 将获取到的数据设置给`CommonList<Project>`并返回

6 . 网络请求部分则被封装到了 `HTTPRequestor` 中：

```
List<Project> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Project[].class);
```
其实，我很佩服写这行代码的工程师的封装能力，下面好好剖析一下：

 *  `getHttpRequestor()` 获取 `HttpRequestor`:
 
   	```
   	private static HTTPRequestor getHttpRequestor() {
				return new HTTPRequestor();
		}
   	```
   	
 *  为请求设置HTTP请求方法：
 	
 	```
 	 /**
     * Sets the HTTP Request method for the request.
     *
     * Has a fluent api for method chaining.
     *
     * @param   method    The HTTP method
     * @return  this
     */
    public HTTPRequestor init(AppContext appContext, byte methodType, String url) {
    		_methodType = methodType;
    		//获取一个Http连接
        _httpClient = getHttpClient();
        this.mContext = appContext;
        this.url = url;
        //获得UserAgent
        String urser_agent = appContext != null ? getUserAgent(appContext) : "";
        //获得Http请求方法
        _method = getMethod(methodType, url, urser_agent);
        return this;
    }

 	```
 	
 * 获取Http连接：

 ```
 /**
	 * 获得一个http连接
	 * @return
	 */
	private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}

 ```
 
 * 获取 User-agent:

 ```
 /**
	 * 获得UserAgent
	 * @param appContext
	 * @return
	 */
	private static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("Git@OSC.NET");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App版本
			ua.append("/Android");//手机系统平台
			ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本  
			ua.append("/"+android.os.Build.MODEL); //手机型号
			ua.append("/"+appContext.getAppId());//客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}

 ```
 
 * 获取Http请求方法:

 ```
 private static HttpMethod getMethod(byte methodType, String url, String userAgent) {
		
		HttpMethod httpMethod = null;
		switch (methodType) {
		case GET_METHOD:
			httpMethod = new GetMethod(url);
			break;
		case POST_METHOD:
			httpMethod = new PostMethod(url);
			break;
		case PUT_METHOD:
			httpMethod = new PutMethod(url);
			break;
		case PATCH_METHOD:
			//httpMethod = new 
			break;
		case DELETE_METHOD:
			httpMethod = new DeleteMethod(url);
			break;
		case HEAD_METHOD:
			httpMethod = new HeadMethod(url);
			break;
		case OPTIONS_METHOD:
			httpMethod = new OptionsMethod(url);
			break;
		case TRACE_METHOD:
			httpMethod = new TraceMethod(url);
			break;
		default:
			break;
		}
		if (null == httpMethod) {
			// 抛出一个不支持的请求方法
			throw new IllegalArgumentException("Invalid HTTP Method: UnKonwn" + ". Must be one of " + METHOD.prettyValues());
		}
		// 设置 请求超时时间
		httpMethod.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpMethod.setRequestHeader("Host", URLs.HOST);
		httpMethod.setRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
		httpMethod.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
		httpMethod.setRequestHeader("Connection","Keep-Alive");
		httpMethod.setRequestHeader(HTTP.USER_AGENT, userAgent);
		return httpMethod;
	}

 ```
 
* 以上工作都准备就绪后，就到了真正的主角出场了：

> 1. 该方法中，先是声明了一个泛型列表 `List<T>`，作为返回数据的存储容器。
> 2. 然后，获取响应体的数据流，如果该流 ` == null ` 那么就跑出异常，否则进行解析
> 3. 解析好后，将返回的 泛型对象数组 `T[]`存储到 `List<T>` 中返回

```
	/**
     * 获取一个列表数据
     * @param type
     * @return
     * @throws AppException
     */
    public <T> List<T> getList(Class<T[]> type) throws AppException {
    	List<T> results = new ArrayList<T>();
    	//获取响应体流
    	InputStream inputStream = getResponseBodyStream();
    	if (inputStream == null) {
    		// 抛出一个网络异常
    		throw AppException.http(_method.getStatusLine().getStatusCode());
    	}
		try {
			//解析响应体流
			T[] _next = parse(inputStream, type, null);
			results.addAll(Arrays.asList(_next));
		} catch (IOException e) {
			throw AppException.io(e);
		}
    	
        return results;
    }

```

获取响应体流：

```
 public InputStream getResponseBodyStream() throws AppException {
    	
    	// 设置请求参数
    	if (hasOutput()) {
            submitData(_data, _method);
        }
    	//生命一个字节流
    	InputStream responseBodyStream = null;
		try 
		{
			//获取相应码
			int statusCode = _httpClient.executeMethod(_method);
			//如果相应不成功的话，向服务器上传错误信息，抛出异常
			if (statusCode != HttpStatus.SC_OK && !String.valueOf(statusCode).startsWith("2")) {
				uploadErrorToServer(mContext, url, statusCode, getMethod(_methodType) + "  " + 
						_method.getResponseBodyAsString() + "  " + getUserAgent(mContext) + "  " + getJsonString(_data));
				throw AppException.http(statusCode);
			}
			Header header = _method.getResponseHeader("Content-Encoding");
			//从响应头中获取是否位`gzip`压缩方式并将流返回
			if (header != null && header.getValue().equalsIgnoreCase("gzip")) {
				responseBodyStream = new GZIPInputStream(_method.getResponseBodyAsStream());
			} else {
				responseBodyStream = new ByteArrayInputStream(_method.getResponseBody());
			}
			
		} catch (HttpException e) {
			e.printStackTrace();
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			throw AppException.http(e);
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
			throw AppException.network(e);
		} finally {
			// 释放连接
			releaseConnection();
		}
		return responseBodyStream;
    }

```

解析上面获取的响应体流：

```
	/**
     * 对获取到的网络数据进行处理
     * @param connection
     * @param type
     * @param instance
     * @return
     * @throws IOException
     */
    private <T> T parse(InputStream inputStream, Class<T> type, T instance) throws IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(inputStream);
            String data = IOUtils.toString(reader);

            if (type != null) {
                return ApiClient.MAPPER.readValue(data, type);
            } else if (instance != null) {
                return ApiClient.MAPPER.readerForUpdating(instance).readValue(data);
            } else {
                return null;
            }
        } catch (SSLHandshakeException e) {
            throw new SSLHandshakeException("You can disable certificate checking by setting ignoreCertificateErrors on GitlabHTTPRequestor");
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

```

# 总结
上述，一路从`MainActivity`中的`setExploreShow`干到了`HTTPRequestor`中的`parse`。那么现在再原路返回看能发生什么：

> 1. `HTTPRequestor` 中的 `parse` 接收一个 `InputStream`然后从其中解析数据，返回一个泛型类型的数据；
> 2. `getList` 方法将 `parse` 解析到的数据存入 `List<T>` 后返回
> 3. `getExploreFeaturedProject`中的`List<Project>` 接收 `getList`返回的数据，并存入`CommonList<Project>` 中返回
> 4. 最后就到了`ExploreListProjectFragment` 中的`asyncLoadList`它调用了`getList`方法来获取 `AppConext ` 中 `getExploreFeatureProject` 返回的数据，进行相应的展示即可！

在这次读代码的过程中，我貌似明白了一点点:
> Session、Token、Cookie 干嘛的了，再深入学习，稍后总结一下！

