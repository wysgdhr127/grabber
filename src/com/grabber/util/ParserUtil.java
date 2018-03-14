package com.grabber.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import com.grabber.config.Attribute;
import com.grabber.config.Entrance;
import com.grabber.config.Mission;
import com.grabber.config.Model;
import com.grabber.config.NextPageCondition;
import com.grabber.config.factory.CrawlConditionFactory;
import com.grabber.config.factory.EntranceFactory;
import com.grabber.config.factory.ModelFactory;
import com.grabber.constant.CommonConstant;
import com.grabber.constant.VariableConstants;
import com.grabber.model.BaseModel;
import com.grabber.model.ErrorUnit;
import com.netease.common.util.FileUtil;

/**
 * 使用htmlcleaner与配置的页面解析类 使用配置：Controller与Model 。Controller负责控制：读取入口、翻页、后处理
 * Model负责解析页面的内容，并将值写入对象
 * 
 * @author WY 14 Mar 2018 16:52:47
 */

public class ParserUtil {

	/**
	 * 根据“下一页”的xpath翻页，直到最后一页
	 * 
	 * @param domain
	 * @param url
	 * @param params
	 * @param method
	 * @param model
	 * @param encode
	 * @param nextPageXpath
	 * @return
	 */
	public static List<BaseModel> parsePagesByNextPageXpath(String url,
			String params, Mission mission, int entranceIndex) {

		List<BaseModel> result = new ArrayList<BaseModel>();
		Set<String> set = new HashSet<String>();
		do {
			set.add(url);
			TagNode root = getTagNodeForUrl(url, params, mission.getCapturer());
			List<NextPageCondition> nextPageConditionList = CrawlConditionFactory
					.getInstance()
					.getCrawlerCondition(mission.getCrawlConditionName())
					.getNextPageConditionList();
			Model model = ModelFactory.getInstance().getModel(
					mission.getModelName());
			List<BaseModel> subResult = parseSinglePage(url, root, model,
					mission, entranceIndex);
			if (subResult != null && !subResult.isEmpty()) {
				result.addAll(subResult);
			}
			// 对多个下一页的条件，直到找到为止
			for (NextPageCondition nextPageCondition : nextPageConditionList) {
				url = getValueByXpathOnlyOne(nextPageCondition.getXpath(), root);
				if (StringUtils.isNotBlank(url)) {
					break;
				}

			}
			// 如果找到空url则直接退出
			if (StringUtils.isNotBlank(url)) {
				url = ProxyUtil.getRealUrl(mission.getDomain(), url);
				// 如果已经存在，则有可能会构成死循环，强制退出
				if (set.contains(url)) {
					break;
				} else {
					set.add(url);
				}
			} else {
				break;
			}
		} while (true);
		return result;
	}

	/**
	 * 根据htmlcleaner与model得到最终的对象的列表
	 * 
	 * @param url
	 * 
	 * @param root
	 *            要解析的url对应的xml的跟节点TagNode
	 * @param model
	 *            描述页面结构与对象属性对应关系的对象
	 * @param propertyNames
	 *            需要往每个对象里面反射的属性名数组
	 * @param propertyValues
	 *            需要往每个对象里面反射的属性值数组
	 * @return 所需的对象的列表
	 */
	@SuppressWarnings("unchecked")
	public static List<BaseModel> parseSinglePage(String url, TagNode root,
			Model model, Mission mission, int entranceIndex) {
		Entrance entrance = EntranceFactory.getInstance().getEntarnce(
				mission.getEntranceName());
		String[] propertyNames = entrance.getKeys();
		String[] propertyValues = entrance.getValuesByIndex(entranceIndex);

		List<BaseModel> list = new ArrayList<BaseModel>();
		try {
			if (root == null) {
				LoggerUtil.debugLog
						.error("[parser-parseSinglePage] Root Node is null");
				return null;
			}
			String modelXpath = model.getModelXpath();
			String modelClass = model.getModelClass();
			String subModelClass = model.getSubModelClass();
			String subModelName = model.getSubModelName();
			String subModelMethodName = model.getSubModelMethodName();
			// 1.得到model所在的路径
			Object[] nodes = root.evaluateXPath(modelXpath);
			if (nodes == null || nodes.length == 0) {
				return list;
			}
			// 1.2是否有model子类
			boolean hasSubModel = false;
			if (StringUtils.isNotBlank(subModelClass)
					&& StringUtils.isNotBlank(subModelName)
					&& StringUtils.isNotBlank(subModelMethodName)) {
				try {
					// 1.2.1判断是否有此类
					Class<?> clazz = Class.forName(subModelClass);
					// 1.2.2判断是否有此方法
					clazz = Class.forName(modelClass);
					Method[] methods = clazz.getDeclaredMethods();
					for (Method method : methods) {
						if (method.getName().equals(subModelMethodName)) {
							hasSubModel = true;
							break;
						}
					}
					// 1.2.3判断是否有此model
					if (ModelFactory.getInstance().getModel(subModelName) == null) {
						hasSubModel = false;
					}
				} catch (Exception e) {
					LoggerUtil.errorLog
							.error("[parser-parseSinglePage] error happen in parseSinglePage()",
									e);
					hasSubModel = false;
				}
			}
			// 2.1得到Model的类型
			Class<?> clazz = Class.forName(modelClass);
			Model subModel = ModelFactory.getInstance().getModel(subModelName);
			// 2.对每一个model的节点
			for (Object n : nodes) {

				List<Attribute> attributes = model.getAttributes();
				// 2.2生成一个T类型的对象
				BaseModel obj = (BaseModel) clazz.newInstance();
				// -----------新增开始----------------
				// 2.0看是否有子类
				if (hasSubModel) {
					// 2.0.1有子类，得到其中的子类数据
					List<?> subResult = parseSinglePage(url, (TagNode) n,
							subModel, mission, entranceIndex);
					// 2.0.2反射插入数据
					for (Object param : subResult) {
						ReflectUtil
								.invokeMethod(obj, subModelMethodName, param);
					}
				}

				// -----------新增结束----------------
				// 2.3将各个属性的值设置到对象中
				for (Attribute a : attributes) {
					String attr = a.getAttr();// 属性
					// 2.3.1得到此属性的xpath
					String xpath = a.getXpath();

					String attrValue = handleVariableAttributes(url, root,
							model, mission, xpath);
					if (attrValue == null) {
						// 2.3.2根据xpath得到在value中的对应的值
						String[] xpathes = xpath.split("\\|");
						Object[] attrNodes = null;

						for (String xp : xpathes) {
							if (xp.startsWith(VariableConstants.ROOT_TAG)) {
								attrNodes = root.evaluateXPath(xp.trim()
										.substring(
												VariableConstants.ROOT_TAG
														.length()));
							} else {
								attrNodes = ((TagNode) n).evaluateXPath(xp
										.trim());
							}
							if (attrNodes != null && attrNodes.length > 0) {
								break;
							}
						}

						// 2.3.3 如果根据xpath失效了，则报警
						if (attrNodes == null || attrNodes.length == 0) {
							LoggerUtil.debugLog.error("can not parse xpath: "
									+ xpath + " in model: "
									+ model.getModelClass());
							ErrorUtil.put(new ErrorUnit(mission,
									"can not parse xpath: " + xpath
											+ " in model: "
											+ model.getModelClass(), null));
							continue;
						}
						// 2.3.4从TagNode中得到目标属性
						attrValue = getTargetFromTagNode(attrNodes);
						// 2.3.5看是否有正则表达式过滤
						String regex = a.getRegex();
						String regexIndex = a.getRegexIndex();
						attrValue = CommonUtil.runRegex(regex, attrValue,
								regexIndex);
					}

					// 2.3.6将此属性设值进入对象中
					ReflectUtil.invokeSet(obj, attr, attrValue);

				}
				// 2.4对每个对象都进行反射设置值
				if (propertyNames != null && propertyValues != null
						&& propertyNames.length == propertyValues.length) {
					for (int i = 0; i < propertyNames.length; ++i) {
						if (propertyNames[i] != null
								&& propertyValues[i] != null) {
							try {
								ReflectUtil.invokeSet(obj, propertyNames[i],
										propertyValues[i]);
							} catch (Exception e) {
								LoggerUtil.errorLog
										.error("[export-parseSinglePage] error happen when reflect default value.[propertyNames = "
												+ propertyNames[i]
												+ "][propertyValues = "
												+ propertyValues[i] + "]", e);
							}
						}
					}
				}
				// 2.5将此对象放入列表中
				list.add(obj);
			}
		} catch (Exception e) {
			LoggerUtil.errorLog
					.error("[export-parseSinglePage] error happen in parseSinglePage.",
							e);
			ErrorUtil.put(new ErrorUnit(mission,
					"[export-parseSinglePage] error happen in parseSinglePage."
							+ model.getModelClass(), e));
		}
		return list;
	}

	private static String handleVariableAttributes(String url, TagNode root,
			Model model, Mission mission, String xpath) {
		if (VariableConstants.CURRENT_URL.equals(xpath)) {
			return url;
		}
		return null;
	}

	/**
	 * 根据url使用htmlcleaner得到该html对应的TagNode
	 * 
	 * @param url
	 *            要解析的url的首页
	 * @param params
	 *            url的参数
	 * @param method
	 *            两种选择，Crawler.GET_METHOD Crawler.POST_METHOD
	 * @param encode
	 *            编码 页面的编码格式，null为默认按照页面的编码，传入encode则将html转码
	 * @param needProxy
	 *            是否需要设置代理
	 */
	public static TagNode getTagNodeForUrl(String url, String params,
			CapturerUtil capturer) {

		// HtmlCleaner的Clean属性
		CleanerProperties props = new CleanerProperties();
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		String html = capturer.getCiteContent(url, params);
		return getTagNodeByHtml(html);
	}

	/**
	 * 根据filePath生成对应的TagNode
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public static TagNode getTagNodeFromFile(String filePath) {

		// HtmlCleaner的Clean属性
		CleanerProperties props = new CleanerProperties();
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		try {
			String file = FileUtil.read(filePath);
			return getTagNodeByHtml(file);
		} catch (Exception e) {
			LoggerUtil.errorLog.error(
					"[export-getTagNodeFromFile] error happen in getTagNodeForUrl.[filePath = "
							+ filePath + "]", e);
		}
		return null;
	}

	/**
	 * 将html转为TagNode
	 * 
	 * @param html
	 * @return
	 */
	public static TagNode getTagNodeByHtml(String html) {

		if (html != null) {
			CleanerProperties props = new CleanerProperties();
			props.setTranslateSpecialEntities(true);
			props.setTransResCharsToNCR(true);
			props.setOmitComments(true);
			HtmlCleaner htmlCleaner = new HtmlCleaner(props);
			TagNode node = htmlCleaner.clean(html);
			return node;
		} else {
			return null;
		}

	}

	/**
	 * 从TagNode属性中读取目标属性
	 * 
	 * @param nodes
	 */
	public static String getTargetFromTagNode(Object[] nodes) {

		StringBuffer attrValueBuffer = new StringBuffer();
		for (Object o : nodes) {
			if (o instanceof String) {
				if (StringUtils.isNotBlank((String) o)) {
					String value = (String) o;
					value = value.replaceAll("\r", "").replaceAll("\n", "");// 去除换行符
					attrValueBuffer.append(value);
				}
			} else if (o instanceof TagNode) {
				String value = ((TagNode) o).getText().toString().trim();
				value = value.replaceAll("\r", "").replaceAll("\n", "");// 去除换行符
				if (StringUtils.isNotBlank(value)) {
					attrValueBuffer.append(value);
				}
			}
			if (nodes.length > 1) {
				attrValueBuffer.append(CommonConstant.REGEX_RESULT_SPLITOR);
			}
		}

		// 删除末尾的','及空元素产生的多个连续','
		return attrValueBuffer
				.toString()
				.replaceAll(CommonConstant.REGEX_RESULT_SPLITOR + "+",
						CommonConstant.REGEX_RESULT_SPLITOR)
				.replaceAll(CommonConstant.REGEX_RESULT_SPLITOR + "$", "");
	}

	/**
	 * 根据xpath得到目标值
	 * 
	 * @param xpath
	 * @param root
	 * @return
	 */
	public static String getValueByXpath(String xpath, TagNode root) {

		Object[] nodes = null;
		try {
			nodes = root.evaluateXPath(xpath);
		} catch (XPatherException e) {
			LoggerUtil.errorLog.error(
					"[export-getValueByXpath] error happen in getValueByXpath.[xpath = "
							+ xpath + "]", e);
		}
		if (nodes == null || nodes.length == 0) {
			return null;
		} else {
			return getTargetFromTagNode(nodes);
		}
	}

	/**
	 * 根据xpath得到目标值，只取第一个数值，其余的忽略
	 * 
	 * @param xpath
	 * @param root
	 * @return
	 */
	public static String getValueByXpathOnlyOne(String xpath, TagNode root) {

		Object[] nodes = null;
		try {
			nodes = root.evaluateXPath(xpath);
		} catch (XPatherException e) {
			LoggerUtil.errorLog.error(
					"[export-getValueByXpath] error happen in getValueByXpath.[xpath = "
							+ xpath + "]", e);
		}
		if (nodes == null || nodes.length == 0) {
			return null;
		} else {
			return getTargetFromTagNode(new Object[] { nodes[0] });
		}
	}

}
