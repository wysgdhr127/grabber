package com.grabber.model;

import java.util.List;

/**
 * @author WY 14 Mar 2018 15:15:31
 */
public class HandleUnit extends BaseModel {

	private List<BaseModel> objectList;

	public HandleUnit(List<BaseModel> objectList) {

		this.objectList = objectList;
	}

	public List<BaseModel> getObjectList() {

		return objectList;
	}

	public void setObjectList(List<BaseModel> objectList) {

		this.objectList = objectList;
	}

}
