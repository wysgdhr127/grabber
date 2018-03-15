package com.grabber.strategy;

import java.util.List;

import com.grabber.model.BaseModel;

/**
 * @author WY 14 Mar 2018 15:10:31
 */
public abstract class HandleStrategy {

	public abstract Object handleObject(BaseModel object);

	public abstract Object handleObjects(List<BaseModel> object, String missionName);
}
