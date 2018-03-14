package com.grabber.util;

import org.apache.log4j.Logger;

public class LoggerUtil {
	
	public static final Logger debugLog = Logger.getLogger("debug");
	public static final Logger errorLog = Logger.getLogger("error");
	public static final Logger parseLog = Logger.getLogger("parse");// 存储解析结果的log
	public static final Logger crawlLog = Logger.getLogger("crawl");// 存储爬行获取来的url
	public static final Logger handleLog = Logger.getLogger("handle"); // 后处理日志
	public static final Logger resultLog = Logger.getLogger("result");// 存储爬行获取来的url
	public static final Logger mailLog = Logger.getLogger("mail");// 存储爬行获取来的url
}
