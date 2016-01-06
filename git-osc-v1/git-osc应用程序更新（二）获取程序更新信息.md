#获取程序更新信息

接上一节，来学习客户端请求服务器获取程序的更新信息,获取App更新信息的时候，程序会走到`ApiClient`中的`getUpdateInfo`中：

```java
	/**
	 * 获得App更新的信息
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static Update getUpdateInfo(AppContext appContext) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.UPDATE, params);
		return getHttpRequestor().init(appContext, GET_METHOD, url)
				.to(Update.class);
	}
```
在`getUpdateInfo`方法中，都做了什么呢？
> 1.拼接了一个参数`params`,该参数携带了一个`Token`，**我不太明白的地方：这块在url中带个token是干嘛的呢？**
> 
> 2.在拼接参数的时候，走到了方法`makeURL`中，请求服务器的`url`是在这里拼接完成的。
> `String url = makeURL(URLs.UPDATE, params);`
> 
> 3.接着就是真正的访问服务器了，在服务器请求后，会返回一个`Update`对象。这块儿还是比较复杂的，下面会仔细分析。
> `getHttpRequestor().init(appContext, GET_METHOD, url).to(Update.class)`

***
##第一步：**获取`Token`**,通过`Map`集合生成一个`Token`。
```
Map<String, Object> params = new HashMap<String, Object>();
params.put(PRIVATE_TOKEN, getToken(appContext));
```
还是那个问题，**为什么要在参数里专门放一个`Token`？**。
那么问题来了：
> 1. `Token` 从哪儿来的？
> 
> 2. `Token` 在哪儿存放的？

可以在`put`方法中看到，调用了方法`getToken()`:

```
	/**
	 * 获得private_token
	 * 
	 * @param appContext
	 * @return
	 */
	public static String getToken(AppContext appContext) {
		if (private_token == null || private_token == "") {
			private_token = appContext.getProperty(PRIVATE_TOKEN);
		}
		return CyptoUtils.decode(GITOSC_PRIVATE_TOKEN, private_token);
	}
```

可以看到，当`private_token == null || private_token == ""`时，会调用方法`getProperty()`:

```
	public String getProperty(String key) {
		String res = AppConfig.getAppConfig(this).get(key);
		return res;
	}
```

也就是说，这个`Token`是在`Properties`中存放的，那么问题来了**该Token又是在哪儿存放进Properties的呢？**
接着是，通过解密返回一个`Token`字符串。

##第二步：拼接参数，调用方法`makeURL`
```
	/**
	 * 给一个url拼接参数
	 * 
	 * @param p_url
	 * @param params
	 * @return
	 */
	private static String makeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (params.size() == 0)
			return p_url;
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			String value = String.valueOf(params.get(name));
			if (value != null && !StringUtils.isEmpty(value)
					&& !value.equalsIgnoreCase("null")) {
				url.append('&');
				url.append(name);
				url.append('=');
				// 对参数进行编码
				try {
					url.append(URLEncoder.encode(
							String.valueOf(params.get(name)), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return url.toString().replace("?&", "?");
	}

```
在该方法中进行了一系列的操作，最后拼接的url应该也就是：`https://www.google.com.hk/?gws_rd=ssl#newwindow=1&safe=strict&q=yes`样子吧！

##第三步：请求服务器，在服务器请求成功后，会返回一个`Update`对象，自己感觉这块还是挺复杂的

接下来都做了什么呢？

 1. 调用`getHttpRequestor()`方法，获取一个`HTTPRequestor`对象

  ```
   private static HTTPRequestor getHttpRequestor() {
		return new HTTPRequestor();
	}
  ```
  
 2. 调用`HTTPRequestor`的`init`方法，进行一系列的初始化
	
	这是具体的调用，在调用时传入参数：`context`、`method`、`url`
	
	```
	init(appContext, GET_METHOD, url)
	```
	
	这是真正的方法：
	
	```
	 public HTTPRequestor init(AppContext appContext, byte methodType, String url) {
    		
    		_methodType = methodType;
        	_httpClient = getHttpClient();
        
        	this.mContext = appContext;
        	this.url = url;
        
        String urser_agent = appContext != null ? getUserAgent(appContext) : "";
        _method = getMethod(methodType, url, urser_agent);
        return this;
    }

	```
	
	在该方法中，做了很多事：
	
	1.  获取`HttpClient`，并进行了一系列的设置，都是使用HttpClient应该注意的

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
	
	2. 接着是获取`UserAgent`,获取用户设备的详细信息

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
	
	3. 接着是获取`Method`，获取具体的请求方法：

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



3. 调用 `to`方法，将会返回一个`Update`对象，它存放了在服务器上的APP版本信息

```
 public <T> T to(Class<T> type) throws AppException {
        return to(type, null);
 }
```

接着走到了真正的`to`方法中，在该方法中做了两件事：

> 1. 读取相应实体流 `getResponseBodyStream()`;
> 
> 2. 解析实体流 `parse(inputStream, type, instance)`;

```
	/**
     * 获取单个对象
     * @param type
     * @param instance
     * @return
     * @throws AppException
     */
    public <T> T to(Class<T> type, T instance) throws AppException {
        
    	InputStream inputStream = getResponseBodyStream();
    	try {
			return parse(inputStream, type, instance);
		} catch (IOException e) {
			throw AppException.io(e);
		}
    }
```

读取相应实体流 `getResponseBodyStream()` 的方法，这块要注意的地方是，在 `try-catch`块中最后的 `finally`的使用——释放连接：

```
 public InputStream getResponseBodyStream() throws AppException {
    	
    	// 设置请求参数
    	if (hasOutput()) {
            submitData(_data, _method);
        }
    	
    	InputStream responseBodyStream = null;
		try 
		{
			int statusCode = _httpClient.executeMethod(_method);
			if (statusCode != HttpStatus.SC_OK && !String.valueOf(statusCode).startsWith("2")) {
				uploadErrorToServer(mContext, url, statusCode, getMethod(_methodType) + "  " + _method.getResponseBodyAsString() + "  " + getUserAgent(mContext) + "  " + getJsonString(_data));
				throw AppException.http(statusCode);
			}
			Header header = _method.getResponseHeader("Content-Encoding");
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

解析流 `parse(InputStream inputStream, Class<T> type, T instance) `：

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


#收获
>1. Http 请求时参数的拼接方式
>2. Http 的使用，因为之前并没有真正的接触过Http的详细使用
>3. Token的使用，虽然我不是非常清楚Token，但我知道了 Token 的存在，总有一天，我会搞明白的！


