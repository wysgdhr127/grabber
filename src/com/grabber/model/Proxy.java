package com.grabber.model;

public class Proxy extends BaseModel {
	
	private String ip;
	private String port;
	private String type;
	private String speed;
	private String areas;
	private String from;
	
	public String getIp() {

		return ip;
	}
	
	public void setIp(String ip) {

		this.ip = ip;
	}
	
	public String getPort() {

		return port;
	}
	
	public void setPort(String port) {

		this.port = port;
	}
	
	public String getType() {

		return type;
	}
	
	public void setType(String type) {

		this.type = type;
	}
	
	public String getSpeed() {

		return speed;
	}
	
	public void setSpeed(String speed) {

		this.speed = speed;
	}
	
	public String getAreas() {

		return areas;
	}
	
	public void setAreas(String areas) {

		this.areas = areas;
	}
	
	public String getFrom() {

		return from;
	}
	
	public void setFrom(String from) {

		this.from = from;
	}
	
}
