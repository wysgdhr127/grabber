package com.grabber.config.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.InputSource;

import com.grabber.constant.CommonConstant;
import com.grabber.util.LoggerUtil;
import com.grabber.util.StreamUtil;

/**
 * Digester工厂类
 * 
 * @author WY
 */
public class DigesterFactory {

	private static DigesterFactory instance;
	private static Map<String, String> inputSourceMap = new HashMap<String, String>();

	private DigesterFactory() {

	}

	public static DigesterFactory getInstance() {

		if (instance == null) {
			synchronized (DigesterFactory.class) {
				if (instance == null) {
					instance = new DigesterFactory();
					init();
				}
			}
		}
		return instance;
	}

	/**
	 * 根据className得到Digester，首先得到
	 */
	public Digester getDigester(String ruleName) {

		ruleName = ruleName.toLowerCase();
		if (inputSourceMap.containsKey(ruleName)) {
			// rule为配置文件的内容
			String rule = inputSourceMap.get(ruleName);
			InputSource is = null;
			try {
				is = new InputSource(StreamUtil.stringTOInputStream(rule, CommonConstant.ENCODING_DEFAULT));
			} catch (Exception e) {
				LoggerUtil.errorLog.error("[getDigester] error happen in getDigester.", e);
			}
			// 得到Digester的实例
			Digester d = DigesterLoader.createDigester(is);
			return d;
		}
		return null;
	}

	/**
	 * 读取xml配置文件，初始化Digester
	 */
	public static void init() {

		String base = CommonConstant.RULE_DIR;
		File path = new File(base);
		String[] fileNames = path.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				String filename = new File(name).getName();
				return filename.lastIndexOf(".xml") != -1;
			}
		});
		// 读取配置文件
		for (String fileName : fileNames) {
			File file = new File(path.getPath() + File.separator + fileName);
			InputStream is = null;
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			String content = "";
			try {
				content = StreamUtil.inputStreamTOString(is, CommonConstant.ENCODING_DEFAULT);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 配置文件的文件名，变为全小写，作为key，value为文件的内容
			inputSourceMap.put(fileName.split("\\.")[0].toLowerCase(), content);
		}

	}

}
