# 学习Token

##Token是什么？
> Token是服务端生成的一串字符串，以作客户端进行请求的一个令牌，当第一次登录后，服务器生成一个Token便将此Token返回给客户端，以后客户端只需带上这个Token前来请求数据即可，无需再次带上用户名和密码。

##Token的引入——
>Token是在客户端频繁向服务端请求数据，服务端频繁的去数据库查询用户名和密码并进行对比，判断用户名和密码正确与否，并作出相应提示，在这样的背景下，Token便应运而生

##使用Token的目的——
> Token的目的是为了减轻服务器的压力，减少频繁的查询数据库，使服务器更加健壮。
    
## 如何使用Token？

两种使用方式：

  1. **用设备号/设备mac地址作为Token（推荐）**
  
    客户端：客户端在登录的时候获取设备的设备号/mac地址，并将其作为参数传递到服务端。
    
    服务端：服务端接收到该参数后，便用一个变量来接收同时将其作为Token保存在数据库，并将该Token设置到session中，客户端每次请求的时候都要统一拦截，并将客户端传递的token和服务器端session中的token进行对比，如果相同则放行，不同则拒绝。    
    分析：此刻客户端和服务器端就统一了一个唯一的标识Token，而且保证了每一个设备拥有了一个唯一的会话。该方法的缺点是客户端需要带设备号/mac地址作为参数传递，而且服务器端还需要保存；优点是客户端不需重新登录，只要登录一次以后一直可以使用，至于超时的问题是有服务器这边来处理，如何处理？若服务器的Token超时后，服务器只需将客户端传递的Token向数据库中查询，同时并赋值给变量Token，如此，Token的超时又重新计时。    
    
  2. **用session值作为Token**    
   
    客户端：客户端只需携带用户名和密码登陆即可。   
     
    客户端：客户端接收到用户名和密码后并判断，如果正确了就将本地获取sessionID作为Token返回给客户端，客户端以后只需带上请求数据即可。    
    
    分析：这种方式使用的好处是方便，不用存储数据，但是缺点就是当session过期后，客户端必须重新登录才能进行访问数据。    

##git-osc中Session 和 Token的使用 ：

不久之前在学习登录时就第一次接触到了Token，但那个时候还不太清楚是怎么个情况，现在明白了：

> 当用户登录时，服务器会返回一个Session，在Session中夹有Token，拿到Token后，我们需要将token存起来。
> 当下次用户在发送请求的时候，就不用再携带用户名和密码了，这样可以减轻服务器的负担，只需要携带Token和相应请求需要带的参数即可。

**至此，我明白了几个问题：**

> 1. 之前学习源码时，不明白Token是干嘛的，现在明白了。
> 2. 之前源码中，有很多地方getToken，那个时候不明白，现在明白了。
> 3. 在请求数据时，在参数中，总要拼接一个token参数，那个时候不明白，现在明白了。

```
	/**
	 * 用户登录，将私有token保存
	 * 
	 * @param appContext
	 * @param username
	 * @param password
	 * @return GitlabUser用户信息
	 * @throws IOException
	 */
	public static User login(AppContext appContext, String userEmail,
			String password) throws AppException {
		String urlString = URLs.LOGIN_HTTPS;
		Session session = getHttpRequestor()
				.init(appContext, HTTPRequestor.POST_METHOD, urlString)
				.with("email", userEmail)
				.with("password", password)
				.to(Session.class);
		// 保存用户的私有token
		if (session != null && session.get_privateToken() != null) {
			String token = CyptoUtils.encode(GITOSC_PRIVATE_TOKEN, session.get_privateToken());
			appContext.setProperty(PRIVATE_TOKEN, token);
		}
		return session;
	}
```


