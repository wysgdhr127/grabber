package com.grabber.model;

/**
 * 解析单元，存在于一个阻塞队列中
 * 
 * @author WY 14 Mar 2018 15:10:31
 */
public class ParseUnit extends BaseModel {

	private String url;// 入口url
	private int entranceIndex;

	public ParseUnit(String url, int entranceIndex) {

		this.url = url;
		this.entranceIndex = entranceIndex;
	}

	public String getUrl() {

		return url;
	}

	public void setUrl(String url) {

		this.url = url;
	}

	public int getEntranceIndex() {

		return entranceIndex;
	}

	public void setEntranceIndex(int entranceIndex) {

		this.entranceIndex = entranceIndex;
	}

}
