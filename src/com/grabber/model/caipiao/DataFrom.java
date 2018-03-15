package com.grabber.model.caipiao;

import java.util.LinkedHashMap;
import java.util.Map;

public class DataFrom {
	
	protected static Map<String, String> codeDescriptionMap = new LinkedHashMap<String, String>();

	public static final String FIFEMILLION = "caipiao_500";
	public static final String FIFEMILLION_DESCRIPTION = "500万彩票网";
	public static final String FIFEMILLION_FROM = "500wan";
	public static final String NETEASE = "caipiao";
	public static final String NETEASE_DESCRIPTION = "网易彩票";
	public static final String NETEASE_FROM = "netease";
	public static final String SPORTTERY = "sporttery";
	public static final String SPORTTERY_DESCRIPTION = "中国竞彩网";
	public static final String SPORTTERY_FROM = "sporttery";
	
	
	static {
		initCodeDescriptionMap();
	}

	public static void initCodeDescriptionMap() {
		codeDescriptionMap.put(FIFEMILLION, FIFEMILLION_FROM);
		codeDescriptionMap.put(NETEASE, NETEASE_FROM);
		codeDescriptionMap.put(SPORTTERY, SPORTTERY_FROM);
	}

	public static String getDescription(String key) {
		return codeDescriptionMap.get(key);
	}

	public static Map<String, String> getCodeDescriptionMap() {
		return codeDescriptionMap;
	}

}
