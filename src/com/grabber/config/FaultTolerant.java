package com.grabber.config;

/**
 * @author WY 14 Mar 2018 17:12:27
 */
public class FaultTolerant {
	
	private String xpath;
	private String regexIndex;
	private String regex;
	private String regexValue;
	
	public String getXpath() {

		return xpath;
	}
	
	public void setXpath(String xpath) {

		this.xpath = xpath;
	}
	
	public String getRegexIndex() {

		return regexIndex;
	}
	
	public void setRegexIndex(String regexIndex) {

		this.regexIndex = regexIndex;
	}
	
	public String getRegex() {

		return regex;
	}
	
	public void setRegex(String regex) {

		this.regex = regex;
	}
	
	public String getRegexValue() {

		return regexValue;
	}
	
	public void setRegexValue(String regexValue) {

		this.regexValue = regexValue;
	}
	
}
