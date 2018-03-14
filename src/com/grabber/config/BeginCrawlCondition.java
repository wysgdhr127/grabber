package com.grabber.config;

/**
 * 开始抓取的条件
 * 
 * @author WY
 */
public class BeginCrawlCondition {
	
	private String index;
	private String type;
	private String xpath;
	private String comparator;
	private String goal;
	private String canBeNull;
	
	public String getIndex() {

		return index;
	}
	
	public void setIndex(String index) {

		this.index = index;
	}
	
	public String getType() {

		return type;
	}
	
	public void setType(String type) {

		this.type = type;
	}
	
	public String getXpath() {

		return xpath;
	}
	
	public void setXpath(String xpath) {

		this.xpath = xpath;
	}
	
	public String getComparator() {

		return comparator;
	}
	
	public void setComparator(String comparator) {

		this.comparator = comparator;
	}
	
	public String getGoal() {

		return goal;
	}
	
	public void setGoal(String goal) {

		this.goal = goal;
	}
	
	public String getCanBeNull() {

		return canBeNull;
	}
	
	public void setCanBeNull(String canBeNull) {

		this.canBeNull = canBeNull;
	}
	
}
