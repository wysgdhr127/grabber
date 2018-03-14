package com.grabber.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.StringUtils;

import com.grabber.config.Mission;
import com.grabber.util.LoggerUtil;

public class ErrorUnit extends BaseModel {
	
	private Mission mission;
	private String content;
	
	public ErrorUnit(Mission mission, String errorMsg, Exception exception) {

		this.mission = mission;
		
		StringBuffer content = new StringBuffer();
		if (mission != null) {
			
			if (StringUtils.isNotBlank(errorMsg)) {
				content.append(errorMsg).append("\n");
			}
			if (exception != null) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				exception.printStackTrace(pw);
				content.append(sw.toString()).append("\n");
				pw.close();
				try {
					sw.close();
				} catch (IOException e) {
					LoggerUtil.errorLog.error(e);
				}
			}
		}
		this.content = content.toString();
		
	}
	
	public Mission getMission() {

		return mission;
	}
	
	public void setMission(Mission mission) {

		this.mission = mission;
	}
	
	public String getContent() {

		return content;
	}
	
	public void setContent(String content) {

		this.content = content;
	}
	
}
