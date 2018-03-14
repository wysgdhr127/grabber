package com.grabber.util;

import org.apache.commons.lang.StringUtils;

import com.grabber.constant.AlarmConstant;
import com.grabber.constant.ThreadConstant;

/**
 * @author WY 14 Mar 2018 16:10:31
 */
public class ThreadUtil {
	
	public static void sleep(long millis) {

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LoggerUtil.errorLog.error(e);
		}
	}
	
	/**
	 * 休息在min与max之间任意一段时间
	 */
	public static void sleepRandomMillis(long min, long max) {

		if (min == max && min == 0) {
			return;
		}
		long millis = min;
		millis += Math.round(Math.random() * (max - min));
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LoggerUtil.errorLog.error(e);
		}
	}
	
	/**
	 * 根据级别来暂停线程
	 * 
	 * @param restLevel
	 */
	public static void sleepByRestLevel(int restLevel) {

		if (restLevel < 0) {
			restLevel = 0;
		} else if (restLevel > 10) {
			restLevel = 10;
		}
		switch (restLevel) {
			case 0:
				break;
			case 1:
				sleepRandomMillis(ThreadConstant.minLevel1, ThreadConstant.maxLevel1);
				break;
			case 2:
				sleepRandomMillis(ThreadConstant.minLevel1, ThreadConstant.maxLevel2);
				break;
			case 3:
				sleepRandomMillis(ThreadConstant.minLevel1, ThreadConstant.maxLevel3);
				break;
			case 4:
				sleepRandomMillis(ThreadConstant.minLevel1, ThreadConstant.maxLevel4);
				break;
			case 5:
				sleepRandomMillis(ThreadConstant.minLevel1, ThreadConstant.maxLevel5);
				break;
			case 6:
				sleepRandomMillis(ThreadConstant.minLevel4, ThreadConstant.maxLevel5);
				break;
			case 7:
				sleepRandomMillis(ThreadConstant.minLevel4, ThreadConstant.maxLevel6);
				break;
			case 8:
				sleepRandomMillis(ThreadConstant.minLevel4, ThreadConstant.maxLevel7);
				break;
			case 9:
				sleepRandomMillis(ThreadConstant.minLevel5, ThreadConstant.maxLevel7);
				break;
			case 10:
				sleepRandomMillis(ThreadConstant.minLevel6, ThreadConstant.maxLevel7);
				break;
		}
	}
	
	/**
	 * 当前线程插入warning信息
	 */
	public static void appendWaringMessage(String message, String warningLevel) {

		StringBuffer sb = AlarmConstant.WARNING.get();
		if (sb == null) {
			sb = new StringBuffer();
			AlarmConstant.WARNING.set(sb);
		}
		String warning = warningLevel;
		if (StringUtils.isNumeric(warningLevel)) {
			int level = Integer.parseInt(warningLevel);
			switch (level) {
				case 1:
					warning = AlarmConstant.WARNING_LEVEL_TINY_DESC;
					break;
				case 2:
					warning = AlarmConstant.WARNING_LEVEL_LOW_DESC;
					break;
				case 3:
					warning = AlarmConstant.WARNING_LEVEL_MEDIUM_DESC;
					break;
				case 4:
					warning = AlarmConstant.WARNING_LEVEL_HIGH_DESC;
					break;
				case 5:
					warning = AlarmConstant.WARNING_LEVEL_ERROR_DESC;
					break;
				default:
					warning = AlarmConstant.WARNING_LEVEL_TINY_DESC;
					break;
			}
		}
		sb.append(warning).append(message).append("\n");
	}
	
	/**
	 * 得到当前线程的warning信息
	 */
	public static String getWaringMessage() {

		StringBuffer sb = AlarmConstant.WARNING.get();
		if (sb == null) {
			return "";
		}
		return sb.toString();
	}
}
