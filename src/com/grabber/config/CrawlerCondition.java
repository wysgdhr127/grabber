package com.grabber.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import com.grabber.constant.CommonConstant;
import com.grabber.util.LoggerUtil;
import com.grabber.util.ParserUtil;

/**
 * 爬虫抓取条件
 * 
 * @author WY
 */
public class CrawlerCondition {
	
	private List<FilterCondition> filterConditionList; // 筛选条件的列表
	private List<BeginCrawlCondition> beginCrawlConditionList; // 开始抓取的条件
	private String beginCrawlConditionRelation; // 开始抓取条件的关系
	private List<NextPageCondition> nextPageConditionList; // 下一页的条件的列表
	
	public CrawlerCondition() {

		filterConditionList = new ArrayList<FilterCondition>();
		beginCrawlConditionList = new ArrayList<BeginCrawlCondition>();
		nextPageConditionList = new ArrayList<NextPageCondition>();
	}
	
	/**
	 * 得到第index个条件的url list
	 * 
	 * @param root
	 * @param index
	 * @return
	 */
	public List<String> getUrlByFilterConditionAndIndex(TagNode root, int index) {

		List<String> result = new ArrayList<String>();
		try {
			Object[] attrNodes = root.evaluateXPath(filterConditionList.get(index).getXpath());
			if (attrNodes == null || attrNodes.length == 0) {
				return result;
			}
			String attrValue = ParserUtil.getTargetFromTagNode(attrNodes);
			String[] data = attrValue.split(CommonConstant.REGEX_RESULT_SPLITOR);
			for (String url : data) {
				result.add(url);
			}
			LoggerUtil.debugLog.info("[crawler-condition] get url by condition [index=" + index + "][resultSize = " + result.size() + "]");
		} catch (XPatherException e) {
			LoggerUtil.errorLog.error("[crawler-condition] error happen when get url by condition.", e);
		}
		return result;
	}
	
	/**
	 * 判断一个网页是否应该被解析，返回true则是，false则应该进一步往下走 详细：比较各个条件的or and关系，并将最后的关系返回
	 */
	public boolean canBeginCrawl(TagNode root) {

		if (beginCrawlConditionList == null || beginCrawlConditionList.size() == 0) {
			return true;
		}
		
		Object[] nodes = null;
		String conditionResult = null;
		Map<String,Boolean> index2ConditionResultMap = new HashMap<String,Boolean>();
		for (BeginCrawlCondition beginCrawlCondition : beginCrawlConditionList) {
			String xpath = beginCrawlCondition.getXpath();
			String index = beginCrawlCondition.getIndex();
			String type = beginCrawlCondition.getType();
			String goal = beginCrawlCondition.getGoal();
			String canBeNull = beginCrawlCondition.getCanBeNull();// xpath没有找到是否条件成立
			String comparator = beginCrawlCondition.getComparator();
			try {
				nodes = root.evaluateXPath(xpath);
				// 如果xpath没有找到，则根据配置文件来决定此条件是否为true
				if (nodes == null || nodes.length == 0) {
					if (canBeNull.equals("true")) {
						index2ConditionResultMap.put(index, true);
					} else {
						index2ConditionResultMap.put(index, false);
					}
				}
				for (Object node : nodes) {
					if (node instanceof TagNode) {
						conditionResult = ((TagNode) node).getText().toString();
					} else if (node instanceof String) {
						conditionResult = (String) node;
					}
				}
				if (StringUtils.isNotBlank(conditionResult)) {
					if (StringUtils.isNotBlank(type) && (type.equals("int") || type.equals("Integer"))) {
						try {
							int goalInt = Integer.parseInt(goal);
							int resultInt = Integer.parseInt(conditionResult);
							if (comparator.equals("<")) {
								index2ConditionResultMap.put(index, resultInt < goalInt);
							} else if (comparator.equals("<=")) {
								index2ConditionResultMap.put(index, resultInt <= goalInt);
							} else if (comparator.equals(">=")) {
								index2ConditionResultMap.put(index, resultInt >= goalInt);
							} else if (comparator.equals(">")) {
								index2ConditionResultMap.put(index, resultInt > goalInt);
							} else if (comparator.equals("=")) {
								index2ConditionResultMap.put(index, resultInt == goalInt);
							} else {
								index2ConditionResultMap.put(index, true);
							}
						} catch (Exception e) {
							LoggerUtil.errorLog.error("[canBeginCrawl]error happen in canBeginCrawl", e);
							index2ConditionResultMap.put(index, true);
						}
					} else {
						if (goal.equals(conditionResult)) {
							index2ConditionResultMap.put(index, true);
						} else {
							index2ConditionResultMap.put(index, false);
						}
					}
				}
			} catch (Exception e) {
				LoggerUtil.errorLog.error("[canBeginCrawl]error happen in canBeginCrawl", e);
				index2ConditionResultMap.put(index, true);
			}
		}
		
		return checkBeginCrawlConditionRelation(beginCrawlConditionRelation, index2ConditionResultMap);
	}
	
	/**
	 * 利用关系式计算出true或false的结果
	 */
	private static boolean checkBeginCrawlConditionRelation(String beginCrawlConditionRelation, Map<String,Boolean> index2ConditionResultMap) {

		index2ConditionResultMap.put("-1", false);
		index2ConditionResultMap.put("-2", true);
		Stack<String> reversePolishNotationStack = getReversePolishNotation(beginCrawlConditionRelation);
		Stack<String> computeStack = new Stack<String>();
		for (int i = 0; i < reversePolishNotationStack.size(); ++i) {
			String tempString = reversePolishNotationStack.get(i);
			if (StringUtils.isNumeric(tempString)) {
				computeStack.push(tempString);
			} else {
				String o1 = computeStack.pop();
				String o2 = computeStack.pop();
				boolean t1 = index2ConditionResultMap.get(o1);
				boolean t2 = index2ConditionResultMap.get(o2);
				if (tempString.equals("|")) {
					computeStack.push(t1 || t2 ? "-2" : "-1");
				} else {
					computeStack.push(t1 && t2 ? "-2" : "-1");
				}
			}
		}
		return index2ConditionResultMap.get(computeStack.pop());
	}
	
	/**
	 * 得到表达式的逆波兰式 true或false的逆波兰表达式要注意两点：①最前面的&或者|应该优先级最高②碰到）时，应该两次清栈
	 * 
	 * @param beginCrawlConditionRelation
	 */
	private static Stack<String> getReversePolishNotation(String beginCrawlConditionRelation) {

		// 1.运算符栈
		Stack<String> operatorStack = new Stack<String>();
		// 2.逆波兰式栈
		Stack<String> resultStack = new Stack<String>();
		for (int i = 0; i < beginCrawlConditionRelation.length(); ++i) {
			char c = beginCrawlConditionRelation.charAt(i);
			if (c == '|' || c == '&') {
				Stack<String> tempStack = new Stack<String>();
				while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
					tempStack.add(operatorStack.pop());
				}
				operatorStack.push(String.valueOf(c));
				while (!tempStack.isEmpty()) {
					operatorStack.push(tempStack.pop());
				}
				if (i < beginCrawlConditionRelation.length() - 1 && (beginCrawlConditionRelation.charAt(i + 1) == '|' || beginCrawlConditionRelation.charAt(i + 1) == '&')) {
					++i;
				}
			} else if (c >= '0' && c <= '9') {
				int j = i;
				int n = c - '0';
				while (i < beginCrawlConditionRelation.length() - 1 && beginCrawlConditionRelation.charAt(i + 1) >= '0' && beginCrawlConditionRelation.charAt(i + 1) <= '9') {
					n *= 10;
					n += beginCrawlConditionRelation.charAt(i + 1) - '0';
					i++;
				}
				resultStack.push(String.valueOf(n));
				if (j > 0 && (beginCrawlConditionRelation.charAt(j - 1) == '|' || beginCrawlConditionRelation.charAt(j - 1) == '&')) {
					resultStack.push(operatorStack.pop());
				}
			} else if (c == '(') {
				operatorStack.push(String.valueOf(c));
			} else if (c == ')') {
				while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
					resultStack.push(operatorStack.pop());
				}
				if (!operatorStack.isEmpty()) {
					operatorStack.pop();
				}
				while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
					resultStack.push(operatorStack.pop());
				}
				if (!operatorStack.isEmpty()) {
					operatorStack.pop();
				}
			}
		}
		while (!operatorStack.isEmpty()) {
			resultStack.push(operatorStack.pop());
		}
		return resultStack;
	}
	
	public void addFilterCondition(FilterCondition filterCondition) {

		filterConditionList.add(filterCondition);
	}
	
	public void addBeginCrawlCondition(BeginCrawlCondition beginCrawlCondition) {

		beginCrawlConditionList.add(beginCrawlCondition);
	}
	
	public void addNextPageCondition(NextPageCondition nextPageCondition) {

		nextPageConditionList.add(nextPageCondition);
	}
	
	public List<FilterCondition> getFilterConditionList() {

		return filterConditionList;
	}
	
	public void setFilterConditionList(List<FilterCondition> filterConditionList) {

		this.filterConditionList = filterConditionList;
	}
	
	public List<BeginCrawlCondition> getBeginCrawlConditionList() {

		return beginCrawlConditionList;
	}
	
	public void setBeginCrawlConditionList(List<BeginCrawlCondition> beginCrawlConditionList) {

		this.beginCrawlConditionList = beginCrawlConditionList;
	}
	
	public String getBeginCrawlConditionRelation() {

		return beginCrawlConditionRelation;
	}
	
	public void setBeginCrawlConditionRelation(String beginCrawlConditionRelation) {

		this.beginCrawlConditionRelation = beginCrawlConditionRelation;
	}
	
	public List<NextPageCondition> getNextPageConditionList() {

		return nextPageConditionList;
	}
	
	public void setNextPageConditionList(List<NextPageCondition> nextPageConditionList) {

		this.nextPageConditionList = nextPageConditionList;
	}
	
	public static void main(String[] args) {

		// CrawlerCondition crawlerCondition =
		// CrawlConditionFactory.getInstance().getCrawlerCondition("qqmeishi_crawler");
		// crawlerCondition.getUrlByFilterConditionAndIndex(Parser.getTagNodeForUrl("http://meishi.qq.com/beijing/s/c100-s11001",
		// null, Capturer.GET_METHOD, null), 0);
		// TagNode root =
		// Parser.getTagNodeForUrl("http://meishi.qq.com/beijing/s/c100-d110114-s11001-u21003",
		// null, Capturer.GET_METHOD, null);
		// System.out.println(crawlerCondition.canBeginCrawl(root));
	}
	
}
