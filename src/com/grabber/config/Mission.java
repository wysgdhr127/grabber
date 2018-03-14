/**
 * 
 */
package com.grabber.config;

import com.grabber.model.BaseModel;
import com.grabber.strategy.HandleStrategy;
import com.grabber.util.CapturerUtil;

/**
 * @author WY 14 Mar 2018 15:12:52
 */
public class Mission extends BaseModel {

	// Mission
	private String missionName;// 此mission的名称
	private String missionStatus;// 此mission的状态，0无效，1有效
	private String missionPriority;// 优先级，从1-10，优先级越高越先执行

	// Thread
	private int crawlerThreadNum;// 爬取线程数
	private int parserThreadNum;// 解析线程数
	private int handleThreadNum;// 后处理线程数
	private int handleUnitMaxSize;// 后处理单元中元素的最大尺寸

	// ErrorMsg
	private String mailTo; // 报警邮件发送人
	private int maxMail; // 发送的邮件上限

	// Entrance
	private String domain;// 基础网址
	private String entranceName;// 入口文件名

	// Crawler
	private int restLevel;// 报警级别
	private String encode;// 页面编码
	private boolean gzip;
	private boolean needProxy; // 是否需要代理
	private String httpMethod;// post || get
	private boolean diskCache;// 是否可以使用硬盘缓存
	private long diskCacheOutTimeSecond;// 硬盘缓存失效时间（秒）
	private boolean pageCheckSwitch;// 页面校验开关
	private String pageCheckRegex;// 页面校验正则表达式

	// Parser
	private String modelName;// model配置文件名（不包含后缀）
	private String strategyName;// 后处理策略类全称，需要继承HandleStrategy
	private String crawlConditionName;// 爬虫条件配置文件名（不包含后缀）
	private HandleStrategy handleStrategy; // 后处理程序

	// Capturer
	private CapturerUtil capturer; // 网页获取器

	public String getCrawlConditionName() {

		return crawlConditionName;
	}

	public void setCrawlConditionName(String crawlConditionName) {

		this.crawlConditionName = crawlConditionName;
	}

	public String getModelName() {

		return modelName;
	}

	public void setModelName(String modelName) {

		this.modelName = modelName;
	}

	public String getStrategyName() {

		return strategyName;
	}

	public void setStrategyName(String strategyName) {

		this.strategyName = strategyName;
	}

	public String getMissionName() {

		return missionName;
	}

	public void setMissionName(String missionName) {

		this.missionName = missionName;
	}

	public String getMissionStatus() {

		return missionStatus;
	}

	public void setMissionStatus(String missionStatus) {

		this.missionStatus = missionStatus;
	}

	public String getMissionPriority() {

		return missionPriority;
	}

	public void setMissionPriority(String missionPriority) {

		this.missionPriority = missionPriority;
	}

	public String getEncode() {

		return encode;
	}

	public void setEncode(String encode) {

		this.encode = encode;
	}

	public boolean isGzip() {
		return gzip;
	}

	public void setGzip(boolean gzip) {
		this.gzip = gzip;
	}

	public boolean getNeedProxy() {

		return needProxy;
	}

	public void setNeedProxy(boolean needProxy) {

		this.needProxy = needProxy;
	}

	public int getRestLevel() {

		return restLevel;
	}

	public void setRestLevel(int restLevel) {

		this.restLevel = restLevel;
	}

	public String getHttpMethod() {

		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {

		this.httpMethod = httpMethod;
	}

	public String getDomain() {

		return domain;
	}

	public void setDomain(String domain) {

		this.domain = domain;
	}

	public String getEntranceName() {

		return entranceName;
	}

	public void setEntranceName(String entranceName) {

		this.entranceName = entranceName;
	}

	public HandleStrategy getHandleStrategy() {

		return handleStrategy;
	}

	public void setHandleStrategy(HandleStrategy handleStrategy) {

		this.handleStrategy = handleStrategy;
	}

	public CapturerUtil getCapturer() {

		return capturer;
	}

	public void setCapturer(CapturerUtil capturer) {

		this.capturer = capturer;
	}

	public int getCrawlerThreadNum() {

		if (crawlerThreadNum <= 0) {
			crawlerThreadNum = 1;
		}
		return crawlerThreadNum;
	}

	public void setCrawlerThreadNum(int crawlerThreadNum) {

		this.crawlerThreadNum = crawlerThreadNum;
	}

	public int getParserThreadNum() {

		if (parserThreadNum <= 0) {
			parserThreadNum = 1;
		}
		return parserThreadNum;
	}

	public void setParserThreadNum(int parserThreadNum) {

		this.parserThreadNum = parserThreadNum;
	}

	public int getHandleThreadNum() {

		if (handleThreadNum <= 0) {
			handleThreadNum = 1;
		}
		return handleThreadNum;
	}

	public void setHandleThreadNum(int handleThreadNum) {

		this.handleThreadNum = handleThreadNum;
	}

	public int getHandleUnitMaxSize() {

		if (handleUnitMaxSize <= 0) {
			handleUnitMaxSize = 1;
		}
		return handleUnitMaxSize;
	}

	public void setHandleUnitMaxSize(int handleUnitMaxSize) {

		this.handleUnitMaxSize = handleUnitMaxSize;
	}

	public boolean getDiskCache() {

		return diskCache;
	}

	public void setDiskCache(boolean diskCache) {

		this.diskCache = diskCache;
	}

	public long getDiskCacheOutTimeSecond() {

		return diskCacheOutTimeSecond;
	}

	public void setDiskCacheOutTimeSecond(long diskCacheOutTimeSecond) {

		this.diskCacheOutTimeSecond = diskCacheOutTimeSecond;
	}

	public String getMailTo() {

		return mailTo;
	}

	public void setMailTo(String mailTo) {

		this.mailTo = mailTo;
	}

	public int getMaxMail() {

		return maxMail;
	}

	public void setMaxMail(int maxMail) {

		this.maxMail = maxMail;
	}

	public boolean getPageCheckSwitch() {

		return pageCheckSwitch;
	}

	public void setPageCheckSwitch(boolean pageCheckSwitch) {

		this.pageCheckSwitch = pageCheckSwitch;
	}

	public String getPageCheckRegex() {

		return pageCheckRegex;
	}

	public void setPageCheckRegex(String pageCheckRegex) {

		this.pageCheckRegex = pageCheckRegex;
	}

}
