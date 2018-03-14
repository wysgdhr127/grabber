package com.grabber.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Model:描述页面结构与对象属性对应关系的对象
 * 
 * @author WY 14 Mar 2018 17:52:47
 */
public class Model {
	
	private String modelClass;// 页面属性对应的class的全称，对应Model配置文件中的<Model class="com.netease.coupon.monitor.vo.Coupon">
	private String modelXpath;// 页面属性所在范围的xpath，对应Model配置文件中的<Xpath>
	private List<Attribute> attributes;// 要解析的数据属性，见Attribute，对应Model配置文件中<Attributes>
	private FaultTolerant faultTolerant;// 容错
	// ----属性子类
	private String subModelClass;// 属性子类名
	private String subModelName;// 属性子类model名
	private String subModelMethodName;// 属性子类与本类的联系的方法名
	
	public void addAttribute(Attribute attribute) {

		if (attributes == null) {
			attributes = new ArrayList<Attribute>();
		}
		if (attribute != null) {
			attributes.add(attribute);
		}
	}
	
	public void addFaultTolerant(FaultTolerant faultTolerant) {

		this.faultTolerant = faultTolerant;
	}
	
	public String getModelClass() {

		return modelClass;
	}
	
	public void setModelClass(String modelClass) {

		this.modelClass = modelClass;
	}
	
	public String getModelXpath() {

		return modelXpath;
	}
	
	public void setModelXpath(String modelXpath) {

		this.modelXpath = modelXpath;
	}
	
	public List<Attribute> getAttributes() {

		return attributes;
	}
	
	public void setAttributes(List<Attribute> attributes) {

		this.attributes = attributes;
	}
	
	public FaultTolerant getFaultTolerant() {

		return faultTolerant;
	}
	
	public void setFaultTolerant(FaultTolerant faultTolerant) {

		this.faultTolerant = faultTolerant;
	}
	
	public String getSubModelClass() {

		return subModelClass;
	}
	
	public void setSubModelClass(String subModelClass) {

		this.subModelClass = subModelClass;
	}
	
	public String getSubModelName() {

		return subModelName;
	}
	
	public void setSubModelName(String subModelName) {

		this.subModelName = subModelName;
	}
	
	public String getSubModelMethodName() {

		return subModelMethodName;
	}
	
	public void setSubModelMethodName(String subModelMethodName) {

		this.subModelMethodName = subModelMethodName;
	}
	
}
