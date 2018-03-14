package com.grabber.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import com.netease.common.util.LogUtil;

/**
 * 参数校验工具类。主要校验request中的参数是否正确，包括：<br />
 * 1.校验字符串是否为null或trim后的长度为0<br />
 * 2.校验是否是数字、整数、正整数、浮点数、正浮点数、long型。<br />
 * 3.校验Email、URL、邮件编码、手机号码、电话号码、身份证号、护照号、机动车行驶证、驾驶证、生日、银行卡号等。<br />
 * 4.校验字符串里的字母是否递增（连续）。<br />
 * 5.校验闰年<br />
 * 6.校验字符串长度是否位于min和max之间。
 * 
 * @author 开发支持中心
 *
 */
public class ValidateUtil {
	/** 身份证号码的正则表达式 */
	private static final String ID_NO_REGEXP = "^((\\d{17}|\\d{14})(\\d|x|X))$";

	/** 身份证前2位。 */
	private static final String[] IDNO_CITY = new String[] { "11", "12", "13", "14", "15", "21", "22", "23", "31",
			"32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61",
			"62", "63", "64", "65", "71", "81", "82", "91" };

	/** 18位身份证号的最后一位 */
	private static final int[] IDNO_IWEIGHT = new int[] { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };

	private static final String[] IDNO_CCHECK = new String[] { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };

	/** email的正则表达式 */
	private static final String EMAIL_REGEXP = "^([\\w-\\.]+)@[\\w-.]+(\\.?[a-zA-Z]{2,4}$)";

	/** email的正则表达式 */
	private static final String CONTAINS_EMAIL_REGEXP = "@[\\w-.]+(\\.?[a-zA-Z]{2,4})";

	/** URL的正则表达式 */
	private static final String URL_REGEXP = "^(http|https)://.*";

	/** 邮政编码的正则表达式 */
	private static final String POSTCODE_REGEXP = "[0-9]{6}";

	/** 手机号码的正则表达式 */
	private static final String PHONE_NO_REGEXP = "[1]{1}[2|3|4|5|7|8]{1}[0-9]{9}";

	/** 国外手机号码的正则表达式 */
	private static final String OVERSEA_PHONE_NO_REGEXP = "[0]{2}[0-9]{6,20}";

	/** 固定电话的正则表达式 */
	private static final String TOLEPHONE_NO_REGEXP = "^([\\d]{3}-)?([\\d]{3,4}-)?[\\d]{7,8}(-[\\d]{1,4})?$";

	/** 汉语名字的正则表达式 */
	private static final String CHINESE_NAME_REGEXP = "^[\u0391-\uFFE5]{2,10}$";

	/** 英文单词的正则表达式 */
	private static final String ENGLISH_WORD_REGEXP = "[a-z\\sA-Z]+";

	/** 护照号码的正则表达式 */
	private static final String PASSPORT_REGEXP = "^[Pp]([0-9]{7})$|^[Gg]([0-9]{8})$";

	/** 汽车牌照的正则表达式 */
	private static final String VEHICLE_LICENCE_NO_REGEXP = "^[\u4e00-\u9fa5]{1}[A-Za-z0-9]{6}$";

	/** 汽车行驶证照的正则表达式 */
	private static final String DRIVING_LICENSE_NO_REGEXP = "^[^\u0391-\uFFE5]{5,64}$";

	/** 银行卡号的正则表达式 */
	private static final String BANK_CARD_NO_REGEXP = "^[\\d]{13,20}$";

	/** 金额字符串正则表达式 */
	private static final String MONEY_REGEXP = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";

	/**
	 * 判断str与正则表达式regexp是否匹配。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @param regexp
	 *            正在表达式。
	 * @return true：匹配。<br/>
	 *         false:不匹配。<br/>
	 *         如果str为null或trim后的长度为0，返回false.
	 */
	private static boolean match(String str, String regexp) {
		if (isEmpty(str)) {
			return false;
		}
		return Pattern.matches(regexp, str);
	}

	/**
	 * 判断字符串是否为null或trim()后的长度为0.
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：为null或trim()后的长度为0。 false：与true相反。
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 * 判断传入的参数是否是Email。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是Email； false：不是Email。
	 */
	public static boolean isEmail(String str) {
		return match(str, EMAIL_REGEXP);
	}

	public static boolean containsEmail(String str) {
		if (StringUtils.isBlank(str)) {
			return false;
		}
		String[] segs = str.split("@");
		try {
			for (int i = 1; i < segs.length; i++) {
				boolean hasDot = false;
				for (int j = 0; j < segs[i].length(); j++) {
					if (segs[i].charAt(j) > 'z') {
						break;
					}
					if (hasDot) {
						return true;
					}
					if (segs[i].charAt(j) == '.') {
						hasDot = true;
					}
				}
			}
		} catch (Exception e) {
			LoggerUtil.debugLog.error("Check contains email", e);
		}
		return false;
	}

	/**
	 * 判断传入的字符串内容是否是int类型的数据。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是int； false：不是int。
	 */
	public static boolean isInt(String str) {
		if (isEmpty(str))
			return false;

		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断传入的字符串内容是否是正整数。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是正整数； false：不是正整数。
	 */
	public static boolean isPositiveInt(String str) {
		if (isEmpty(str))
			return false;

		try {
			int tmp = Integer.valueOf(str);
			return tmp >= 0;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断传入的字符串里的内容是否是浮点数（float）。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是float; false:不是float.
	 */
	public static boolean isFloat(String str) {
		if (isEmpty(str))
			return false;

		try {
			Float.parseFloat(str);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * 判断传入的字符串里的内容是否是正浮点数（positive float）。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是正float; false:不是正float.
	 */
	public static boolean isPositiveFloat(String str) {
		if (isEmpty(str))
			return false;

		try {
			float num = Float.parseFloat(str);
			return (num >= 0);
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * 校验long类型的数据。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是；false：不是。
	 */
	public static boolean isLong(String str) {
		try {
			new Long(str);
		} catch (Exception e) {

			return false;
		}

		return true;
	}

	/**
	 * 判断字符串里的字符是否都是数字。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是数字；fasle：不是。
	 */
	public static boolean isNumber(String str) {
		char[] tmp = null;
		boolean result = true;

		if (isEmpty(str))
			return false;

		tmp = str.toCharArray();
		int len = tmp.length;

		for (int i = 0; i < len; i++) {
			if (!Character.isDigit(tmp[i])) {
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * 校验URL。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是邮件编码。false：不是。
	 */
	public static boolean isUrl(String str) {
		return match(str, URL_REGEXP);
	}

	/**
	 * 校验host
	 * 
	 * @param url
	 * @param host
	 * @return
	 */
	public static boolean isUrlValidHost(String url, String host) {
		try {
			if (!isUrl(url) || StringUtils.isBlank(host)) {
				return false;
			}
			url = url.replace("http://", "").replace("https://", "");
			String urlHost = url.substring(0, url.indexOf("/"));
			return urlHost.endsWith(host);
		} catch (Exception e) {
			LoggerUtil.debugLog.error("[isUrlValidHost]", e);
		}
		return false;
	}

	/**
	 * 校验邮件编码。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是邮件编码；false：不是。
	 */
	public static boolean isPostCode(String str) {
		return match(str, POSTCODE_REGEXP);
	}

	/**
	 * 校验手机号码。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是手机号码；false:不是。
	 */
	public static boolean isPhoneNo(String str) {
		return match(str, PHONE_NO_REGEXP);
	}

	/**
	 * 校验海外手机号码。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是手机号码；false:不是。
	 */
	public static boolean isOverseaPhoneNo(String str) {
		return match(str, PHONE_NO_REGEXP) || match(str, OVERSEA_PHONE_NO_REGEXP);
	}

	/**
	 * 校验固定电话号码。格式为“3位国家代码-3或4位区号-7或8位电话号码-1到4位分机号码",
	 * （注：中间用分隔符“-”隔开），除了7或8位电话号码，其余几处都可为空。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是电话号码；false:不是。
	 */
	public static boolean isTelephoneNo(String str) {
		return match(str, TOLEPHONE_NO_REGEXP);
	}

	/**
	 * 校验身份证号码。
	 * 
	 * @param identityNo
	 *            待校验的身份证号码。
	 * @return true:是身份证号。 false:不是身份证号。
	 */
	public static boolean isIdentityNo(String identityNo) {

		if (!match(identityNo, ID_NO_REGEXP)) {
			return false;
		}

		int length = identityNo.length();
		if (length != 15 && length != 18) {
			return false;
		}

		// 校验身份证号码的前2位。
		String city = identityNo.substring(0, 2);
		if (StringUtils.indexOfAny(city, IDNO_CITY) < 0) {
			return false;
		}

		if (length == 18) {
			// 校验身份证号码的生日部分。
			String birth = identityNo.substring(6, 14);
			try {
				DateUtils.getDate(birth, "yyyyMMdd");
			} catch (ParseException e) {
				return false;
			}

			int total = 0;
			for (int i = 0; i < 17; i++) {
				total += Integer.valueOf(identityNo.substring(i, i + 1)) * IDNO_IWEIGHT[i];
			}
			int mo = total % 11;
			String lastOne = IDNO_CCHECK[mo];
			return identityNo.substring(17).equalsIgnoreCase(lastOne);
		} else {
			// 校验身份证号码的生日部分。
			String birth = identityNo.substring(6, 12);
			try {
				DateUtils.getDate(birth, "yyMMdd");
				return true;
			} catch (ParseException e) {
				return false;
			}
		}

	}

	/**
	 * 校验护照号码。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true：是；false：不是。
	 */
	public static boolean isPassportNo(String str) {
		return match(str, PASSPORT_REGEXP);
	}

	/**
	 * 校验中文姓名。
	 * 
	 * @param name
	 *            待校验的字符串。
	 * @return true : 是中文姓名；false：不是。
	 */
	public static boolean isChineseName(String name) {
		return match(name, CHINESE_NAME_REGEXP);
	}

	/**
	 * 校验英文单词。
	 * 
	 * @param name
	 *            待校验的字符串。
	 * @return true : 是中文姓名；false：不是。
	 */
	public static boolean isEnglishWord(String name) {
		return match(name, ENGLISH_WORD_REGEXP);
	}

	/**
	 * 校验闰年。
	 * 
	 * @param year
	 *            待校验的年。
	 * @return true：是；false：不是。
	 */
	public static boolean isLeapYear(String year) {
		if (isEmpty(year)) {
			return false;
		}
		int yearint = Integer.parseInt(year);
		return ((yearint % 4 == 0 && yearint % 100 != 0) || yearint % 400 == 0);
	}

	/**
	 * 校验车牌号是否合法.
	 * 
	 * @param str
	 *            待检验的车牌号。
	 * @return true:是；false：不是。
	 */
	public static boolean isVehicleLicenseNo(String str) {
		return match(str, VEHICLE_LICENCE_NO_REGEXP);

	}

	/**
	 * 校验车辆行驶证型号是否合法
	 * 
	 * @param str
	 *            待校验的行驶证号。
	 * @return true：是；false：不是。
	 */
	public static boolean isDrivingLicenseNo(String str) {
		return match(str, DRIVING_LICENSE_NO_REGEXP);
	}

	/**
	 * 校验字符串里的字符是否都相同。
	 * 
	 * @param str
	 *            待校验的字符串。
	 * @return true:都相同；false：不相同。
	 */
	public static boolean isSameCharacter(String str) {
		Map<Character, String> map = new HashMap<Character, String>();
		int messageLength = str.length();

		for (int i = 0; i < messageLength; i++) {
			char a = str.charAt(i);
			map.put(a, "ok");
		}
		return (map.size() <= 1);
	}

	/**
	 * 校验字符串里的字母是不是连续的，可以递增有序，也可以递减有序。例如：“123456”、“abcdef”、“654321”等。
	 * 字符串由字母和数字组成。
	 * 
	 * @param str
	 *            字符串
	 * @return true：递增。false：不递增。
	 */
	public static boolean isContinuous(String str) {
		if (isEmpty(str))
			return false;

		LogUtil.debug(LogUtil.LOG_BASIC_FRAMEWORK, str);

		if (Pattern.matches("^[0-9]*$", str) || Pattern.matches("^[A-Za-z]+$", str)) {
			for (int i = 0; i <= str.length() - 2; i++) {

				boolean b = isContinuous(str, i);
				if (b == false) {
					return false;
				}
			}
			return true;
		}
		return false;

	}

	/**
	 * 判断index和index+1是否是连续的。
	 * 
	 * @param message
	 *            字符串
	 * @param index
	 *            待校验的索引位置。
	 * @return true：递增。false：不递增。
	 */
	private static boolean isContinuous(String str, int index) {
		char c1 = str.charAt(index);
		char c2 = str.charAt(++index);
		return ((c1 + 1) == c2 || c1 == (c2 + 1));
	}

	/**
	 * 校验生日。“<=今天”的都是生日，“>今天”不是生日。
	 * 
	 * @param date
	 *            待校验的String格式的生日。
	 * @param format
	 *            参数date的格式。
	 * @return true:是生日；false:不是生日。
	 */
	public static boolean isBirthday(String date, String format) {
		try {
			Date birthday = DateUtils.getDate(date, format);
			return isBirthday(birthday);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 校验生日。“<=今天”的都是生日，“>今天”不是生日。
	 * 
	 * @param strY
	 *            年
	 * @param strM
	 *            月
	 * @param strD
	 *            日
	 * @return true:是生日；false:不是生日。
	 */
	public static boolean isBirthday(String strY, String strM, String strD) {
		int iY = Integer.parseInt(strY);
		int iM = Integer.parseInt(strM);
		int iD = Integer.parseInt(strD);
		Date birthday = DateUtils.getDate(iY, iM, iD);

		return isBirthday(birthday);
	}

	/**
	 * 校验生日。“<=今天”的都是生日，“>今天”不是生日。
	 * 
	 * @param date
	 *            待校验的日期。
	 * @return true:是生日；false:不是生日。
	 */
	public static boolean isBirthday(Date date) {
		if (date == null) {
			return false;
		}

		Calendar birth = Calendar.getInstance();
		birth.setTime(date);

		Calendar today = Calendar.getInstance();

		return today.after(birth);

	}

	/**
	 * 校验银行卡号（13-20位数字）。
	 * 
	 * @param backCardNo
	 *            银行卡号
	 * @return true:是银行卡号； false:不是银行卡号。
	 */
	public static boolean isBankCardNo(String backCardNo) {
		return match(backCardNo, BANK_CARD_NO_REGEXP);
	}

	/**
	 * 验证用户名是否符合RFC标准。RFC规定用户名中不能含有连续的“.”字符，该方法未把所有限制都列出，
	 * 但针对我们规定的用户名字符集，限制连续“.”这种情况就可以了。
	 * RFC中说明中有保留用户名，也应该被限制，如“Postmaster”,"noreply"，不区分大小写。
	 * 
	 * @param ssn
	 *            待验证的用户名。
	 * @return true：符合RFC标准；false:不符合。
	 */
	public static boolean isRfcFormat(String ssn) {

		if (isEmpty(ssn) || ssn.indexOf("..") != -1 || "Postmaster".equalsIgnoreCase(ssn)
				|| "noreply".equalsIgnoreCase(ssn)) {
			return false;
		}
		return true;
	}

	/**
	 * 验证 ( str's length >= min and str's length<= max ) ，一个汉字等于两个字符.
	 * 
	 * @param str
	 * @param minLength
	 *            长度下限
	 * @param maxLength
	 *            长度上线
	 * @return true：在区间内。 false：不在区间内。
	 */
	public static boolean checkStrLength(String str, int minLength, int maxLength) {
		byte[] b = str.getBytes();

		return (b.length >= minLength && b.length <= maxLength);

	}

	/**
	 * 校验金额合法性，默认校验格式为 xxx.xx
	 * 
	 * @param money
	 *            金额
	 * @return true:合法金额 false:非法金额
	 */
	public static boolean isMoney(String money) {
		return match(money, MONEY_REGEXP);
	}

	/**
	 * IP校验
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean verifyIp(String ip) {
		boolean ret = true;
		int tmp;

		String[] str = ip.split("\\.", -1);
		if (str.length != 4)
			return false;
		try {
			for (int i = 0; i < str.length; i++) {
				tmp = Integer.parseInt(str[i]);
				if (tmp < 0 || tmp > 255) {
					ret = false;
					break;
				}
			}
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}

	public static void main(String[] args) {
		String s = "1、985或211院校研究生；2、17年毕业生；3、要求全职实习。邮箱：。4.请投递";
		System.out.println(containsEmail(s));
	}
}
