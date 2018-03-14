package com.grabber.config.factory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grabber.config.Entrance;
import com.grabber.constant.CommonConstant;
import com.grabber.util.FileUtil;

/**
 * entrance工厂，根据entrance配置文件名（去除后缀，全部小写）得到相应的entrance
 * 
 * @author WY 14 Mar 2018 16:10:31
 */
public class EntranceFactory {

	private static Map<String, Entrance> entranceMap = new HashMap<String, Entrance>();
	private static EntranceFactory instance = new EntranceFactory();

	private EntranceFactory() {

		init();
	}

	public static EntranceFactory getInstance() {

		if (instance == null) {
			synchronized (EntranceFactory.class) {
				if (instance == null) {
					instance = new EntranceFactory();
					init();
				}
			}
		}
		return instance;
	}

	/**
	 * 初始化：从entrance配置文件的目录中读取所有的entrance配置，并实例化
	 */
	private static void init() {

		String base = CommonConstant.ENTRANCE_DIR;
		File path = new File(base);
		// 1.得到所有以.conf结尾的文件
		String[] fileNames = path.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				String filename = new File(name).getName();
				return filename.lastIndexOf(".conf") != -1;
			}
		});

		for (String fileName : fileNames) {

			File file = new File(path.getPath() + File.separator + fileName);
			Entrance entrance = null;
			try {
				String content = FileUtil.readFile(file.getAbsolutePath(), "utf-8");
				if (content != null) {
					HashMap<String, List<String>> property = new HashMap<String, List<String>>();
					entrance = new Entrance();
					String subContent[] = content.split("\n");

					if (subContent != null && subContent.length >= 3) {
						String spilter = subContent[0];
						String keys[] = subContent[1].split(spilter);

						for (int i = 0; i < keys.length; i++) {
							property.put(keys[i], new ArrayList<String>());
						}

						for (int i = 2; i < subContent.length; i++) {
							String values[] = subContent[i].split(spilter);
							if (values.length == keys.length) {
								for (int j = 0; j < values.length; j++) {
									property.get(keys[j]).add(values[j]);
								}
							}
						}
					}
					entrance.setProperty(property);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// 3.2 配置文件的文件名，变为全小写，作为key，value为model
			if (entrance != null) {
				entranceMap.put(fileName.split("\\.")[0].toLowerCase(), entrance);
			}
		}
	}

	public Entrance getEntarnce(String entranceFile) {

		if (entranceMap.containsKey(entranceFile)) {
			return entranceMap.get(entranceFile);
		} else {
			init();// 初始化后再次尝试
			if (entranceMap.containsKey(entranceFile)) {
				return entranceMap.get(entranceFile);
			}
		}
		return null;
	}

}
