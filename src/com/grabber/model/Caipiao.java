package com.grabber.model;

import java.sql.Timestamp;

public class Caipiao extends BaseModel {

	private String matchIssue;// 格式：20170320001
	private String matchId;// 竞彩官网ID
	private String leagueName;// 联赛名称
	private Timestamp issueSaleEndTime;// 我方销售截止时间
	private Timestamp issueEndTime;// 官方截止投注时间
	private Timestamp matchStartTime;// 比赛开始时间
	private String issueSaleEndTimeString;
	private String issueEndTimeString;
	private String matchStartTimeString;
	private String matchSn;
	
	private String isstop;
	
	private String homeTeamName;
	private String awayTeamName;
	private String homeTeamRank;
	private String awayTeamRank;
	private String handicap;// +1或者-1 篮球让分也用此字段表示
	private String localWeather;
	
	private String spf;
	private String rfspf;
	private String bf;
	private String bqc;
	private String zjq;

	public String getMatchIssue() {
		return matchIssue;
	}

	public void setMatchIssue(String matchIssue) {
		this.matchIssue = matchIssue;
	}

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public String getLeagueName() {
		return leagueName;
	}

	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}

	public Timestamp getIssueSaleEndTime() {
		return issueSaleEndTime;
	}

	public void setIssueSaleEndTime(Timestamp issueSaleEndTime) {
		this.issueSaleEndTime = issueSaleEndTime;
	}

	public Timestamp getIssueEndTime() {
		return issueEndTime;
	}

	public void setIssueEndTime(Timestamp issueEndTime) {
		this.issueEndTime = issueEndTime;
	}

	public Timestamp getMatchStartTime() {
		return matchStartTime;
	}

	public void setMatchStartTime(Timestamp matchStartTime) {
		this.matchStartTime = matchStartTime;
	}

	public String getHomeTeamName() {
		return homeTeamName;
	}

	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}

	public String getAwayTeamName() {
		return awayTeamName;
	}

	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
	}

	public String getHomeTeamRank() {
		return homeTeamRank;
	}

	public void setHomeTeamRank(String homeTeamRank) {
		this.homeTeamRank = homeTeamRank;
	}

	public String getAwayTeamRank() {
		return awayTeamRank;
	}

	public void setAwayTeamRank(String awayTeamRank) {
		this.awayTeamRank = awayTeamRank;
	}

	public String getHandicap() {
		return handicap;
	}

	public void setHandicap(String handicap) {
		this.handicap = handicap;
	}

	public String getLocalWeather() {
		return localWeather;
	}

	public void setLocalWeather(String localWeather) {
		this.localWeather = localWeather;
	}

	public String getIssueSaleEndTimeString() {
		return issueSaleEndTimeString;
	}

	public void setIssueSaleEndTimeString(String issueSaleEndTimeString) {
		this.issueSaleEndTimeString = issueSaleEndTimeString;
	}

	public String getIssueEndTimeString() {
		return issueEndTimeString;
	}

	public void setIssueEndTimeString(String issueEndTimeString) {
		this.issueEndTimeString = issueEndTimeString;
	}

	public String getMatchStartTimeString() {
		return matchStartTimeString;
	}

	public void setMatchStartTimeString(String matchStartTimeString) {
		this.matchStartTimeString = matchStartTimeString;
	}

	public String getSpf() {
		return spf;
	}

	public void setSpf(String spf) {
		this.spf = spf;
	}

	public String getRfspf() {
		return rfspf;
	}

	public void setRfspf(String rfspf) {
		this.rfspf = rfspf;
	}

	public String getBf() {
		return bf;
	}

	public void setBf(String bf) {
		this.bf = bf;
	}

	public String getBqc() {
		return bqc;
	}

	public void setBqc(String bqc) {
		this.bqc = bqc;
	}

	public String getZjq() {
		return zjq;
	}

	public void setZjq(String zjq) {
		this.zjq = zjq;
	}

	public String getIsstop() {
		return isstop;
	}

	public void setIsstop(String isstop) {
		this.isstop = isstop;
	}

	public String getMatchSn() {
		return matchSn;
	}

	public void setMatchSn(String matchSn) {
		this.matchSn = matchSn;
	}
	
}
