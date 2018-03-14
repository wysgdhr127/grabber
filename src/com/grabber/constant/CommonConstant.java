package com.grabber.constant;

import com.grabber.model.ErrorUnit;
import com.grabber.model.HandleUnit;
import com.grabber.model.ParseUnit;

/**
 * @author WY 14 Mar 2018 14:52:17
 */
public class CommonConstant {

	public static final String REGEX_RESULT_SPLITOR = ",";
	public static final String ENCODING_DEFAULT = "utf-8";
	public static final String ENCODING_GBK = "GBK";
	public static final String ENCODING_NULL = null;

	public static String SEPARATOR_CRAWLER = "<&&>";
	public static String CRAWLER_OVER_URL = "----crawler--over--crawler----";
	public static ErrorUnit END_ERRORUNIT = new ErrorUnit(null, null, null);
	public static ParseUnit END_PARSEUNIT = new ParseUnit(CRAWLER_OVER_URL, 0);
	public static HandleUnit END_HANDLEUNIT = new HandleUnit(null);

	/** -----model与controller的系统配置-----开始----- */
	public static final String MODEL_DIR = "config/model";// model配置文件的目录，在configuration.properties中作为key出现
	public static final String MISSIONS_DIR = "config/mission";// controller配置文件的目录，在configuration.properties中作为key出现
	public static final String ENTRANCE_DIR = "config/entrance";// controller配置文件的目录，在configuration.properties中作为key出现
	public static final String CRAWLER_CONDITION_DIR = "config/crawler_condition";// controller配置文件的目录，在configuration.properties中作为key出现
	public static final String RULE_DIR = "config/rule";// Digester的rule配置文件的目录，在configuration.properties中作为key出现
	public static final String MODEL_RULE = "model_rule";// 解析model的rule的配置文件名，在configuration.properties中作为key出现
	public static final String MISSIONS_RULE = "mission_rule";// 解析controller的rule的配置文件名，在configuration.properties中作为key出现
	public static final String CRAWLER_CONDITION_RULE = "crawler_rule";// 解析controller的rule的配置文件名，在configuration.properties中作为key出现
	public static final String TMP_PIC_DIR = "tmp/pic"; // 临时图片存放区
	public static final String TMP_TXT_DIR = "tmp/doc"; // 临时文档存放区
	public static final String TMP_HTML_DIR = "tmp/html"; // 临时网页存放区
	/** -----model与controller的系统配置-----结束----- */

	public static String getCrawlerName(String missionName) {

		return missionName + "_crawler";
	}

	public static String getParserName(String missionName) {

		return missionName + "_parser";
	}

	public static String getHandleName(String missionName) {

		return missionName + "_handle";
	}
}
