package com.grabber.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;

import com.grabber.model.Proxy;

public class ProxyUtil {

	private static String source = "conf/proxy.xls";
	private static ArrayList<Proxy> proxyList;
	private static String baseTestUrl = "http://www.baidu.com";

	private static boolean init() {

		// 重置proxyList
		proxyList = new ArrayList<Proxy>();

		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(new File(source));

			Sheet sheet = workbook.getSheet(0);

			for (int i = 1; i < sheet.getRows(); i++) {
				Proxy proxy = new Proxy();
				int j = 0;

				proxy.setIp(sheet.getCell(j++, i).getContents().trim());
				proxy.setPort(sheet.getCell(j++, i).getContents().trim());
				proxy.setType(sheet.getCell(j++, i).getContents().trim());
				proxy.setSpeed(sheet.getCell(j++, i).getContents().trim());
				proxy.setAreas(sheet.getCell(j++, i).getContents().trim());

				proxyList.add(proxy);
			}

		} catch (Exception e) {
			LoggerUtil.errorLog.equals(e);
			return false;
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}

		return true;
	}

	/**
	 * 给定测试地址，获取针对testUrl的可用代理
	 * 
	 * @param testUrl
	 * @return
	 */
	public static Proxy getValidProxy(String testUrl) {

		// 如果proxyList无效，则进行初始化
		if (proxyList == null || proxyList.isEmpty()) {
			init();
		}
		if (proxyList != null && !proxyList.isEmpty()) {

			Iterator<Proxy> iterator = proxyList.iterator();
			while (iterator.hasNext()) {
				Proxy proxy = iterator.next();
				// 代理是否对待访问的网址有效
				if (isValidProxy(testUrl, proxy.getIp(), proxy.getPort())) {
					return proxy;
				} else {// 代理无效，进一步判断代理是否针真的无效
					if (!isValidProxy(baseTestUrl, proxy.getIp(),
							proxy.getPort())) {
						iterator.remove();
					}
				}

			}
		}

		return null;
	}

	/**
	 * 判断代理是否可用，测试地址默认为baseTestUrl
	 * 
	 * @param proxyIp
	 * @param proxyPort
	 * @return
	 */
	public static boolean isValidProxy(String proxyIp, String proxyPort) {

		return isValidProxy(baseTestUrl, proxyIp, proxyPort);
	}

	/**
	 * 判断代理是否可用，给定测试地址testUrl
	 * 
	 * @param testUrl
	 * @param proxyIp
	 * @param proxyPort
	 * @return
	 */
	public static boolean isValidProxy(String testUrl, String proxyIp,
			String proxyPort) {

		boolean isValid = false;

		if (StringUtils.isNotBlank(proxyIp)
				&& StringUtils.isNotBlank(proxyPort)) {

			int port = Integer.valueOf(proxyPort);

			HttpClient httpClient = new HttpClient();
			httpClient.getHttpConnectionManager().getParams()
					.setConnectionTimeout(2000);
			httpClient.getHttpConnectionManager().getParams()
					.setSoTimeout(2000);

			httpClient.getHostConfiguration().setProxy(proxyIp, port);
			GetMethod getMethod = new GetMethod(testUrl);

			try {
				httpClient.executeMethod(getMethod);
			} catch (Exception e) {
				return false;
			} finally {
				if (getMethod != null) {
					getMethod.releaseConnection();
				}
			}
			int statusCode = getMethod.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				isValid = true;
			} else {
				isValid = false;
			}
		}
		return isValid;
	}

	/**
	 * 获取随机ip
	 * 
	 * @return
	 */
	public static String getRandomIp() {

		StringBuffer ip = new StringBuffer();
		Random r = new Random();

		for (int i = 0; i < 4; ++i) {
			ip.append(r.nextInt(253) + 1);
			if (i != 3) {
				ip.append(".");
			}
		}
		return ip.toString();
	}

	/**
	 * 获取真实的url
	 * 
	 * @param domain
	 * @param site
	 * @return
	 */
	public static String getRealUrl(String domain, String site) {

		if (site.indexOf("http://") != -1) {
			return site;
		} else {
			return domain + site;
		}
	}

	public static void main(String[] args) {

		System.out.println(ProxyUtil.getRandomIp());

		// 运行爬取任务，爬取可用代理
		// BaseDto<Proxy> baseDto = new BaseDto<Proxy>("proxy", 1, 1, 1);
		// baseDto.start();
	}

}
