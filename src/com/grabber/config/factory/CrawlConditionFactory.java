package com.grabber.config.factory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;

import com.grabber.config.CrawlerCondition;
import com.grabber.constant.CommonConstant;

/**
 * CrawlCondition工厂，根据CrawlCondition配置文件名（去除后缀，全部小写）得到相应的CrawlCondition
 * 
 * @author WY
 */
public class CrawlConditionFactory {

	private static Map<String, CrawlerCondition> crawlerConditionMap = new HashMap<String, CrawlerCondition>();
	private static CrawlConditionFactory instance = new CrawlConditionFactory();

	private CrawlConditionFactory() {

		init();
	}

	public static CrawlConditionFactory getInstance() {

		if (instance == null) {
			synchronized (CrawlConditionFactory.class) {
				if (instance == null) {
					instance = new CrawlConditionFactory();
					init();
				}
			}
		}
		return instance;
	}

	/**
	 * 初始化：从CrawlerCondition配置文件的目录中读取所有的CrawlCondition配置，并实例化
	 */
	private static void init() {

		String base = CommonConstant.CRAWLER_CONDITION_DIR;
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
		Digester digester = DigesterFactory.getInstance().getDigester(CommonConstant.CRAWLER_CONDITION_RULE);
		// 3.读取配置文件
		for (String fileName : fileNames) {
			File file = new File(path.getPath() + File.separator + fileName);
			CrawlerCondition model = null;
			try {
				// 3.1 解析Controller配置文件并实例化Controller
				model = (CrawlerCondition) digester.parse(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 3.2 配置文件的文件名，变为全小写，作为key，value为model
			if (model != null) {
				crawlerConditionMap.put(fileName.split("\\.")[0].toLowerCase(), model);
			}
		}
	}

	public CrawlerCondition getCrawlerCondition(String modelName) {

		if (crawlerConditionMap.containsKey(modelName)) {
			return crawlerConditionMap.get(modelName);
		} else {
			init();// 初始化
			if (crawlerConditionMap.containsKey(modelName)) {
				return crawlerConditionMap.get(modelName);
			}
		}
		return null;
	}

}
