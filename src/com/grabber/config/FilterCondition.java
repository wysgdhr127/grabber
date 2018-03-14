package com.grabber.config;

/**
 * 筛选条件
 * 
 * @author WY
 */
public class FilterCondition {
	
	private String xpath;
	private String index;
	private String name;
	private String depth;
	
	public String getXpath() {

		return xpath;
	}
	
	public void setXpath(String xpath) {

		this.xpath = xpath;
	}
	
	public String getIndex() {

		return index;
	}
	
	public void setIndex(String index) {

		this.index = index;
	}
	
	public String getName() {

		return name;
	}
	
	public void setName(String name) {

		this.name = name;
	}
	
	public String getDepth() {

		return depth;
	}
	
	public void setDepth(String depth) {

		this.depth = depth;
	}
	
}
