package com.grabber.strategy;

import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.grabber.constant.SportteryConstant;
import com.grabber.model.BaseModel;
import com.grabber.model.caipiao.Caipiao;
import com.grabber.model.caipiao.CaipiaoSp;
import com.grabber.model.caipiao.DataFrom;
import com.grabber.util.DateUtils;
import com.grabber.util.HttpUtils;
import com.grabber.util.LoggerUtil;
import com.grabber.util.UnicodeUtils;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * @author WY
 */
public class CaipiaoHandleStrategy extends HandleStrategy {

	private static final String mybatisConfig = "sqlmap/SqlMapConfig.xml";// mybatis配置文件名，需要是完整路径
	private static Map<String, SqlSessionFactory> factoryMap = new HashMap<String, SqlSessionFactory>();// Mybatis的Factory

	@Override
	public Object handleObject(BaseModel object) {
		Caipiao caipiao = (Caipiao) object;
		return caipiao;
	}

	@Override
	public List<Caipiao> handleObjects(List<BaseModel> object, String missionName) {
		List<Caipiao> tempList = new ArrayList<Caipiao>();
		List<Caipiao> caipiaoList = new ArrayList<Caipiao>();
		for (BaseModel baseModel : object) {
			Caipiao caipiao = (Caipiao) baseModel;
			caipiao.setDataFrom(DataFrom.getDescription(missionName));
			if (caipiao.getDataFrom().equals(DataFrom.NETEASE_FROM)) {
				if (StringUtils.isNotBlank(caipiao.getMatchId())) {
					tempList.add(caipiao);
				} else {
					for (Caipiao temp : tempList) {
						if (temp.getMatchIssue().equals(caipiao.getMatchIssue())) {
							temp.combine(caipiao);
							caipiaoList.add(temp);
						}
					}
				}
			} else {
				caipiaoList.add(caipiao);
			}
		}
		export(caipiaoList, missionName);
		return caipiaoList;
	}

	public String sendMessage() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append(SportteryConstant.SPORTTERY_REQUEST_PART1).append("&");
		for (String poolCode : SportteryConstant.getPoolCodeList()) {
			buffer.append(SportteryConstant.SPORTTERY_REQUEST_PART2).append(poolCode).append("&");
		}
		buffer.append("_=" + System.currentTimeMillis());
		return HttpUtils.sendGet(SportteryConstant.SPORTTERY_INFO_URL + buffer.toString());
	}

	public void handleMessage(String responseMessage) {
		responseMessage = UnicodeUtils.unicode2String(responseMessage.replace("getData(", "").replace(");", ""));
		Map responseMap = (Map) JSONObject.fromObject(responseMessage);
		List<Caipiao> caipiaoList = new ArrayList<>();
		for (Object object : ((Map) responseMap.get("data")).values()) {
			Map<String, Object> map = (Map<String, Object>) object;
			Caipiao caipiao = new Caipiao();
			setCaiPiaoProperty(map, caipiao);
			caipiaoList.add(caipiao);
		}
		export(caipiaoList, DataFrom.SPORTTERY);
	}

	private void setCaiPiaoProperty(Map<String, Object> map, Caipiao caipiao) {
		String num = ((String) map.get("num"));
		String date = ((String) map.get("date")).replace("-", "");
		Timestamp startTime = DateUtils.formatToTimestamp(map.get("date") + " " + map.get("time"),
				DateUtils.DATE_FORMAT_YYYYMMDD_HHMMSS);
		caipiao.setMatchIssue(date + caipiao.WEEK_CHN.indexOf(num.charAt(1)) + num.substring(2));
		caipiao.setMatchId((String) map.get("id"));
		caipiao.setHomeTeamName((String) map.get("h_cn_abbr"));
		caipiao.setAwayTeamName((String) map.get("a_cn_abbr"));
		caipiao.setHomeTeamRank((String) map.get("h_order"));
		caipiao.setAwayTeamRank((String) map.get("a_order"));
		caipiao.setIssueEndTime(startTime);
		caipiao.setMatchStartTime(startTime);
		caipiao.setLeagueName((String) map.get("l_cn_abbr"));
		caipiao.setMatchSn(num);
		caipiao.setDataFrom(DataFrom.SPORTTERY_FROM);
		caipiao.setSpf(getSp((Map) map.get(SportteryConstant.SPF_POOL_CODE), SportteryConstant.SPF_KEY));
		caipiao.setRfspf(getSp((Map) map.get(SportteryConstant.RQSPF_POOL_CODE), SportteryConstant.SPF_KEY));
		caipiao.setBf(getSp((Map) map.get(SportteryConstant.BF_POOL_CODE), SportteryConstant.BF_KEY));
		caipiao.setZjq(getSp((Map) map.get(SportteryConstant.ZJQ_POOL_CODE), SportteryConstant.ZJQ_KEY));
		caipiao.setBqc(getSp((Map) map.get(SportteryConstant.BQC_POOL_CODE), SportteryConstant.BQC_KEY));
		caipiao.setHandicap((String) ((Map) map.get(SportteryConstant.RQSPF_POOL_CODE)).get("fixedodds"));
		// 中国竞彩网接口只回调没有停止的比赛
		caipiao.setIsstop("0");
	}

	private String getSp(Map spMap, String key) {
		if (spMap != null) {
			StringBuffer buffer = new StringBuffer();
			for (String code : key.split(",")) {
				code = code.trim();
				buffer.append((String) spMap.get(code)).append(",");
			}
			return buffer.toString().substring(0, buffer.length() - 1);
		}
		return null;
	}

	private void export(List<Caipiao> list, String exporterName) {
		// 4.1如果还没有建立数据库连接
		if (!factoryMap.containsKey(exporterName)) {
			try {
				synchronized (factoryMap) {
					if (!factoryMap.containsKey(exporterName)) {
						Reader reader = Resources.getResourceAsReader(mybatisConfig);
						SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
						SqlSessionFactory factory = builder.build(reader);
						factoryMap.put(exporterName, factory);
					}
				}
			} catch (IOException e) {
				LoggerUtil.errorLog.error("[export-mybatis] error happen when vo export to mybatis.", e);
			}
		}
		// 4.2如果已经建立数据库连接
		if (factoryMap.containsKey(exporterName)) {
			SqlSession session = factoryMap.get(exporterName).openSession();
			try {
				insertOrUpdateCaipiao(list, session);
			} catch (Exception e) {
				LoggerUtil.errorLog.error("[export-mybatis] error happen when vo export to mybatis.", e);
			} finally {
				session.commit();
				session.close();
			}
		}
	}

	private void insertOrUpdateCaipiao(List<Caipiao> list, SqlSession session) {
		List<Caipiao> inserts = new ArrayList<Caipiao>();
		List<Caipiao> updates = new ArrayList<Caipiao>();
		for (Caipiao caipiao : list) {
			List<Caipiao> exists = session.selectList("caipiaoMatch.selectMatch", caipiao);
			if (exists.size() > 0) {
				caipiao.setId(exists.get(0).getId());
				updates.add(caipiao);
			} else {
				caipiao.setStatus(1);
				inserts.add(caipiao);
			}
		}
		if (inserts.size() > 0) {
			session.insert("caipiaoMatch.batchInsertMatch", inserts);
		} else if (updates.size() > 0) {
			for (Caipiao caipiao : updates) {
				session.update("caipiaoMatch.updateMatch", caipiao);
			}
		}
		insertOrUpdateCaipiaoSp(inserts, session);
		insertOrUpdateCaipiaoSp(updates, session);
	}

	private void insertOrUpdateCaipiaoSp(List<Caipiao> list, SqlSession session) {
		List<CaipiaoSp> inserts = new ArrayList<CaipiaoSp>();
		List<CaipiaoSp> updates = new ArrayList<CaipiaoSp>();
		for (Caipiao caipiao : list) {
			CaipiaoSp sp = new CaipiaoSp(caipiao);
			List<CaipiaoSp> exists = session.selectList("caipiaoMatch.selectMatchSp", sp);
			if (exists.size() > 0) {
				sp.setMatchSpId(exists.get(0).getMatchSpId());
				updates.add(sp);
			} else {
				sp.setStatus(1);
				inserts.add(sp);
			}
		}
		if (inserts.size() > 0) {
			session.insert("caipiaoMatch.batchInsertMatchSp", inserts);
		} else if (updates.size() > 0) {
			for (CaipiaoSp caipiaosp : updates) {
				session.update("caipiaoMatch.updateMatchSp", caipiaosp);
			}
		}
	}

	public static void main(String[] args) {
		String exporterName = "check";
		// 4.1如果还没有建立数据库连接
		if (!factoryMap.containsKey(exporterName)) {
			try {
				synchronized (factoryMap) {
					if (!factoryMap.containsKey(exporterName)) {
						Reader reader = Resources.getResourceAsReader(mybatisConfig);
						SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
						SqlSessionFactory factory = builder.build(reader);
						factoryMap.put(exporterName, factory);
					}
				}
			} catch (IOException e) {
				LoggerUtil.errorLog.error("[export-mybatis] error happen when vo export to mybatis.", e);
			}
		}
		// 4.2如果已经建立数据库连接
		if (factoryMap.containsKey(exporterName)) {
			SqlSession session = factoryMap.get(exporterName).openSession();
			try {
				Caipiao caipiao = new Caipiao();
				caipiao.setDataFrom(DataFrom.SPORTTERY);
				List<Caipiao> exists = session.selectList("caipiaoMatch.selectMatch", caipiao);
				for (Caipiao c : exists) {
					String url = "http://i.sporttery.cn/api/fb_match_info/get_result_his?is_ha=all&mid="
							+ c.getMatchId() + "&f_callback=getResultHistoryInfo&_=" + System.currentTimeMillis();
					Map historyInfomMap = (Map) JSONObject.fromObject(UnicodeUtils.unicode2String(HttpUtils
							.sendGet(url).replace("getResultHistoryInfo(", "").replace(");", "")));
					Map result = (Map) historyInfomMap.get("result");
					Map total = (Map) result.get("total");
					System.out.println(c.getHomeTeamName() + "-" + c.getAwayTeamName() + ":历史交锋："
							+ total.get("win") + "胜" + total.get("draw") + "平" + total.get("lose") + "负");
				}
			} catch (Exception e) {
				LoggerUtil.errorLog.error("[export-mybatis] error happen when vo export to mybatis.", e);
			} finally {
				session.commit();
				session.close();
			}
		}
	}
}
