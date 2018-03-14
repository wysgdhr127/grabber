package com.grabber.thread.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.htmlcleaner.TagNode;

import com.grabber.config.CrawlerCondition;
import com.grabber.config.Entrance;
import com.grabber.config.FilterCondition;
import com.grabber.config.Mission;
import com.grabber.config.factory.CrawlConditionFactory;
import com.grabber.config.factory.EntranceFactory;
import com.grabber.constant.CommonConstant;
import com.grabber.model.ParseUnit;
import com.grabber.thread.ThreadPool;
import com.grabber.util.LoggerUtil;
import com.grabber.util.ParserUtil;
import com.grabber.util.ProxyUtil;

/**
 * @author WY 14 Mar 2018 15:10:31
 */
public class CrawlerThread implements Callable<ArrayList<String>> {
	private Set<String> visitUrlSet = new HashSet<String>();// 访问过的路径

	private BlockingQueue<ParseUnit> waitingParsedUrlQueue;// 等到解析的url

	private Mission mission;

	private int entranceIndex;

	public CrawlerThread(Mission mission, int entranceIndex,
			BlockingQueue<ParseUnit> waitingParsedUrlQueue) {

		this.waitingParsedUrlQueue = waitingParsedUrlQueue;
		this.entranceIndex = entranceIndex;
		this.mission = mission;
	}

	@Override
	public ArrayList<String> call() throws Exception {

		long start = System.currentTimeMillis();
		Entrance entranceUtil = EntranceFactory.getInstance().getEntarnce(
				mission.getEntranceName());
		String entrance = entranceUtil.getEntranceByIndex(entranceIndex);
		LoggerUtil.crawlLog.info("[crawl begin][begin = " + start
				+ "][entrance = " + entrance + "]");

		// 爬虫条件
		CrawlerCondition crawlerCondition = CrawlConditionFactory.getInstance()
				.getCrawlerCondition(mission.getCrawlConditionName());
		List<FilterCondition> filterConditionList = crawlerCondition
				.getFilterConditionList();
		Queue<String> queue = new LinkedList<String>();// 等待爬的url
		Map<String, TagNode> url2TagNodeMap = new HashMap<String, TagNode>(); // url到html的映射

		// 0.植入祖先url
		queue.offer(getFixedUrlTask(entrance, 0, 0));
		LoggerUtil.crawlLog
				.info("[crawl running] put the entrance to queue [entrance = "
						+ entrance + "]");

		while (!queue.isEmpty()) {
			String[] data = queue.poll()
					.split(CommonConstant.SEPARATOR_CRAWLER);
			// 1.从队列中取出一个site
			String site = data[0];
			// 2.将其标记为已经访问过
			visitUrlSet.add(site);
			// 3.得到此site对应的TagNode
			String url = ProxyUtil.getRealUrl(mission.getDomain(), site);

			// 如果不需要多层次遍历
			if (filterConditionList == null || filterConditionList.size() == 0) {
				ParseUnit parseUnit = new ParseUnit(url, entranceIndex);
				try {
					waitingParsedUrlQueue.put(parseUnit);
					LoggerUtil.crawlLog
							.info("[crawl running] Sucess to put parseUnit to waitingParsedUrlQueue,[url="
									+ url + "][entrance = " + entrance + "].");
				} catch (InterruptedException e) {
					LoggerUtil.errorLog
							.error("[crawl running] Error happen when put parseUnit to waitingParsedUrlQueue,[url="
									+ url + "][entrance = " + entrance + "].");
				}
			} else {

				TagNode root = url2TagNodeMap.remove(site);
				if (root == null) {
					root = ParserUtil.getTagNodeForUrl(url, null,
							mission.getCapturer());
				}
				// 4.得到此site的访问条件下标和层数
				int index = Integer.parseInt(data[1]);
				int level = Integer.parseInt(data[2]);
				int depth = Integer.parseInt(filterConditionList.get(index)
						.getDepth());
				// 4.1当前的过滤条件深度如果没有达到最高的深度，则继续往深里爬
				if (level < depth) {
					level++;
				} else {
					// 4.2当前的过滤条件的深度已经达到最大，需要开始下一个过滤条件
					index++;
					level = 0;
				}
				if (index < crawlerCondition.getFilterConditionList().size()) {
					// 5.对此页面下的筛选条件，进行宽度优先遍历
					// 5.1得到第index个筛选条件对应的site列表
					List<String> list = crawlerCondition
							.getUrlByFilterConditionAndIndex(root, index);
					// 5.2对此条件下的每一个页面进行处理：
					for (String subSite : list) {
						// 5.2.1查看其是否已经访问过，如果没有访问过，则判断其是否应该解析，如果不该解析，则应该放入queue
						if (!visitUrlSet.contains(subSite)) {
							String subUrl = ProxyUtil.getRealUrl(
									mission.getDomain(), subSite);
							// 5.2.2得到subUrl对应的TagNode
							TagNode tagNode = ParserUtil.getTagNodeForUrl(
									subUrl, null, mission.getCapturer());
							// 5.2.3如果已经可以开始解析了，则将其放入等待解析的队列中
							if (crawlerCondition.canBeginCrawl(tagNode)) {
								ParseUnit parseUnit = new ParseUnit(subUrl,
										entranceIndex);
								try {
									waitingParsedUrlQueue.put(parseUnit);
									LoggerUtil.crawlLog
											.info("[crawl running] Sucess to put parseUnit to waitingParsedUrlQueue,[url="
													+ subUrl
													+ "][index = "
													+ index
													+ "][level = "
													+ level + "].");
								} catch (InterruptedException e) {
									LoggerUtil.crawlLog
											.error("[crawl running] Error happen when put parseUnit to waitingParsedUrlQueue,[url="
													+ url
													+ "][index = "
													+ index
													+ "][level = "
													+ level + "].", e);
								}
							} else {
								// 5.2.4还不能解析，还需要继续往深处爬
								// 5.2.4.1将tagNode放入map中存放，下次循环将会用到
								url2TagNodeMap.put(subSite, tagNode);
								// 5.2.4.2将此site放入队列中
								queue.offer(getFixedUrlTask(subSite, index,
										level));
								LoggerUtil.crawlLog
										.info("[crawl running] need to crawl deeper,[url="
												+ subUrl
												+ "][index = "
												+ index
												+ "][level = " + level + "].");
							}
						}
					}
				}
			}
		}

		ThreadPool.getInstance().releaseTaskForMission(
				CommonConstant.getCrawlerName(mission.getMissionName()));
		long end = System.currentTimeMillis();
		LoggerUtil.crawlLog.info("[crawl end] : " + entrance + ", costs "
				+ (end - start) + "ms.");
		return null;
	}

	/**
	 * 获取爬虫队列的标准url格式
	 * 
	 * @param url
	 * @param index
	 * @param level
	 * @return
	 */
	private String getFixedUrlTask(String url, int index, int level) {

		return url + CommonConstant.SEPARATOR_CRAWLER + index
				+ CommonConstant.SEPARATOR_CRAWLER + level;
	}
}
