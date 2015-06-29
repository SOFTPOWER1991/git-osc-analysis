package net.oschina.gitapp.api;

import static net.oschina.gitapp.api.HTTPRequestor.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import android.graphics.Bitmap;
import android.util.Log;
import net.oschina.gitapp.AppContext;
import net.oschina.gitapp.AppException;
import net.oschina.gitapp.bean.Branch;
import net.oschina.gitapp.bean.CodeFile;
import net.oschina.gitapp.bean.CodeTree;
import net.oschina.gitapp.bean.Comment;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.CommonList;
import net.oschina.gitapp.bean.Event;
import net.oschina.gitapp.bean.GitNote;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Language;
import net.oschina.gitapp.bean.LuckMsg;
import net.oschina.gitapp.bean.Milestone;
import net.oschina.gitapp.bean.NotificationReadResult;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.bean.ProjectNotificationArray;
import net.oschina.gitapp.bean.RandomProject;
import net.oschina.gitapp.bean.ReadMe;
import net.oschina.gitapp.bean.Session;
import net.oschina.gitapp.bean.ShippingAddress;
import net.oschina.gitapp.bean.StarWatchOptionResult;
import net.oschina.gitapp.bean.UpLoadFile;
import net.oschina.gitapp.bean.Update;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.bean.URLs;
import net.oschina.gitapp.common.CyptoUtils;
import net.oschina.gitapp.common.StringUtils;

/**
 * API客户端接口：用于访问网络数据
 * 
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class ApiClient {

	private final static String PRIVATE_TOKEN = "private_token";
	private final static String GITOSC_PRIVATE_TOKEN = "git@osc_token";

	// 私有token，每个用户都有一个唯一的
	private static String private_token;
	public static final ObjectMapper MAPPER = new ObjectMapper().configure(
			DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	/**
	 * 清除private_token，注销登录的时候清除
	 */
	public static void cleanToken() {
		private_token = "";
	}

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

	private static HTTPRequestor getHttpRequestor() {
		return new HTTPRequestor();
	}

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

	/**
	 * 获得一个用户的信息
	 * 
	 * @param appContext
	 * @param userId
	 * @return
	 * @throws AppException
	 */
	public static User getUser(AppContext appContext, int userId)
			throws AppException {
		String url = URLs.USER + URLs.URL_SPLITTER + userId;
		return getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD,
				url).to(User.class);
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		return getHttpRequestor().init(null, GET_METHOD, url)
				.getNetBitmap();
	}

	/**
	 * 获得一个项目的信息
	 * 
	 * @param appContext
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("serial")
	public static Project getProject(final AppContext appContext, String projectId)
			throws AppException {
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId,
				new HashMap<String, Object>() {
					{
						put(PRIVATE_TOKEN, getToken(appContext));
					}
				});
		return getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD,
				url).to(Project.class);
	}
	
	/**
	 * 获得具体用户的项目列表
	 * @param appContext
	 * @param urerId
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Project> getUserProjects(final AppContext appContext, String urerId, int page) throws AppException {
		CommonList<Project> res = new CommonList<Project>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		String url = makeURL(URLs.USER + URLs.URL_SPLITTER + urerId + URLs.URL_SPLITTER + "projects", params);
		List<Project> list = getHttpRequestor().init(appContext, GET_METHOD, url)
				.getList(Project[].class);
		res.setCount(list.size());
		res.setPageSize(list.size());
		res.setList(list);
		return res;
	}

	/**
	 * 获得发现页面最近更新的项目列表
	 * 
	 * @param appContext
	 * @param page
	 *            页数
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("serial")
	public static CommonList<Project> getExploreLatestProject(
			final AppContext appContext, final int page) throws AppException {
		CommonList<Project> projects = new CommonList<Project>();
		String url = makeURL(URLs.EXPLORELATESTPROJECT,
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

	/**
	 * 获得发现页面热门项目列表
	 * 
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("serial")
	public static CommonList<Project> getExplorePopularProject(
			final AppContext appContext, final int page) throws AppException {
		CommonList<Project> projects = new CommonList<Project>();
		String url = makeURL(URLs.EXPLOREPOPULARPROJECT,
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
	
	/**
	 * 获得查询项目的结果
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("deprecation")
	public static List<Project> getSearcheProject(AppContext appContext, String query, int page) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		String url = makeURL(URLs.SEARCHPROJECT + URLs.URL_SPLITTER + URLEncoder.encode(query), params);
		return getHttpRequestor().init(appContext, GET_METHOD, url)
				.getList(Project[].class);
	}

	/**
	 * 获得个人动态列表
	 * 
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("serial")
	public static CommonList<Event> getMySelfEvents(
			final AppContext appContext, final int page) throws AppException {
		CommonList<Event> events = new CommonList<Event>();
		String url = makeURL(URLs.EVENTS, new HashMap<String, Object>() {
			{
				put("page", page);
				put(PRIVATE_TOKEN, getToken(appContext));
			}
		});
		final List<Event> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Event[].class);
		events.setList(list);
		events.setCount(list.size());
		events.setPageSize(list.size());
		return events;
	}

	/**
	 * 获得个人的所有项目
	 * 
	 * @param appContext
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Project> getMySelfProjectList(
			AppContext appContext, int page) throws AppException {
		CommonList<Project> msProjects = new CommonList<Project>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		String url = makeURL(URLs.PROJECT, params);
		List<Project> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Project[].class);
		msProjects.setList(list);
		msProjects.setCount(list.size());
		msProjects.setPageSize(list.size());
		return msProjects;
	}
	
	/**
	 * 获取具体用户的最近动态列表
	 * @param appContext
	 * @param user_id
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Event> getUserEvents(AppContext appContext, String user_id ,int page) throws AppException {
		CommonList<Event> events = new CommonList<Event>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		String url = makeURL(URLs.USEREVENTS + URLs.URL_SPLITTER + user_id, params);
		List<Event> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Event[].class);
		events.setList(list);
		events.setCount(list.size());
		events.setPageSize(list.size());
		return events;
	}

	/**
	 * 获得一个项目的commit列表
	 * 
	 * @param appContext
	 * @param projectId
	 *            指定项目的id
	 * @param page
	 *            页码
	 * @param ref_name
	 *            分支（optional）
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Commit> getProjectCommitList(
			AppContext appContext, int projectId, int page, String ref_name)
			throws AppException {
		CommonList<Commit> commits = new CommonList<Commit>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		params.put("ref_name", ref_name);
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/commits", params);
		List<Commit> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Commit[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}

	/**
	 * 获得项目的代码树列表
	 * 
	 * @param appContext
	 * @param projectId
	 *            项目的id
	 * @param path
	 *            (optional) 路径
	 * @param ref_name
	 *            (optional) 分支或者标签，空则为默认的master分支
	 * @return
	 * @throws AppException
	 */
	public static CommonList<CodeTree> getProjectCodeTree(
			AppContext appContext, int projectId, String path, String ref_name)
			throws AppException {
		CommonList<CodeTree> codeTree = new CommonList<CodeTree>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("path", path);
		params.put("ref_name", ref_name);
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/tree", params);
		List<CodeTree> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(CodeTree[].class);
		codeTree.setList(list);
		codeTree.setCount(list.size());
		codeTree.setPageSize(list.size());
		return codeTree;
	}

	/**
	 * 获得一个项目issues列表
	 * 
	 * @param appContext
	 * @param projectId
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Issue> getProjectIssuesList(AppContext appContext,
			int projectId, int page) throws AppException {
		CommonList<Issue> commits = new CommonList<Issue>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "issues", params);
		List<Issue> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Issue[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}

	/**
	 * 获得一个项目的Branchs或者Tags列表
	 * 
	 * @param appContext
	 * @param projectId
	 * @param page
	 * @param branchOrTag
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Branch> getProjectBranchsOrTagsLsit(
			AppContext appContext, String projectId, int page,
			String branchOrTag) throws AppException {
		CommonList<Branch> commits = new CommonList<Branch>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository" + URLs.URL_SPLITTER
				+ branchOrTag, params);
		List<Branch> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Branch[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}
	
	/**
	 * 获得issue的详情
	 * @param appContext
	 * @param projectId
	 * @param issueId
	 * @return
	 * @throws AppException
	 */
	public static Issue getIssue(AppContext appContext, String projectId, String issueId) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId + "/issues/" + issueId, params);
		return getHttpRequestor().init(appContext, GET_METHOD, url)
				.to(Issue.class);
	}

	/**
	 * 获得issue的评论列表
	 * 
	 * @param appContext
	 * @param projectId
	 * @param noteId
	 * @param page
	 * @param isRefresh
	 * @return
	 * @throws Exception
	 */
	public static CommonList<GitNote> getIssueCommentList(
			AppContext appContext, String projectId, String issueId, int page)
			throws Exception {
		CommonList<GitNote> commits = new CommonList<GitNote>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "issues" + URLs.URL_SPLITTER + issueId
				+ URLs.URL_SPLITTER + "notes", params);
		List<GitNote> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(GitNote[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}

	/**
	 * 提交issue的一个评论
	 * 
	 * @param appContext
	 * @param projectId
	 * @param issueId
	 * @param body
	 * @return
	 * @throws AppException
	 */
	public static GitNote pubIssueComment(AppContext appContext,
			String projectId, String issueId, String body) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "issues" + URLs.URL_SPLITTER + issueId
				+ URLs.URL_SPLITTER + "notes", params);
		return getHttpRequestor()
				.init(appContext, HTTPRequestor.POST_METHOD, url)
				.with("body", body).to(GitNote.class);
	}

	/**
	 * 获得代码文件详情
	 * 
	 * @param appContext
	 * @param projectId
	 * @param file_path
	 * @param ref
	 * @return
	 * @throws AppException
	 */
	public static CodeFile getCodeFile(AppContext appContext, String projectId,
			String file_path, String ref) throws AppException {
		CodeFile codeFile = null;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("file_path", file_path);
		params.put("ref", ref);
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/files", params);
		codeFile = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).to(CodeFile.class);
		return codeFile;
	}
	
	/**
	 * 获得一个项目的readme文件
	 * @param appContext
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public static ReadMe getReadMeFile(AppContext appContext, String projectId) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId + URLs.URL_SPLITTER + "readme", params);
		return getHttpRequestor()
				.init(appContext, GET_METHOD, url)
				.to(ReadMe.class);
	}

	/**
	 * 获得commit文件diff
	 * 
	 * @param appContext
	 * @param projectId
	 * @param commitId
	 * @return
	 * @throws Exception
	 */
	public static CommonList<CommitDiff> getCommitDiffList(
			AppContext appContext, String projectId, String commitId)
			throws Exception {
		CommonList<CommitDiff> commits = new CommonList<CommitDiff>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/commits" + URLs.URL_SPLITTER
				+ commitId + URLs.URL_SPLITTER + "diff", params);
		List<CommitDiff> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(CommitDiff[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}

	/**
	 * 获得commit的评论列表
	 * 
	 * @param appContext
	 * @param projectId
	 * @param commitId
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Comment> getCommitCommentList(
			AppContext appContext, String projectId, String commitId,
			boolean isRefresh) throws AppException {
		CommonList<Comment> commits = new CommonList<Comment>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		// 拼接url地址
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/commits" + URLs.URL_SPLITTER
				+ commitId + URLs.URL_SPLITTER + "comment", params);
		List<Comment> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Comment[].class);
		commits.setList(list);
		commits.setCount(list.size());
		commits.setPageSize(list.size());
		return commits;
	}

	/**
	 * 通过commits获取代码文件的内容
	 * 
	 * @param appContext
	 * @param projectId
	 * @param commitId
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static String getCommitFileDetail(AppContext appContext,
			String projectId, String commitId, String filePath)
			throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("filepath", filePath);
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "repository/commits" + URLs.URL_SPLITTER
				+ commitId + URLs.URL_SPLITTER + "blob", params);
		return getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD,
				url).getResponseBodyString();
	}

	/**
	 * 加载项目的参与成员
	 * 
	 * @param appContext
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public static List<User> getProjectMembers(AppContext appContext,
			String projectId) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "members", params);
		return getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD,
				url).getList(User[].class);
	}

	/**
	 * 加载项目的里程碑
	 * 
	 * @param appContext
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public static List<Milestone> getProjectMilestone(AppContext appContext,
			String projectId) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "milestones", params);
		return getHttpRequestor().init(appContext, HTTPRequestor.GET_METHOD,
				url).getList(Milestone[].class);
	}

	/**
	 * 创建一个issue
	 * 
	 * @param appContext
	 * @param projectId
	 * @param title
	 * @param description
	 * @param assignee_id
	 * @param milestone_id
	 * @return
	 * @throws AppException
	 */
	public static Issue pubCreateIssue(AppContext appContext,
			String projectId, String title, String description,
			String assignee_id, String milestone_id) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId
				+ URLs.URL_SPLITTER + "issues", params);
		return getHttpRequestor()
				.init(appContext, HTTPRequestor.POST_METHOD, url)
				.with("description", description)
				.with("title", title)
				.with("assignee_id", assignee_id)
				.with("milestone_id", milestone_id)
				.to(Issue.class);
	}

	/**
	 * 上传文件
	 * 
	 * @param appContext
	 * @param files
	 * @return
	 */
	public static UpLoadFile upLoadFile(AppContext appContext, File file)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.UPLOAD, params);
		return getHttpRequestor()
				.init(appContext, HTTPRequestor.POST_METHOD, url)
				.with("file", file).to(UpLoadFile.class);
	}
	
	/**
	 * 获得通知
	 * @param appContext
	 * @param filter
	 * @param all
	 * @param project_id
	 * @return
	 * @throws AppException
	 */
	public static CommonList<ProjectNotificationArray> getNotification(
			AppContext appContext, String filter, String all, String project_id)
			throws AppException {
		CommonList<ProjectNotificationArray> projectNotifications = new CommonList<ProjectNotificationArray>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("filter", filter);
		params.put("all", all);
		String url = makeURL(URLs.NOTIFICATION, params);
		List<ProjectNotificationArray> list = getHttpRequestor().
				init(appContext, HTTPRequestor.GET_METHOD, url)
				.getList(ProjectNotificationArray[].class);
		projectNotifications.setList(list);
		projectNotifications.setCount(list.size());
		projectNotifications.setPageSize(list.size());
		return projectNotifications;
	}
	
	/**
	 * 设置通知为已读
	 * @param appContext
	 * @param notificationId
	 * @return
	 * @throws AppException
	 */
	public static NotificationReadResult setNotificationIsRead(AppContext appContext, String notificationId) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.NOTIFICATION_READED + URLs.URL_SPLITTER + notificationId, params);
		return getHttpRequestor().init(appContext, GET_METHOD, url)
				.to(NotificationReadResult.class);
	}
	
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
	
	/**
	 * 获得语言列表
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Language> getLanguageList(AppContext appContext) throws AppException {
		CommonList<Language> languages = new CommonList<Language>();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + "languages", params);
		List<Language> list = getHttpRequestor().init(appContext, GET_METHOD, url)
				.getList(Language[].class);
		languages.setCount(list.size());
		languages.setList(list);
		languages.setPageSize(list.size());
		return languages;
	}
	
	/**
	 * 根据语言的ID获得项目的列表
	 * @param appContext
	 * @param languageId
	 * @param page
	 * @return
	 * @throws AppException
	 */
	public static List<Project> getLanguageProjectList(AppContext appContext, String languageId, int page) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + "languages" + URLs.URL_SPLITTER + languageId, params);
		return getHttpRequestor().init(appContext, GET_METHOD, url)
				.getList(Project[].class);
	}
	
	/**
	 * star or unstar一个项目
	 * @param appContext
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public static StarWatchOptionResult starProject(AppContext appContext, String projectId, String type) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = "";
		if (type.equalsIgnoreCase("star")) {
			url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId + URLs.URL_SPLITTER + "star", params);
		} else {
			url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId + URLs.URL_SPLITTER + "unstar", params);
		}
		return getHttpRequestor().init(appContext, POST_METHOD, url)
				.to(StarWatchOptionResult.class);
	}
	
	/**
	 * watch or unwatch一个项目
	 * @param appContext
	 * @param projectId
	 * @return
	 * @throws AppException
	 */
	public static StarWatchOptionResult watchProject(AppContext appContext, String projectId, String type) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = "";
		if (type.equalsIgnoreCase("watch")) {
			url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId + URLs.URL_SPLITTER + "watch", params);
		} else {
			url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + projectId + URLs.URL_SPLITTER + "unwatch", params);
		}
		return getHttpRequestor().init(appContext, POST_METHOD, url)
				.to(StarWatchOptionResult.class);
	}
	
	/**
	 * 随机获取一个项目
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static RandomProject getRandomProject(AppContext appContext) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("luck", 1);
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + "random", params);
		return getHttpRequestor().init(appContext, GET_METHOD, url)
				.to(RandomProject.class);
	}
	
	/**
	 * 更新代码库中的代码文件
	 * @param appContext
	 * @param project_id
	 * @param branch_name
	 * @param content
	 * @param commit_message
	 * @return
	 * @throws AppException
	 */
	public static String updateRepositoryFiles(AppContext appContext, String project_id, String ref, String file_path, String branch_name, String content, String commit_message) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + project_id + "/repository/files", params);
		return getHttpRequestor().init(appContext, POST_METHOD, url)
				.with("ref", ref)
				.with("file_path", file_path)
				.with("branch_name", branch_name)
				.with("content", content)
				.with("commit_message", commit_message)
				.getResponseBodyString();
	}
	
	/**
	 * 获得某个用户star的项目列表
	 * @param appContext
	 * @param user_id
	 * @param page
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Project> getStarProjectList(AppContext appContext, String user_id, int page, boolean isRefresh) throws AppException {
		CommonList<Project> projects = new CommonList<Project>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		// 拼接url地址
		String url = makeURL(URLs.USER + URLs.URL_SPLITTER + user_id
				+ URLs.URL_SPLITTER + "stared_projects", params);
		List<Project> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}
	
	/**
	 * 获得某个用户watched的项目列表
	 * @param appContext
	 * @param user_id
	 * @param page
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public static CommonList<Project> getWatchProjectList(AppContext appContext, String user_id, int page, boolean isRefresh) throws AppException {
		CommonList<Project> projects = new CommonList<Project>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		params.put("page", page);
		// 拼接url地址
		String url = makeURL(URLs.USER + URLs.URL_SPLITTER + user_id
				+ URLs.URL_SPLITTER + "watched_projects", params);
		List<Project> list = getHttpRequestor().init(appContext,
				HTTPRequestor.GET_METHOD, url).getList(Project[].class);
		projects.setList(list);
		projects.setCount(list.size());
		projects.setPageSize(list.size());
		return projects;
	}
	
	/**
	 * 获取用户的收货信息
	 * @param appContext
	 * @param user_id
	 * @return
	 * @throws AppException
	 */
	public static ShippingAddress getUserShippingAddress(AppContext appContext, String user_id) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.URL_API_HOST + "users" + URLs.URL_SPLITTER + user_id + URLs.URL_SPLITTER + "address", params);
		return getHttpRequestor().init(appContext, GET_METHOD, url)
				.to(ShippingAddress.class);
	}
	
	/**
	 * 更新用户的收货信息
	 * @param appContext
	 * @param user_id
	 * @param shippingAddress
	 * @return
	 * @throws AppException
	 */
	public static ShippingAddress updateUserShippingAddress(AppContext appContext, String user_id, ShippingAddress shippingAddress) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.URL_API_HOST + "users" + URLs.URL_SPLITTER + user_id + URLs.URL_SPLITTER + "address", params);
		return getHttpRequestor().init(appContext, POST_METHOD, url)
				.with("name", shippingAddress.getName())
				.with("tel", shippingAddress.getTel())
				.with("address", shippingAddress.getAddress())
				.with("comment", shippingAddress.getComment())
				.to(ShippingAddress.class);
	}
	
	/**
	 * 获得抽奖活动的信息
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static LuckMsg getLuckMsg(AppContext appContext) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PRIVATE_TOKEN, getToken(appContext));
		String url = makeURL(URLs.PROJECT + URLs.URL_SPLITTER + "luck_msg", params);
		return getHttpRequestor().init(appContext, GET_METHOD, url)
				.to(LuckMsg.class);
	}
}
