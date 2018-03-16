package com.grabber.model.caipiao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.grabber.constant.CaipiaoConstants;
import com.grabber.constant.CommonConstant;
import com.grabber.model.BaseModel;
import com.grabber.util.ValidateUtil;

public class Caipiao extends BaseModel {

	private final static String[] BF_STRINGS = { "10", "20", "21", "30", "31", "32", "40", "41", "42", "50", "51",
			"52", "3A", "00", "11", "22", "33", "1A", "01", "02", "12", "03", "13", "23", "04", "14", "24", "05", "15",
			"25", "0A" };

	private final static String[] ZJQ_STRINGS = { "0", "1", "2", "3", "4", "5", "6", "7" };

	private final static String[] BQC_STRINGS = { "33", "31", "30", "13", "11", "10", "03", "01", "00" };

	public final static String WEEK_CHN = "空一二三四五六日";

	private Long id;
	private String matchIssue;// 格式：20170320001
	private String matchId;// 竞彩官网ID
	private String leagueName;// 联赛名称
	private Timestamp issueEndTime;// 官方截止投注时间
	private Timestamp matchStartTime;// 比赛开始时间
	private String issueEndTimeString;
	private String matchStartTimeString;
	private String matchSn;

	private String dataFrom;

	private String week;
	private Integer status;
	private Timestamp createTime;
	private Timestamp updateTime;

	private String isstop;

	private String homeTeamName;
	private String awayTeamName;
	private String homeTeamRank;
	private String awayTeamRank;
	private String handicap;// +1或者-1 篮球让分也用此字段表示
	private String localWeather;

	// 500万彩票网数据待处理
	private String pdate; // 2018-03-15去掉-，配合pname生成matchIssue
	private String pname; // "4001"处理成"周四001"

	private String spf;
	private String rfspf;
	private String bf;
	private String bqc;
	private String zjq;

	private String betSp;

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

	public String getIssueEndTimeString() {
		return issueEndTimeString;
	}

	public void setIssueEndTimeString(String issueEndTimeString) {
		if (ValidateUtil.isNumber(issueEndTimeString)) {
			issueEndTime = new Timestamp(Long.parseLong(issueEndTimeString));
		}
		this.issueEndTimeString = issueEndTimeString;
	}

	public String getMatchStartTimeString() {
		return matchStartTimeString;
	}

	public void setMatchStartTimeString(String matchStartTimeString) {
		if (ValidateUtil.isNumber(matchStartTimeString)) {
			matchStartTime = new Timestamp(Long.parseLong(matchStartTimeString));
		}
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

	public String getBetSp() {
		Map<String, String> betSpMap = new HashMap<>();
		betSpMap.put(CaipiaoConstants.SP + CaipiaoConstants.SPF, spf);
		betSpMap.put(CaipiaoConstants.SP + CaipiaoConstants.RQSPF, rfspf);
		betSpMap.put(CaipiaoConstants.SP + CaipiaoConstants.BF, bf);
		betSpMap.put(CaipiaoConstants.SP + CaipiaoConstants.BQC, bqc);
		betSpMap.put(CaipiaoConstants.SP + CaipiaoConstants.ZJQ, zjq);
		betSp = betSpMap.toString();
		return betSp;
	}

	public void setBetSp(String betSp) {
		this.betSp = betSp;
	}

	public void combine(Caipiao caipiao) {
		if (StringUtils.isNotBlank(caipiao.getBf())) {
			this.setBf(caipiao.getBf());
		}
		if (StringUtils.isNotBlank(caipiao.getBqc())) {
			this.setBqc(caipiao.getBqc());
		}
		if (StringUtils.isNotBlank(caipiao.getZjq())) {
			this.setZjq(caipiao.getZjq());
		}
		if (StringUtils.isBlank(week)) {
			week = WEEK_CHN.indexOf(matchSn.charAt(1)) + "";
		}
		getBetSp();
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
		if (dataFrom.equals(DataFrom.FIFEMILLION_FROM)) {
			if (StringUtils.isBlank(matchIssue)) {
				matchIssue = pdate.replaceAll("-", "") + pname;
			}
			if (matchStartTime == null && StringUtils.isNotBlank(matchStartTimeString)) {
				matchStartTime = Timestamp.valueOf(matchStartTimeString.replace("开赛时间：", "") + ":00");
			}

			if (issueEndTime == null && StringUtils.isNotBlank(issueEndTimeString)) {
				issueEndTime = Timestamp.valueOf(issueEndTimeString);
			}

			if (StringUtils.isBlank(matchSn)) {
				matchSn = "周" + WEEK_CHN.charAt(pname.charAt(0) - '0') + pname.substring(1);
				week = pname.charAt(0) - '0' + "";
			}

			if (StringUtils.isNotBlank(bf)) {
				bf = handleData(bf, BF_STRINGS);
			}
			if (StringUtils.isNotBlank(zjq)) {
				zjq = handleData(zjq, ZJQ_STRINGS);
			}
			if (StringUtils.isNotBlank(bqc)) {
				bqc = handleData(bqc, BQC_STRINGS);
			}

			getBetSp();
		} else if (dataFrom.equals(DataFrom.SPORTTERY_FROM)) {
			if (StringUtils.isBlank(week) && StringUtils.isNotBlank(matchSn)) {
				week = WEEK_CHN.indexOf(matchSn.charAt(1)) + "";
			}
			getBetSp();
		}
		this.dataFrom = dataFrom;
	}

	private String handleData(String orignal, String[] orders) {
		String[] orignals = orignal.split(CommonConstant.REGEX_RESULT_SPLITOR);
		orignal = "";
		for (String order : orders) {
			int sp = 0;
			int score = 1;
			for (String b : orignals) {
				if (b.split("\\|")[0].contains(".")) {
					score = 0;
					sp = 1;
				}
				if (b.split("\\|").length > 1) {
					if (order.equals(b.split("\\|")[score])) {
						orignal += b.split("\\|")[sp] + ",";
					}
				}
			}
		}
		return orignal.substring(0, orignal.length() - 1);
	}

	public String getPdate() {
		return pdate;
	}

	public void setPdate(String pdate) {
		this.pdate = pdate;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

}
