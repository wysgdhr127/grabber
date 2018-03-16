package com.grabber.model.caipiao;

import java.sql.Timestamp;

import com.grabber.model.BaseModel;

public class CaipiaoSp extends BaseModel {

	private Long matchSpId;
	private Long matchId;
	private String matchIssue;
	private String matchSn;
	private String spJson;
	private String homeRecentRecord;
	private String awayRecentRecord;
	private String dataFrom;
	private String isstop;
	private Integer status;
	private Timestamp createTime;
	private Timestamp updateTime;

	public CaipiaoSp() {
		super();
	}

	public CaipiaoSp(Caipiao caipiao) {
		this.matchId = caipiao.getId();
		this.matchIssue = caipiao.getMatchIssue();
		this.matchSn = caipiao.getMatchSn();
		this.spJson = caipiao.getBetSp();
		this.dataFrom = caipiao.getDataFrom();
		this.isstop = caipiao.getIsstop();
	}

	public Long getMatchSpId() {
		return matchSpId;
	}

	public void setMatchSpId(Long matchSpId) {
		this.matchSpId = matchSpId;
	}

	public Long getMatchId() {
		return matchId;
	}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	public String getMatchIssue() {
		return matchIssue;
	}

	public void setMatchIssue(String matchIssue) {
		this.matchIssue = matchIssue;
	}

	public String getMatchSn() {
		return matchSn;
	}

	public void setMatchSn(String matchSn) {
		this.matchSn = matchSn;
	}

	public String getSpJson() {
		return spJson;
	}

	public void setSpJson(String spJson) {
		this.spJson = spJson;
	}

	public String getHomeRecentRecord() {
		return homeRecentRecord;
	}

	public void setHomeRecentRecord(String homeRecentRecord) {
		this.homeRecentRecord = homeRecentRecord;
	}

	public String getAwayRecentRecord() {
		return awayRecentRecord;
	}

	public void setAwayRecentRecord(String awayRecentRecord) {
		this.awayRecentRecord = awayRecentRecord;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

	public String getIsstop() {
		return isstop;
	}

	public void setIsstop(String isstop) {
		this.isstop = isstop;
	}

}
