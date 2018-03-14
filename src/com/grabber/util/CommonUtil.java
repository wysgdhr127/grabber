package com.grabber.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class CommonUtil {
	
	private static Map<String, Integer> amountMap = new LinkedHashMap<String, Integer>();
	
	static {
		amountMap.put("万", 10000);
		amountMap.put("千", 1000);
		amountMap.put("百", 100);
		amountMap.put("十", 10);
	}

	public static Double parseAmount(String content) {
		content = content.replaceAll(",", "");
		String amountContent = runRegex("(\\d+\\.?\\d*)?", content, "1").trim();
		double amount = Double.parseDouble(amountContent);
		for (Entry<String, Integer> entry : amountMap.entrySet()) {
			if (content.contains(entry.getKey())) {
				amount = amount * entry.getValue();
			}
		}
		return amount;
	}

	public static String runRegex(String regex, String content, String regexIndex) {
		if (StringUtils.isNotBlank(regex) && StringUtils.isNotBlank(regexIndex)) {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(content);
			int rIndex = 0;
			rIndex = Integer.parseInt(regexIndex);
			if (m.find()) {
				return m.group(rIndex);
			}
		}
		return content;
	}
	
}
