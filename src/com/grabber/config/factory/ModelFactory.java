package com.grabber.config.factory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;

import com.grabber.config.Model;
import com.grabber.constant.CommonConstant;

/**
 * Model工厂，根据Model配置文件名（去除后缀，全部小写）得到相应的Model
 * 
 * @author WY
 */

public class ModelFactory {

	private static Map<String, Model> modelMap = new HashMap<String, Model>();
	private static ModelFactory instance = new ModelFactory();

	private ModelFactory() {

		init();
	}

	public static ModelFactory getInstance() {

		if (instance == null) {
			synchronized (ModelFactory.class) {
				if (instance == null) {
					instance = new ModelFactory();
					init();
				}
			}
		}
		return instance;
	}

	/**
	 * 初始化：从Model配置文件的目录中读取所有的Model配置，并实例化
	 */
	private static void init() {

		String base = CommonConstant.MODEL_DIR;
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
		Digester digester = DigesterFactory.getInstance().getDigester(CommonConstant.MODEL_RULE);
		// 3.读取配置文件
		for (String fileName : fileNames) {
			File file = new File(path.getPath() + File.separator + fileName);
			Model model = null;
			try {
				// 3.1 解析Controller配置文件并实例化Controller
				model = (Model) digester.parse(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 3.2 配置文件的文件名，变为全小写，作为key，value为model
			if (model != null) {
				modelMap.put(fileName.split("\\.")[0].toLowerCase(), model);
			}
		}
	}

	public Model getModel(String modelName) {

		if (modelMap.containsKey(modelName)) {
			return modelMap.get(modelName);
		} else {
			init();// 初始化
			if (modelMap.containsKey(modelName)) {
				return modelMap.get(modelName);
			}
		}
		return null;
	}

	public static void main(String[] args) {

		Model c = ModelFactory.getInstance().getModel("mtime_awards_model");
		System.out.println(c.getSubModelClass());
	}
}
