package com.grabber.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class DateUtils {
	private final static Log log = LogFactory.getLog(DateUtils.class);

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_FORMAT_YYYYMM = "yyyy-MM";
	public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
	public static final String DATE_FORMAT_YYMMDDHH = "yyMMddHH";
	public static final String DATE_FORMAT_YYMMDD = "yyMMdd";
	public static final String DATE_FORMAT_YYYYMMDD_HHMM = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String DATE_FORMAT_YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_YYYYMMDD_HHMMSS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String DATE_FORMAT_YYYY_YEAR_MM_MONTH_DD_DAY_HH_HOUR_MM_MINUTE = "yyyy年MM月dd日HH时mm分";
	public static final String DATE_FORMAT_YYYYMMDD_WEEK_HHMM = "yyyy-MM-dd(E)HH:mm";
	public static final String DATE_FORMAT_YY = "yy";
	public static final String DATE_FORMAT_HH = "HH";
	public static final String DATE_FORMAT_HHMM = "HH:mm";
	public static final int FMT_DATE_YYYY = 0;
	public static final int FMT_DATE_YYYYMMDD = 1;
	public static final int FMT_DATE_YYYYMMDD_HHMMSS = 2;
	public static final int FMT_DATE_HHMMSS = 3;
	public static final int FMT_DATE_HHMM = 4;
	public static final int FMT_DATE_SPECIAL = 5;

	public static Date getUTCDate() {
		Calendar c = Calendar.getInstance();

		TimeZone z = c.getTimeZone();
		int offset = z.getRawOffset();

		if (z.inDaylightTime(new Date())) {
			offset = offset + z.getDSTSavings();
		}

		int offsetHrs = offset / 1000 / 60 / 60;
		int offsetMins = offset / 1000 / 60 % 60;

		c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
		c.add(Calendar.MINUTE, (-offsetMins));

		return c.getTime();
	}

	/**
	 * Checks if a year value represents a real and valid year,from 1900 to
	 * 2099.
	 * 
	 * @param year
	 *            A year.
	 * @return true if valid;false otherwise.
	 */
	static boolean isYear(String year) {
		if (year.length() != 4)
			return false;
		if (!year.substring(0, 2).equals("19") && !year.substring(0, 2).equals("20"))
			return false;

		if (!isNumber(year))
			return false;

		int tmp = Integer.parseInt(year.substring(2, 4));
		return tmp >= 0 && tmp <= 99;
	}

	/**
	 * Checks if each character of the specified string is a number character.
	 * 
	 * @param origin
	 *            a string.
	 * @return true if all characters of 'origin' is number characters. false
	 *         otherwise.
	 */
	public static boolean isNumber(String origin) {
		String standard = "0123456789";
		int i = 0;

		for (; i < origin.length(); i++) {
			if (standard.indexOf(origin.charAt(i)) == -1) {
				break;
			}
		}
		return i == origin.length();
	}

	/**
	 * 获取下几年的日期
	 * 
	 * @param date
	 *            基准
	 * @param number
	 *            1明年 2后年 -1去年
	 * @return
	 */
	public static Date getNextYear(Date date, int number) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int td = c.get(Calendar.YEAR);
		c.set(Calendar.YEAR, td + number);
		return c.getTime();
	}

	/**
	 * 当月第一天 date参数为空，默认当前系统时间
	 *
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
		return cal.getTime();

	}

	/**
	 * 当月最后一天
	 *
	 * @return
	 */
	public static Date getLastDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		return cal.getTime();
	}

	/**
	 * @param dateStr
	 *            日期，格式 yyyyMMdd
	 * @param type
	 *            增加形式，按月增加和按天增加
	 * @return 返回字符串日期 yyyyMMdd
	 */
	public static String dateInc(String dateStr, String type) {
		if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(type)) {
			return dateStr;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cl = Calendar.getInstance();
		try {
			Date date = sdf.parse(dateStr);
			cl.setTime(date);
		} catch (ParseException e) {
			log.error("date format wrong:" + e.getMessage());
		}
		if (type.toUpperCase().equals("D")) {
			cl.set(Calendar.DAY_OF_MONTH, cl.get(Calendar.DAY_OF_MONTH) + 1);
		} else if (type.toUpperCase().equals("M")) {
			cl.set(Calendar.MONTH, cl.get(Calendar.MONTH) + 1);
		}
		dateStr = new SimpleDateFormat("yyyy-MM-dd").format(cl.getTime());
		return dateStr;
	}

	/**
	 * 日期减一
	 * 
	 * @param dateStr
	 * @param type
	 * @return
	 */
	public static String dateDec(String dateStr, String type) {
		if (StringUtils.isEmpty(dateStr) || StringUtils.isEmpty(type)) {
			return dateStr;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cl = Calendar.getInstance();
		try {
			Date date = sdf.parse(dateStr);
			cl.setTime(date);
		} catch (ParseException e) {
			log.error("date format wrong:" + e.getMessage());
		}
		if (type.toUpperCase().equals("D")) {
			cl.set(Calendar.DAY_OF_MONTH, cl.get(Calendar.DAY_OF_MONTH) - 1);
		} else if (type.toUpperCase().equals("M")) {
			cl.set(Calendar.MONTH, cl.get(Calendar.MONTH) - 1);
		}
		dateStr = new SimpleDateFormat("yyyy-MM-dd").format(cl.getTime());
		return dateStr;
	}

	/**
	 * 将格式为yyyyMMdd的日期转为yyyy-MM-dd格式
	 * 
	 * @param dateStr
	 *            需要转的日期
	 * @return 返回String格式数据
	 */
	public static String formatDateString(String dateStr) {
		if (StringUtils.isEmpty(dateStr) || dateStr.length() != 8) {
			return dateStr;
		}
		String temStr = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-"
				+ dateStr.substring(dateStr.length() - 2);
		return temStr;
	}

	/**
	 * 比较两个日期大小
	 * 
	 * @param dateStr1
	 * @param dateStr2
	 * @return 布尔值
	 */
	public static boolean compareTwoDates(String dateStr1, String dateStr2) {
		if (StringUtils.isEmpty(dateStr1) || StringUtils.isEmpty(dateStr2) || dateStr1.length() != 8
				|| dateStr2.length() != 8) {
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		try {
			Date date1 = sdf.parse(dateStr1);
			Date date2 = sdf.parse(dateStr2);
			if (date1.getTime() > date2.getTime()) {
				return true;
			}
		} catch (ParseException e) {
			log.error("date format wrong:" + e.getMessage());
		}
		return false;
	}

	/**
	 * 获得日期的间隔天数
	 * 
	 * @param startDate
	 *            格式yyyyMMdd
	 * @param endDate
	 *            格式yyyyMMdd
	 * @return 返回间隔天数
	 */
	public static int getDays(String startDate, String endDate) {
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate) || startDate.length() != 8
				|| endDate.length() != 8) {
			return -1;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = sdf.parse(startDate);
			date2 = sdf.parse(endDate);
			return getDays(date1, date2);
		} catch (ParseException e) {
			log.error("date format wrong:" + e.getMessage());
		}
		return -1;
	}

	/**
	 * 获得日期的间隔天数
	 * 
	 * @param startDate
	 *            格式Date
	 * @param endDate
	 *            格式Date
	 * @return 返回间隔天数
	 */
	public static int getDays(Date startDate, Date endDate) {
		Long time = endDate.getTime() - startDate.getTime();
		if (time >= 0) {
			return (int) ((((time / 1000) / 60) / 60) / 24);
		}
		return -1;
	}

	/**
	 * 获得多少天前
	 * 
	 * @return 返回间隔天数
	 */
	public static int getDays(Date date) {
		return getDays(date, new Date());
	}

	/**
	 * 获得多少天前
	 * 
	 * @return 返回间隔天数
	 */
	public static int getDays(Timestamp date) {
		return getDays(date, getCurrentTime());
	}

	public static boolean isyyyyMMddFromat(String dateStr) {
		boolean flag = true;
		if (StringUtils.isEmpty(dateStr) || dateStr.length() != 8) {
			flag = false;
			return flag;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Date date = sdf.parse(dateStr);
		} catch (ParseException e) {
			flag = false;
		}
		return flag;
	}

	public static int getDays(Timestamp start, Timestamp end) {
		return getDays(format(start, "yyyyMMdd"), format(end, "yyyyMMdd"));
	}

	/**
	 * 加、减分钟数。
	 * 
	 * @param date
	 *            基准日期。
	 * @param addedDays
	 *            如果>0，则增加分钟数；否则，会减分钟数。
	 * @return 计算后的日期。
	 */
	public static Date addMinutes(Date date, int addedMinutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, addedMinutes);
		return cal.getTime();
	}

	/**
	 * 加、减天数。
	 * 
	 * @param date
	 *            基准日期。
	 * @param addedDays
	 *            如果>0，则增加天数；否则，会减天数。
	 * @return 计算后的日期。
	 */
	public static Date addDays(Date date, int addedDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, addedDays);
		return cal.getTime();
	}

	/**
	 * 加、减月数。
	 * 
	 * @param date
	 *            基准日期。
	 * @param addedMonths
	 *            如果>0，则增加月数；否则，会减月数。
	 * @return 计算后的日期。
	 */
	public static Date addMonths(Date date, int addedMonths) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, addedMonths);
		return c.getTime();
	}

	/**
	 * 根据生日计算年龄。
	 * 
	 * @param birth
	 *            字符串格式的生日。
	 * @param format
	 *            日期格式。
	 * @return 年龄。
	 * @throws ParseException
	 */
	public static int calculateAge(String birth, String format) throws ParseException {

		Date birthday = getDate(birth, format);
		return calculateAge(birthday);
	}

	/**
	 * 根据生日计算年龄。
	 * 
	 * @param birth
	 *            生日。
	 * @return 年龄。
	 */
	public static int calculateAge(Date birth) {

		Calendar c = Calendar.getInstance();
		Calendar b = Calendar.getInstance();
		b.setTime(birth);

		int thisYear = c.get(Calendar.YEAR);
		int thisMonth = c.get(Calendar.MONTH);
		int thisDate = c.get(Calendar.DATE);

		int birthYear = b.get(Calendar.YEAR);
		int birthMonth = b.get(Calendar.MONTH);
		int birthDate = b.get(Calendar.DAY_OF_MONTH);

		if (thisMonth < birthMonth) // 今年还没过生日
		{
			return thisYear - birthYear - 1;
		} else if (thisMonth > birthMonth) {
			return thisYear - birthYear;
		} else {
			if (thisDate < birthDate) // 在生日月份没到具体日子
			{
				return thisYear - birthYear - 1;
			} else {
				return thisYear - birthYear;
			}
		}
	}

	/**
	 * 时间比较。
	 * 
	 * @param date1
	 *            日期1
	 * @param date2
	 *            日期2
	 * @return 0 : 相同；<br/>
	 *         小于0的值：date1小于date2.<br/>
	 *         大于0的值：date1大于date2. <br/>
	 */
	public static int compareDate(Date date1, Date date2) {

		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		return c1.compareTo(c2);

	}

	/**
	 * 按指定格式把Date对象格化为字符串。
	 * 
	 * @param d
	 *            待格式化的日期。
	 * @param format
	 *            日期格式。例如：“yyyy-MM-dd”。参见： <a href=
	 *            "http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html"
	 *            >SimpleDateFormat</a>
	 * @return 格式化后的日期字符串，如果传入的日期对象为NULL，返回null。
	 */
	public static String format(Date d, String format) {
		return d == null ? null : new SimpleDateFormat(format).format(d);
	}

	public static String format(Date d) {
		return d == null ? null : new SimpleDateFormat("yyyy-MM-dd").format(d);
	}

	/**
	 * 按指定格式把Timestamp对象格化为字符串。
	 * 
	 * @param time
	 *            带格式化的Timestamp对象。
	 * @param format
	 *            日期格式。例如：“yyyy-MM-dd”。参见： <a href=
	 *            "http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html"
	 *            >SimpleDateFormat</a>
	 * @return 格式化后的日期字符串，如果传入的日期对象为NULL，返回空字符串。
	 */
	public static String format(Timestamp time, String format) {
		if (StringUtils.isEmpty(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		return time == null ? null : new SimpleDateFormat(format).format(time);
	}

	/**
	 * 将字符串格式化为日期对象
	 * 
	 * @param date
	 * @param format
	 * @return 如果date为空或格式不标准，返回NULL，否则返回对应的日期对象
	 */
	public static Date formatToDate(String date, String format) {
		try {
			if (null == date || "".equalsIgnoreCase(date)) {
				return null;
			}

			SimpleDateFormat sorceFmt = new SimpleDateFormat(format);
			return new Date(sorceFmt.parse(date).getTime());
		} catch (ParseException e) {
			LoggerUtil.debugLog.info("invalid date :" + date);
			return null;
		}
	}

	/**
	 * 将字符串格式化为日期对象
	 * 
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Timestamp formatToTimestamp(String dateStr, String format) {
		try {
			SimpleDateFormat sorceFmt = new SimpleDateFormat(format);
			return new Timestamp(sorceFmt.parse(dateStr).getTime()); // 一天的时间24*3600*1000
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 判断date 是否在start 和 end之间
	 * 
	 * @param date
	 *            待比较的日期对象。
	 * @param start
	 *            日期区间的开始处。
	 * @param end
	 *            日期区间的结束处。
	 * @return true：date在[start,end]区间内；false：在区间外。
	 */
	public static boolean isBetween(Date date, Date start, Date end) {

		if (date == null || start == null || end == null)
			return false;

		return date.after(start) && date.before(end);
	}

	/**
	 * 判断是否是当月最后一天。
	 * 
	 * @param date
	 *            待判断的日期。
	 * @return true：是当月最后一天；false：不是。
	 */
	public static boolean isLastDayOfMonth(Date date) {
		if (date == null) {
			return false;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return day == last;
	}

	/**
	 * 判断传入日期是否是今天
	 * 
	 * @param date
	 * @return true:是今天。< br/>
	 *         false:不是今天。
	 */
	public static boolean isToday(Date date) {
		if (date == null) {
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return sdf.format(Calendar.getInstance().getTime()).equals(sdf.format(date.getTime()));

	}

	/**
	 * 根据年、月、日创建日期对象。
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @return Date对象。
	 */
	public static Date getDate(int year, int month, int day) {

		return getDate(year, month, day, 0, 0, 0);
	}

	/**
	 * 根据年、月、日、小时、分钟、秒创建 Date对象。
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @param hour
	 *            小时
	 * @param minute
	 *            分钟
	 * @param second
	 *            秒
	 * @return Date对象。
	 */
	public static Date getDate(int year, int month, int day, int hour, int minute, int second) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, hour, minute, second);
		return calendar.getTime();
	}

	/**
	 * 将字符串格式化为日期对象
	 * 
	 * @param date
	 * @param format
	 *            日期格式，例如“yyyy-MM-dd”。参见： <a href=
	 *            "http://docs.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html"
	 *            >SimpleDateFormat</a>
	 * @return 如果date为空或格式不正确，返回NULL，否则返回对应的日期对象
	 * @throws ParseException
	 */
	public static Date getDate(String date, String format) throws ParseException {

		SimpleDateFormat sorceFmt = new SimpleDateFormat(format);
		return new Date(sorceFmt.parse(date).getTime());
	}

	/**
	 * 将字符串格式化为Timestamp对象。
	 * 
	 * @param str
	 *            待转换的字符串。
	 * @param format
	 *            格式
	 * @return 转换或的Timestamp对象。
	 * @throws ParseException
	 */
	public static Timestamp getTimestamp(String str, String format) throws ParseException {

		SimpleDateFormat sorceFmt = new SimpleDateFormat(format);
		return new Timestamp(sorceFmt.parse(str).getTime()); // 一天的时间24*3600*1000
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static Timestamp getCurrentTime() {
		return new Timestamp(Calendar.getInstance().getTimeInMillis());
	}

	public static Date getCurrentDay() {
		String date = format(new Date(), "yyyy-MM-dd");
		return formatToDate(date, "yyyy-MM-dd");
	}

	public static String getDateOrTimeContent(Timestamp time) {
		if (hasTime(time)) {
			return format(time, "yyyy-MM-dd HH:mm");
		}
		return format(time, "yyyy-MM-dd");
	}

	public static String getDateOrTimeContentWithoutYears(Timestamp time) {
		if (hasTime(time)) {
			return format(time, "MM-dd HH:mm");
		}
		return format(time, "MM-dd");
	}

	public static boolean hasTime(Timestamp time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time.getTime());
		if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0
				&& calendar.get(Calendar.SECOND) == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 获得begin-end的天数值。
	 * 
	 * @param begin
	 *            开始的Date对象。
	 * @param end
	 *            结束的Date对象。
	 * @return begin-end的天数值。
	 */
	public static int getDiffDays(Date begin, Date end) {

		return (int) (getDiffMinutes(begin, end) / 1440);
	}

	/**
	 * 获得begin-end的分钟值。
	 * 
	 * @param begin
	 *            开始的Date对象。
	 * @param end
	 *            结束的Date对象。
	 * @return begin-end的分钟值
	 */
	public static long getDiffMinutes(Date begin, Date end) {

		return getDiffMsecs(begin, end) / (60 * 1000);
	}

	/**
	 * 获得begin-end的毫秒值。
	 * 
	 * @param begin
	 *            开始的Date对象。
	 * @param end
	 *            结束的Date对象。
	 * @return begin-end的毫秒值。
	 */
	public static long getDiffMsecs(Date begin, Date end) {

		return end.getTime() - begin.getTime();
	}

	/**
	 * 取Date对象的局部信息“年”。
	 * 
	 * @param date
	 *            Date对象。
	 * @return 年。
	 */
	public static int getYear(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * 取Date对象的局部信息“月”。
	 * 
	 * @param date
	 *            Date对象。
	 * @return 月
	 */
	public static int getMonth(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 取Date对象的局部信息“天”。
	 * 
	 * @param date
	 *            Date对象。
	 * @return 天.
	 */
	public static int getDay(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 得到Date对象的星期。
	 * 
	 * @param date
	 *            Date对象。
	 * @return 1:周日、2：周一....、7:周六。
	 */
	public static int getWeek(Date date) {

		Calendar current = Calendar.getInstance();
		current.setTime(date);
		return current.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 得到昨天。
	 * 
	 * @return 昨天的同一时间点。
	 */
	public static Date getYesterday() {
		return addDays(new Date(), -1);
	}

	/**
	 * 得到昨天。
	 * 
	 * @return 昨天的同一时间点。
	 */
	public static Timestamp getYesterdayTimestamp() {
		return new Timestamp(addDays(new Date(), -1).getTime());
	}

	// Convert Unix timestamp to normal date style
	public static String timeStamp2Date(String timestampString) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new java.util.Date(timestamp));
		return date;
	}

	/**
	 * 获取当前时间的前n秒
	 * 
	 * @param num
	 * @return
	 */
	public static Date getTimeBeforeSecond(Date date, int num) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND, num);
		date = c.getTime();
		return date;
	}

	/**
	 * 获得指定时间间隔的timestamp
	 *
	 * @param ts
	 * @param seconds
	 * @return
	 */
	public static Timestamp getIntervalTimestampBySeconds(Timestamp ts, long seconds) {
		return new Timestamp(ts.getTime() + seconds * 1000L);
	}

	public static Timestamp getIntervalTimestampByDays(Timestamp ts, long days) {
		return getIntervalTimestampBySeconds(ts, days * 86400L);
	}

	public static Timestamp getBeginOfToday() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = form.format(cal.getTime()) + " 00:00:00";
		Date date = null;
		try {
			date = form.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
		return new Timestamp(date.getTime());
	}

	public static Timestamp getBeginOfOneHour(Timestamp times) {
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH");
		String dateStr = form.format(times.getTime()) + ":00:00";
		Date date = null;
		try {
			date = form.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
		return new Timestamp(date.getTime());
	}

	public static Timestamp getBeginOfOneDay(Timestamp times) {
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = form.format(times.getTime()) + " 00:00:00";
		Date date = null;
		try {
			date = form.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
		return new Timestamp(date.getTime());
	}
}
