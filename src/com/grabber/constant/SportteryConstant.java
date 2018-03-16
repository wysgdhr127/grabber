package com.grabber.constant;

import java.util.ArrayList;
import java.util.List;

public class SportteryConstant {

	protected static List<String> list = new ArrayList<String>();

	// 中国竞彩网 竞彩信息 查询
	public static String SPORTTERY_INFO_URL = "http://i.sporttery.cn/odds_calculator/get_odds?";

	public static final String SPORTTERY_REQUEST_PART1 = "i_format=json&i_callback=getData";
	public static final String SPORTTERY_REQUEST_PART2 = "poolcode[]=";

	// 足彩玩法
	public static final String SPF_POOL_CODE = "had";
	public static final String BF_POOL_CODE = "crs";
	public static final String BQC_POOL_CODE = "hafu";
	public static final String ZJQ_POOL_CODE = "ttg";
	// public static final String ZQHH_POOL_CODE = "ZQHH";
	public static final String RQSPF_POOL_CODE = "hhad";

	public static final String SPF_KEY = "h, a, d";
	public static final String BF_KEY = "0100, 0200, 0201, 0300, 0301, 0302, 0400, 0401, 0402, 0500, 0501, 0502, -1-h, 0000, 0101, 0202, 0303, -1-d, 0001, 0002, 0102, 0003, 0103, 0203, 0004, 0104, 0204, 0005, 0105, 0205, -1-a";
	public static final String BQC_KEY = "hh, hd, ha, dh, dd, da, ah, ad, aa";
	public static final String ZJQ_KEY = "s0, s1, s2, s3, s4, s5, s6, s7";

	static {
		initPoolCodeList();
	}

	public static void initPoolCodeList() {
		list.add(SPF_POOL_CODE);
		list.add(BF_POOL_CODE);
		list.add(BQC_POOL_CODE);
		list.add(ZJQ_POOL_CODE);
		// list.add(ZQHH_POOL_CODE);
		list.add(RQSPF_POOL_CODE);
	}

	public static List<String> getPoolCodeList() {
		return list;
	}

}
