package com.grabber.strategy;

import com.grabber.model.BaseModel;
import com.grabber.model.Caipiao;

/**
 * @author WY
 */
public class CaipiaoStrategy extends HandleStrategy {

	@Override
	public Object handleObject(BaseModel object) {
		Caipiao caipiao = (Caipiao) object;
		System.out.println(caipiao);
		return caipiao;
	}
	
}
