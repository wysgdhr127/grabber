package com.grabber.config.factory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringUtils;

import com.grabber.config.Mission;
import com.grabber.constant.CommonConstant;
import com.grabber.strategy.HandleStrategy;
import com.grabber.util.CapturerUtil;
import com.grabber.util.LoggerUtil;

/**
 * Missions工厂，根据Missions配置文件名（去除后缀，全部小写）得到相应的Missions
 * 
 * @author WY
 */
public class MissionFactory {

	private static Map<String, Mission> missionMap = new HashMap<String, Mission>();
	private static MissionFactory instance = new MissionFactory();

	private MissionFactory() {

		init();
	}

	public static MissionFactory getInstance() {

		if (instance == null) {
			synchronized (MissionFactory.class) {
				if (instance == null) {
					instance = new MissionFactory();
					init();
				}
			}
		}
		return instance;
	}

	/**
	 * 初始化：从Missions配置文件的目录中读取所有的Missions配置，并实例化
	 */
	private static void init() {

		HashMap<String, String> missionHashMap = new HashMap<String, String>();
		String base = CommonConstant.MISSIONS_DIR;
		File path = new File(base);
		// 1.得到所有以.xml结尾的文件
		String[] fileNames = path.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				String filename = new File(name).getName();
				return filename.lastIndexOf(".xml") != -1;
			}
		});
		// 2.得到Digester实例
		Digester digester = DigesterFactory.getInstance().getDigester(CommonConstant.MISSIONS_RULE);
		// 3.读取配置文件
		for (String fileName : fileNames) {
			File file = new File(path.getPath() + File.separator + fileName);
			Mission mission = null;
			try {
				// 3.1 解析Controller配置文件并实例化Controller
				mission = (Mission) digester.parse(file);

				// 3.2 对mission的合法性进行检查
				String missionName = mission.getMissionName();
				if (StringUtils.isBlank(missionName)) {
					LoggerUtil.errorLog.error("[mission - init] missing the missionName, please check conf.[file="
							+ fileName + "]");
					continue;
				} else {
					if (missionHashMap.containsKey(missionName)) {
						LoggerUtil.errorLog
								.error("[mission - init] missionName is duplicate, please check conf.[file1 ="
										+ fileName + "][file2 = " + missionHashMap.get(missionName) + "]");
						continue;
					} else {
						missionHashMap.put(missionName, fileName);
					}
				}

				// 3.3 设置后处理类
				Class<?> clazz = Class.forName(mission.getStrategyName());
				mission.setHandleStrategy((HandleStrategy) clazz.newInstance());

				// 3.4 设置页面获取器
				mission.setCapturer(new CapturerUtil(mission));

			} catch (Exception e) {
				LoggerUtil.errorLog.error("Error happen when init mission , cann't get the HandleStrategy class.", e);
			}
			// 3.3 配置文件的文件名，变为全小写，作为key，value为model
			if (mission != null) {
				missionMap.put(fileName.split("\\.")[0].toLowerCase(), mission);
			}
		}
	}

	public Mission getMission(String missionFile) {
		if (missionMap.containsKey(missionFile)) {
			return missionMap.get(missionFile);
		} else {
			return null;
		}
	}
}
