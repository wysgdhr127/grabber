package com.grabber.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import com.grabber.config.Entrance;
import com.grabber.config.Mission;
import com.grabber.config.factory.EntranceFactory;
import com.grabber.config.factory.MissionFactory;
import com.grabber.constant.CommonConstant;
import com.grabber.model.HandleUnit;
import com.grabber.model.ParseUnit;
import com.grabber.thread.ThreadPool;
import com.grabber.thread.task.CrawlerThread;
import com.grabber.thread.task.HandleThread;
import com.grabber.thread.task.ParseThread;
import com.grabber.util.LoggerUtil;

/**
 * @author WY 15 Mar 2018 15:13:30
 */
public class BaseDto {

	private static final int QUEUE_CAPACITY = 10000;

	private Mission mission; // mission配置

	private BlockingQueue<ParseUnit> waitingParsedUrlQueue;// 等待爬取的队列

	private BlockingQueue<HandleUnit> waitingHandledQueue;// 等待后处理的队列

	/**
	 * 根据任务名初始化任务
	 * 
	 * @param missionsName
	 * @param threadNum
	 *            执行爬虫的线程数
	 */
	public BaseDto(String dtoName) {

		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		this.mission = MissionFactory.getInstance().getMission(dtoName);
		this.waitingParsedUrlQueue = new ArrayBlockingQueue<ParseUnit>(QUEUE_CAPACITY);
		this.waitingHandledQueue = new ArrayBlockingQueue<HandleUnit>(QUEUE_CAPACITY);

	}

	/**
	 * 初始化爬取任务
	 * 
	 * @return
	 */
	private boolean init() {

		// 分配线程池资源
		boolean isReadyCrawler = ThreadPool.getInstance().applyForThread(
				CommonConstant.getCrawlerName(mission.getMissionName()), mission.getCrawlerThreadNum());
		boolean isReadyParser = ThreadPool.getInstance().applyForThread(
				CommonConstant.getParserName(mission.getMissionName()), mission.getParserThreadNum());
		boolean isReadyHandle = ThreadPool.getInstance().applyForThread(
				CommonConstant.getHandleName(mission.getMissionName()), mission.getHandleThreadNum());

		boolean isready = isReadyCrawler && isReadyParser && isReadyHandle;
		if (!isready) {
			if (isReadyCrawler) {
				ThreadPool.getInstance().releaseThread(CommonConstant.getCrawlerName(mission.getMissionName()));
			}
			if (isReadyParser) {
				ThreadPool.getInstance().releaseThread(CommonConstant.getParserName(mission.getMissionName()));
			}
			if (isReadyHandle) {
				ThreadPool.getInstance().releaseThread(CommonConstant.getHandleName(mission.getMissionName()));
			}
			LoggerUtil.errorLog.error("Error happen when init mission , cann't apply the threadpool[isReadyCrawler = "
					+ isReadyCrawler + "][isReadyParser = " + isReadyParser + "][isReadyHandle = " + isReadyHandle
					+ "]");
		}
		return isready;
	}

	/**
	 * 开始任务
	 */
	public void start() {

		LoggerUtil.debugLog.info("[main-mission] strat new mission [mission = " + mission.getMissionName()
				+ "][CrawlerThreadNum = " + mission.getCrawlerThreadNum() + "][ParserThreadNum = "
				+ mission.getParserThreadNum() + "][HandleThreadNum =" + mission.getHandleThreadNum() + "]");
		if (init()) {
			// 1.启动爬虫线程
			// 1.1为爬虫申请线程(init中已经完成)
			// 1.2启动爬虫的线程
			new Thread() {

				@Override
				public void run() {

					long startTimeAll = System.currentTimeMillis();
					LoggerUtil.debugLog.info("[main-mission-crawl] start to crawl mission [mission = "
							+ mission.getMissionName() + "][CrawlerThreadNum = " + mission.getCrawlerThreadNum() + "]");
					// 1.3得到任务列表
					long startTime = System.currentTimeMillis();
					LoggerUtil.debugLog.info("[main-mission-crawl] start to submit crawl mission [mission = "
							+ mission.getMissionName() + "][CrawlerThreadNum = " + mission.getCrawlerThreadNum() + "]");
					// 1.4对每一个入口，分配一个线程
					Entrance entrance = EntranceFactory.getInstance().getEntarnce(mission.getEntranceName());
					List<Future<?>> fuLst = new ArrayList<Future<?>>();
					for (int i = 0; i < entrance.getEntanceSize(); i++) {
						// 1.4.1实例化爬虫
						CrawlerThread crawler = new CrawlerThread(mission, i, waitingParsedUrlQueue);
						// 1.4.2提交爬虫任务到线程池
						fuLst.add(ThreadPool.getInstance().submitTaskForMission(
								CommonConstant.getCrawlerName(mission.getMissionName()), crawler));
						LoggerUtil.debugLog.info("[main-mission-crawl] submit a crawl mission [mission = "
								+ mission.getMissionName() + "][ i = " + i + "]");
					}
					long endTime = System.currentTimeMillis();
					LoggerUtil.debugLog.info("[main-mission-crawl] end to submit crawl mission [mission = "
							+ mission.getMissionName() + "][CrawlerThreadNum = " + mission.getCrawlerThreadNum()
							+ "][cost = " + (endTime - startTime) + "ms]");

					startTime = System.currentTimeMillis();
					LoggerUtil.debugLog
							.info("[main-mission-crawl] start to listening crawl mission running [mission = "
									+ mission.getMissionName() + "][CrawlerThreadNum = "
									+ mission.getCrawlerThreadNum() + "]");
					// 1.5轮询等待所有线程结束
					for (Future<?> future : fuLst) {
						try {
							future.get();
						} catch (Exception e) {
							LoggerUtil.errorLog.error(e);
						}
					}
					endTime = System.currentTimeMillis();
					LoggerUtil.debugLog
							.info("[main-mission-crawl] end to listening  crawl mission running  [mission = "
									+ mission.getMissionName() + "][CrawlerThreadNum = "
									+ mission.getCrawlerThreadNum() + "][cost = " + (endTime - startTime) + "ms]");

					// 1.6打上结束标记
					for (int i = 0; i < mission.getParserThreadNum(); ++i) {
						try {
							waitingParsedUrlQueue.put(CommonConstant.END_PARSEUNIT);
							LoggerUtil.debugLog
									.info("[main-mission-crawl] put end tag to waitingParsedUrlQueue [mission = "
											+ mission.getMissionName() + "][CrawlerThreadNum = "
											+ mission.getCrawlerThreadNum() + "]");
						} catch (InterruptedException e) {
							LoggerUtil.debugLog.error(
									"[main-mission-crawl] error happen when put end tag to waitingParsedUrlQueue [mission = "
											+ mission.getMissionName() + "]", e);
						}
					}
					// 1.7释放爬虫占用的线程
					ThreadPool.getInstance().releaseThread(CommonConstant.getCrawlerName(mission.getMissionName()));
					long endTimeAll = System.currentTimeMillis();
					LoggerUtil.debugLog.info("[main-mission-crawl] end to crawl mission [mission = "
							+ mission.getMissionName() + "][CrawlerThreadNum = " + mission.getCrawlerThreadNum()
							+ "][cost = " + (endTimeAll - startTimeAll) + "ms]");
				}
			}.start();
			// 2.启动解析器线程
			// 2.1为解析器申请线程(init中已经完成)
			// 2.2启动解析器线程
			new Thread() {

				@Override
				public void run() {

					long startTimeAll = System.currentTimeMillis();
					LoggerUtil.debugLog.info("[main-mission-parse] start to parse mission [mission = "
							+ mission.getMissionName() + "][ParserThreadNum = " + mission.getParserThreadNum() + "]");

					// 2.3提交解析任务至线程池
					LoggerUtil.debugLog.info("[main-mission-parse] start to submit parse mission [mission = "
							+ mission.getMissionName() + "][ParserThreadNum = " + mission.getParserThreadNum() + "]");

					List<Future<?>> fuLst = new ArrayList<Future<?>>();
					for (int i = 0; i < mission.getParserThreadNum(); ++i) {
						ParseThread parseThread = new ParseThread(waitingParsedUrlQueue, waitingHandledQueue, mission);
						fuLst.add(ThreadPool.getInstance().submitTaskForMission(
								CommonConstant.getParserName(mission.getMissionName()), parseThread));
						LoggerUtil.debugLog.info("[main-mission-parse] submit a parse mission [mission = "
								+ mission.getMissionName() + "][ParserThreadNum = " + mission.getParserThreadNum()
								+ "][ i = " + i + "]");
					}
					LoggerUtil.debugLog.info("[main-mission-parse] end to submit parse mission [mission = "
							+ mission.getMissionName() + "][ParserThreadNum = " + mission.getParserThreadNum() + "]");

					long startTime = System.currentTimeMillis();
					LoggerUtil.debugLog
							.info("[main-mission-parse] start to listening parse mission running [mission = "
									+ mission.getMissionName() + "][ParserThreadNum = " + mission.getParserThreadNum()
									+ "]");
					// 2.4轮询等待所有线程结束
					for (Future<?> future : fuLst) {
						try {
							future.get();
						} catch (Exception e) {
							LoggerUtil.errorLog.error(e);
						}
					}
					long endTime = System.currentTimeMillis();
					LoggerUtil.debugLog.info("[main-mission-parse] end to listening parse mission running [mission = "
							+ mission.getMissionName() + "][ParserThreadNum = " + mission.getParserThreadNum()
							+ "][cost = " + (endTime - startTime) + "]");

					// 2.5打上结束标记
					for (int i = 0; i < mission.getHandleThreadNum(); ++i) {
						try {
							waitingHandledQueue.put(CommonConstant.END_HANDLEUNIT);
							LoggerUtil.debugLog
									.info("[main-mission-parse] put end tag to waitingHandledQueue [mission = "
											+ mission.getMissionName() + "]");
						} catch (InterruptedException e) {
							LoggerUtil.debugLog.error(
									"[main-mission-parse] error happen when put end tag to waitingHandledQueue [mission = "
											+ mission.getMissionName() + "]", e);
						}
					}

					// 2.6释放解析器占用的线程
					ThreadPool.getInstance().releaseThread(CommonConstant.getParserName(mission.getMissionName()));
					long endTimeAll = System.currentTimeMillis();
					LoggerUtil.debugLog.info("[main-mission-parse] end to parse mission [mission = "
							+ mission.getMissionName() + "][ParserThreadNum = " + mission.getParserThreadNum()
							+ "][cost = " + (endTimeAll - startTimeAll) + "ms]");

				}

			}.start();
			// 3.启动后处理线程
			// 3.1为后处理申请线程(init中已经完成)
			// 3.2启动后处理
			new Thread() {

				@Override
				public void run() {

					LoggerUtil.debugLog.info("[main-mission-handle] start to handle mission [mission = "
							+ mission.getMissionName() + "]");
					// 3.3提交解析任务至线程池
					List<Future<?>> fuLst = new ArrayList<Future<?>>();
					for (int i = 0; i < mission.getHandleThreadNum(); ++i) {
						HandleThread handleThread = new HandleThread(waitingHandledQueue, mission);
						fuLst.add(ThreadPool.getInstance().submitTaskForMission(
								CommonConstant.getHandleName(mission.getMissionName()), handleThread));
						LoggerUtil.debugLog.info("[main-mission-handle] submit a handle mission [mission = "
								+ mission.getMissionName() + "][HandleThreadNum = " + mission.getHandleThreadNum()
								+ "][ i = " + i + "]");
					}

					LoggerUtil.debugLog
							.info("[main-mission-handle] start to listening handle mission running [mission = "
									+ mission.getMissionName() + "][HandleThreadNum = " + mission.getHandleThreadNum()
									+ "]");
					// 3.4轮询等待所有线程结束
					for (Future<?> future : fuLst) {
						try {
							future.get();
						} catch (Exception e) {
							LoggerUtil.errorLog.error(e);
						}
					}
					LoggerUtil.debugLog
							.info("[main-mission-handle] end to listening handle mission running [mission = "
									+ mission.getMissionName() + "][HandleThreadNum = " + mission.getHandleThreadNum()
									+ "]");
					// 3.5释放解析器占用的线程
					ThreadPool.getInstance().releaseThread(CommonConstant.getHandleName(mission.getMissionName()));
					ThreadPool.getInstance().shutdown();
				}
			}.start();
		} else {
			LoggerUtil.debugLog.info("[main-mission] fail to start new mission, thread num limited [mission = "
					+ mission.getMissionName() + "][CrawlerThreadNum = " + mission.getCrawlerThreadNum()
					+ "][ParserThreadNum = " + mission.getParserThreadNum() + "][HandleThreadNum ="
					+ mission.getHandleThreadNum() + "]");
		}
	}

	public static void main(String[] args) {
		new BaseDto("caipiao").start();
	}
}
