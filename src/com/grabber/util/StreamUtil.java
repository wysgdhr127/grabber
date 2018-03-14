package com.grabber.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author WY
 */
public class StreamUtil {
	
	/**
	 * 工具方法 InputSteam to String
	 */
	public static String inputStreamTOString(InputStream is, String encoding) {

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader reader = null;
				if (encoding == null) {
					reader = new BufferedReader(new InputStreamReader(is));
				} else {
					reader = new BufferedReader(new InputStreamReader(is, encoding));
				}
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} catch (Exception e) {
				LoggerUtil.errorLog.equals(e);
			} finally {
				try {
					is.close();
				} catch (Exception e) {
					LoggerUtil.errorLog.equals(e);
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * 工具方法 String to InputSteam
	 */
	public static InputStream stringTOInputStream(String in, String encoding) {

		ByteArrayInputStream is = null;
		try {
			is = new ByteArrayInputStream(in.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			LoggerUtil.errorLog.equals(e);
		}
		return is;
	}
}
