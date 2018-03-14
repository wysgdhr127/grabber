package com.grabber.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;

import com.grabber.config.Mission;
import com.grabber.constant.CommonConstant;
import com.grabber.model.Proxy;
import com.netease.common.util.MD5Util;

/**
 * 页面获取器
 * 
 * @author WY
 */

public class CapturerUtil {

	public static int connectionTimeout = 60000;// 连接超时
	public static int soTimeout = 60000;// 读取超时

	private HttpClient httpClient;
	private String httpMethod;
	private boolean needProxy;
	private int restLevel;
	private String encode;
	private boolean diskCache;
	private long diskCacheOutTimeSecond;
	private boolean pageCheckSwitch;
	private String pageCheckRegex;
	private boolean gzip;

	/**
	 * 使用Mission生成页面获取器
	 */
	public CapturerUtil(Mission mission) {

		httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");

		this.encode = mission.getEncode() != null ? mission.getEncode() : CommonConstant.ENCODING_DEFAULT;
		this.gzip = mission.isGzip();
		this.restLevel = mission.getRestLevel();
		this.needProxy = mission.getNeedProxy();
		this.diskCache = mission.getDiskCache();
		this.diskCacheOutTimeSecond = mission.getDiskCacheOutTimeSecond();
		this.httpMethod = mission.getHttpMethod() != null ? mission.getHttpMethod() : "get";
		this.pageCheckSwitch = mission.getPageCheckSwitch();
		this.pageCheckRegex = mission.getPageCheckRegex();

		if (needProxy) {
			setProxy(mission.getDomain());
		} else {
			LoggerUtil.debugLog.info("[Capturer-set-proxy] don't need set proxy [missionName = "
					+ mission.getMissionName() + "].");
		}
	}

	/**
	 * 设置代理
	 * 
	 * @param testUrl
	 *            测试的url
	 * @return
	 */
	private boolean setProxy(String testUrl) {

		Proxy proxy = ProxyUtil.getValidProxy(testUrl);
		if (proxy != null) {
			httpClient.getHostConfiguration().setProxy(proxy.getIp(), Integer.valueOf(proxy.getPort()));
			LoggerUtil.debugLog.info("[Capturer-set-proxy] set the proxy:" + proxy.toString());
			return true;
		} else {
			LoggerUtil.errorLog.error("[Capturer-set-proxy]Can't get a valid proxy.");
			return false;
		}
	}

	/**
	 * 登录网站
	 * 
	 * @param action
	 * @param userNameKey
	 * @param userNameValue
	 * @param passwordKey
	 * @param passwordValue
	 * @return
	 */
	public String login(String action, String userNameKey, String userNameValue, String passwordKey,
			String passwordValue) {

		PostMethod post = new PostMethod(action); // 访问的不是页面，而是负责登录的action
		NameValuePair username = new NameValuePair(userNameKey, userNameValue);
		NameValuePair password = new NameValuePair(passwordKey, passwordValue);
		post.setRequestBody(new NameValuePair[] { username, password });
		try {
			httpClient.executeMethod(post);
		} catch (Exception e) {
			LoggerUtil.errorLog.error(e);
		}

		post.releaseConnection();
		return post.getStatusLine().toString();
	}

	/**
	 * 登录网站，需要验证码
	 * 
	 * @param action
	 * @param userNameKey
	 * @param userNameValue
	 * @param passwordKey
	 * @param passwordValue
	 * @param checkCodeKey
	 * @param checkCodeValue
	 * @return
	 */

	public String login(String action, String userNameKey, String userNameValue, String passwordKey,
			String passwordValue, String checkCodeKey, String checkCodeValue) {

		PostMethod post = new PostMethod(action); // 访问的不是页面，而是负责登录的action
		NameValuePair username = new NameValuePair(userNameKey, userNameValue);
		NameValuePair password = new NameValuePair(passwordKey, passwordValue);
		NameValuePair checkCode = new NameValuePair(checkCodeKey, checkCodeValue);
		post.setRequestBody(new NameValuePair[] { username, password, checkCode });
		try {
			httpClient.executeMethod(post);
		} catch (Exception e) {
			LoggerUtil.errorLog.error(e);
		}

		post.releaseConnection();
		return post.getStatusLine().toString();
	}

	/**
	 * 根据url，返回页面内容
	 * 
	 * @param url
	 * @return
	 */
	public String getCiteContent(String url) {

		return getCiteContent(url, null, null, null);
	}

	/**
	 * 输入为url,param，返回页面内容
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public String getCiteContent(String url, String params) {

		return getCiteContent(url, params, null, null);
	}

	/**
	 * 输入为url，param，initialState，requestHeaderMap，返回页面内容
	 * 
	 * @param url
	 * @param params
	 * @param initialState
	 * @param requestHeaderMap
	 * @return
	 */
	public String getCiteContent(String url, String params, HttpState initialState, Map<String, String> requestHeaderMap) {

		// 保证同一时间httpClient只能运行一次，否则1、休息时间形同虚设；2、会冲突
		synchronized (httpClient) {

			File file = null;
			// 处理硬盘缓存相关事宜
			if (diskCache) {
				String urlReal = url;
				if (StringUtils.isNotBlank(url)) {
					urlReal = urlReal + params;
				}
				String fileName = MD5Util.get(url, CommonConstant.ENCODING_DEFAULT);
				LoggerUtil.debugLog.debug("[getCiteContent-diskCache] change url to fileName.[url=" + url
						+ "][fileName=" + fileName + "]");
				String filePath = CommonConstant.TMP_HTML_DIR + File.separator + fileName;
				file = new File(filePath);
				if (file.exists()) {
					long between = (System.currentTimeMillis() - file.lastModified()) / 1000;
					if (diskCacheOutTimeSecond <= 0 || between < diskCacheOutTimeSecond) {
						LoggerUtil.debugLog.debug("[getCiteContent-diskCache] success to get html from disk.[url="
								+ url + "][fileName=" + fileName + "]");
						return FileUtil.readFile(filePath, CommonConstant.ENCODING_DEFAULT);
					} else {
						LoggerUtil.debugLog
								.debug("[getCiteContent-diskCache] The html has timeout,need refresh.[diskCacheOutTimeSecond="
										+ diskCacheOutTimeSecond + "s][between=" + between + "s]");
					}
				}
			}

			// 设置http会话状态
			if (initialState != null) {
				httpClient.setState(initialState);
			}

			// 设置httpMethod，注入param
			HttpMethod httpMethodReal = null;
			if ("get".equalsIgnoreCase(httpMethod)) {
				if (StringUtils.isNotBlank(params)) {
					url += "?" + params;
				}
				httpMethodReal = new GetMethod(url);
			} else {
				httpMethodReal = new PostMethod(url);
				if (StringUtils.isNotBlank(params)) {
					String[] paramsArray = params.split("&");
					for (String param : paramsArray) {
						String[] temp = param.split("=", 2);
						if (temp != null && temp.length == 2) {
							((PostMethod) httpMethodReal).addParameter(temp[0], temp[1]);
						}
					}
				}
			}

			// 如果需要代理，则每一次都变换原始ip数值
			if (needProxy) {
				httpMethodReal.addRequestHeader("X-Forwarded-For", ProxyUtil.getRandomIp());
			}

			// 设置http头信息
			if (requestHeaderMap != null) {
				for (Entry<String, String> entry : requestHeaderMap.entrySet()) {
					httpMethodReal.setRequestHeader(entry.getKey(), entry.getValue());
				}
			}

			String html = null;
			try {
				httpClient.executeMethod(httpMethodReal);

				String status = httpMethodReal.getStatusLine().toString();
				int statusCode = httpMethodReal.getStatusLine().getStatusCode();
				// 成功后进行的处理
				InputStream stream = httpMethodReal.getResponseBodyAsStream();
				if (gzip) {
					try {
						stream = new GZIPInputStream(stream);
					} catch (Exception e) {
					}
				}
				if (statusCode == 200) {
					html = StreamUtil.inputStreamTOString(stream, encode);
					// 每一次成功后都要睡眠一段时间，防止被封，不要太嚣张
					ThreadUtil.sleepByRestLevel(restLevel);
				} else {// 失败后进行的处理
					// 如果是需要代理的，则可能已经被封掉了，需要进行判断
					if (needProxy) {
						// 重新设置一个代理，如果所有代理都不可用，则证明是网址的问题，直接返回null
						if (setProxy(url)) {
							// 设置http会话状态
							if (initialState != null) {
								httpClient.setState(initialState);
							}
							httpClient.executeMethod(httpMethodReal);
							status = httpMethodReal.getStatusLine().toString();
							if (statusCode == 200) {
								html = StreamUtil.inputStreamTOString(stream, encode);
								ThreadUtil.sleepByRestLevel(restLevel);
							}
						}
					}
					LoggerUtil.debugLog.error("[Capturer-getCiteContent] httpclient can't get [url = " + url
							+ "],[status = " + status + "], [statusCode=" + statusCode + "]");
				}
			} catch (Exception e) {
				LoggerUtil.errorLog.equals(e);
			} finally {
				if (httpMethodReal != null) {
					httpMethodReal.releaseConnection();
				}
			}
			// 对爬取的html做校验
			if (pageCheckSwitch && StringUtils.isNotBlank(html)) {
				if (StringUtils.isNotBlank(pageCheckRegex)) {
					Pattern p = Pattern.compile(pageCheckRegex);
					Matcher m = p.matcher(html);
					if (!m.find()) {
						LoggerUtil.debugLog
								.error("[Capturer-getCiteContent] the html is not fit for pageCheck, set null value. [url = "
										+ url + "],[pageCheckRegex = " + pageCheckRegex + "][html = " + html + "]");
						html = null;
					}
				}
			}

			// 进行硬盘缓存
			if (StringUtils.isNotBlank(html) && diskCache) {
				FileUtil.writeTxtFile(file.getAbsolutePath(), html, CommonConstant.ENCODING_DEFAULT);
			}
			return html;

		}
	}

	public static void main(String[] args) throws IOException {

	}
}
