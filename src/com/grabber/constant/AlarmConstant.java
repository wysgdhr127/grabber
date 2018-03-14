package com.grabber.constant;

public class AlarmConstant {
	
	public static final ThreadLocal<StringBuffer> WARNING = new ThreadLocal<StringBuffer>();
	public static final String WARNING_LEVEL_ERROR_DESC = "terrible warning: ";// 严重的警告，接近是错误，必须处理
	public static final String WARNING_LEVEL_HIGH_DESC = "serious warning: ";// 高级别的警告，需要处理
	public static final String WARNING_LEVEL_MEDIUM_DESC = "need attention warning: ";// 中等警告，需要引起注意
	public static final String WARNING_LEVEL_LOW_DESC = "light warning: ";// 轻微警告，建议
	public static final String WARNING_LEVEL_TINY_DESC = "negligible warning: ";// 可忽略的警告，可无视
	public static final String WARNING_LEVEL_ERROR = "5";// 严重的警告，接近是错误，必须处理
	public static final String WARNING_LEVEL_HIGH = "4";// 高级别的警告，需要处理
	public static final String WARNING_LEVEL_MEDIUM = "3";// 中等警告，需要引起注意
	public static final String WARNING_LEVEL_LOW = "2";// 轻微警告，建议
	public static final String WARNING_LEVEL_TINY = "1";// 可忽略的警告，可无视
}
