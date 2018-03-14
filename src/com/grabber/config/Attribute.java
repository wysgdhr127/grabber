package com.grabber.config;

/**
 * Attribute: 描述对象属性与其在页面中对应数据的关系，对应Model配置文件中的<Attribute>
 * @author WY 14 Mar 2018 14:12:47
 */
public class Attribute {
	
	private String attr;// 对象中的属性名
	private String name;// 对象中的属性描述
	private String xpath;// 该属性在页面中对应的数据的xpath
	private String regex;// 需要进行正则表达式过滤的表达式
	private String regexIndex;// 该属性对应数据在正则中的位置
	private String warningLevel;// 报警级别，见ICommonConstant，5最重，1最轻
	
	public String getAttr() {

		return attr;
	}
	
	public void setAttr(String attr) {

		this.attr = attr;
	}
	
	public String getName() {

		return name;
	}
	
	public void setName(String name) {

		this.name = name;
	}
	
	public String getXpath() {

		return xpath;
	}
	
	public void setXpath(String xpath) {

		this.xpath = xpath;
	}
	
	public String getRegex() {

		return regex;
	}
	
	public void setRegex(String regex) {

		this.regex = regex;
	}
	
	public String getRegexIndex() {

		return regexIndex;
	}
	
	public void setRegexIndex(String regexIndex) {

		this.regexIndex = regexIndex;
	}
	
	public String getWarningLevel() {

		return warningLevel;
	}
	
	public void setWarningLevel(String warningLevel) {

		this.warningLevel = warningLevel;
	}
	
}
