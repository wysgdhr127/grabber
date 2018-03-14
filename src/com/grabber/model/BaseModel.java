package com.grabber.model;

import java.lang.reflect.Field;

import com.grabber.util.LoggerUtil;

public class BaseModel {

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		Field fields[] = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				sb.append(field.getName()).append(":").append(field.get(this))
						.append(",");
			} catch (Exception e) {
				LoggerUtil.errorLog.error(e);
			}
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 得到属性名数组
	 */
	public String[] getAttrNameArray() {
		Field fields[] = this.getClass().getDeclaredFields();
		String[] attrNameArray = new String[fields.length];
		int i = 0;
		for (Field field : fields) {
			field.setAccessible(true);
			attrNameArray[i++] = field.getName();
		}
		return attrNameArray;
	}

	/**
	 * 得到属性值数组
	 */
	public Object[] getAttrValueArray() {
		Field fields[] = this.getClass().getDeclaredFields();
		Object[] attrValueArray = new Object[fields.length];
		int i = 0;
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				attrValueArray[i++] = field.get(this);
			} catch (Exception e) {
				LoggerUtil.errorLog.error(e);
			}
		}
		return attrValueArray;
	}
}
