package com.grabber.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WY
 */
public class ReflectUtil {
	
	/**
	 * java反射bean的get方法
	 * 
	 * @param objectClass
	 * @param fieldName
	 * @return
	 */
	
	public static Method getGetMethod(Class<? extends Object> objectClass, String fieldName) {

		StringBuffer sb = new StringBuffer();
		
		sb.append("get");
		
		sb.append(fieldName.substring(0, 1).toUpperCase());
		
		sb.append(fieldName.substring(1));
		
		try {
			
			return objectClass.getMethod(sb.toString());
			
		} catch (Exception e) {
			
		}
		
		return null;
		
	}
	
	/**
	 * java反射bean的任何方法
	 * 
	 * @param objectClass
	 * @param fieldName
	 * @return
	 */
	
	public static Method getMethod(Class<? extends Object> objectClass, String methodName) {

		try {
			Method[] methods = objectClass.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					return method;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * java反射bean的set方法
	 * 
	 * @param objectClass
	 * @param fieldName
	 * @return
	 */
	
	public static Method getSetMethod(Class<? extends Object> objectClass, String fieldName) {

		try {
			
			Class<?>[] parameterTypes = new Class[1];
			
			Field field = objectClass.getDeclaredField(fieldName);
			
			parameterTypes[0] = field.getType();
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("set");
			
			sb.append(fieldName.substring(0, 1).toUpperCase());
			
			sb.append(fieldName.substring(1));
			
			Method method = objectClass.getMethod(sb.toString(), parameterTypes);
			
			return method;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		return null;
		
	}
	
	/**
	 * 执行set方法
	 * 
	 * @param o执行对象
	 * @param fieldName属性
	 * @param value值
	 */
	
	public static void invokeSet(Object o, String fieldName, Object value) {

		Method method = getSetMethod(o.getClass(), fieldName);
		
		try {
			
			method.invoke(o, new Object[] { value });
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	/**
	 * 执行get方法
	 * 
	 * @param o执行对象
	 * @param fieldName属性
	 */
	
	public static Object invokeMethod(Object o, String methodName, Object param) {

		Method method = getMethod(o.getClass(), methodName);
		
		try {
			return method.invoke(o, param);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		return null;
		
	}
	
	/**
	 * 执行get方法
	 * 
	 * @param o执行对象
	 * @param fieldName属性
	 */
	
	public static Object invokeGet(Object o, String fieldName) {

		Method method = getGetMethod(o.getClass(), fieldName);
		
		try {
			
			return method.invoke(o, new Object[0]);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		return null;
		
	}
	
	/**
	 * 比较两个对象中不同值的属性名，并将其返回
	 */
	public static List<String> getFieldInDifferentObjectValue(Object o1, Object o2) {

		List<String> diffFiledsNameList = new ArrayList<String>();
		if (!o1.getClass().equals(o2.getClass())) {
			return diffFiledsNameList;
		}
		Field[] fields = o1.getClass().getDeclaredFields();
		for (Field field : fields) {
			Object a1 = invokeGet(o1, field.getName());
			Object a2 = invokeGet(o2, field.getName());
			if (a1 == null && a2 == null) {
				continue;
			} else if (a1 == null || a2 == null) {
				diffFiledsNameList.add(field.getName());
			} else {
				if (!a1.equals(a2)) {
					diffFiledsNameList.add(field.getName());
				}
			}
		}
		return diffFiledsNameList;
	}
	
	public static void main(String[] args) {

	}
}
