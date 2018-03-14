package com.grabber.config;

import java.util.HashMap;
import java.util.List;

import com.grabber.model.BaseModel;

/**
 * EntranceFile: 入口文件
 */
public class Entrance extends BaseModel {
	
	public static String ENTRANCE_NAME = "entrance";
	public static int KEY_INDEX = 0;
	public static int VALUE_INDEX = 1;
	private HashMap<String,List<String>> property;// 属性值
	private String[] keys;
	private String[][] values;
	private String[] entrances;
	
	public HashMap<String,List<String>> getProperty() {

		return property;
	}
	
	public void setProperty(HashMap<String,List<String>> property) {

		this.property = property;
		
		if (property != null) {
			entrances = new String[property.get(ENTRANCE_NAME).size()];
			List<String> urls = property.get(ENTRANCE_NAME);
			for (int j = 0; j < urls.size(); j++) {
				entrances[j] = urls.get(j);
			}
			
			if (property.keySet().size() > 1) {
				keys = new String[property.keySet().size() - 1];
				values = new String[property.get(ENTRANCE_NAME).size()][property.keySet().size() - 1];
				int i = 0;
				for (String key : property.keySet()) {
					if (!ENTRANCE_NAME.equals(key)) {
						keys[i] = key;
						List<String> value = property.get(key);
						for (int j = 0; j < value.size(); j++) {
							values[j][i] = value.get(j);
						}
						i++;
					}
				}
			}
		}
	}
	
	public int getEntanceSize() {

		int size = 0;
		if (property != null && property.get(ENTRANCE_NAME) != null) {
			size = property.get(ENTRANCE_NAME).size();
		}
		return size;
	}
	
	public String[] getKeys() {

		return keys;
	}
	
	public String[] getValuesByIndex(int index) {

		if (values != null && index >= 0 && index < values.length) {
			return values[index];
		} else {
			return null;
		}
	}
	
	public String getEntranceByIndex(int index) {

		if (entrances != null && index >= 0 && index < entrances.length) {
			return entrances[index];
		} else {
			return null;
		}
		
	}
}
