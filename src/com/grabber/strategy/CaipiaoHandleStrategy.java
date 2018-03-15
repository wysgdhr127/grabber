package com.grabber.strategy;

import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.grabber.constant.CommonConstant;
import com.grabber.constant.SportteryConstant;
import com.grabber.model.BaseModel;
import com.grabber.model.caipiao.Caipiao;
import com.grabber.model.caipiao.DataFrom;
import com.grabber.util.HttpUtils;
import com.grabber.util.JacksonUtil;
import com.grabber.util.LoggerUtil;
import com.grabber.util.UnicodeUtils;
import com.netease.common.util.DateUtil;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

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

	protected String sendMessage() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append(SportteryConstant.SPORTTERY_REQUEST_PART1).append("&");
		for (String poolCode : SportteryConstant.getPoolCodeList()) {
			buffer.append(SportteryConstant.SPORTTERY_REQUEST_PART2).append(poolCode).append("&");
		}
		buffer.append("_=" + System.currentTimeMillis());
		return HttpUtils.sendGet(SportteryConstant.SPORTTERY_INFO_URL + buffer.toString());
	}

	private void handleMessage(String responseMessage) {
		responseMessage = UnicodeUtils.unicode2String(responseMessage.replace("getData(", "").replace(");", ""));
		Map responseMap = (Map) JacksonUtil.json2map(responseMessage);
		List<Match> matchList = new ArrayList<>();
		for (Object object : ((LinkedHashMap) responseMap.get("data")).values()) {
			Map<String, Object> map = (Map<String, Object>) object;
			Caipiao caipiao = new Caipiao();
			String num = ((String) map.get("num"));
			String date = ((String) map.get("date")).replace("-", "");
		}
	}

	@SuppressWarnings("unchecked")
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
			} catch (Exception e) {
				LoggerUtil.errorLog.error("[export-mybatis] error happen when vo export to mybatis.", e);
			} finally {
				session.commit();
				session.close();
			}
		}
	}

	public static void main(String[] args) {
		CaipiaoHandleStrategy strategy = new CaipiaoHandleStrategy();
		try {
			System.out.println(strategy.sendMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
