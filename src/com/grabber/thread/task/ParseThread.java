package com.grabber.thread.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import com.grabber.config.Mission;
import com.grabber.constant.CommonConstant;
import com.grabber.model.BaseModel;
import com.grabber.model.HandleUnit;
import com.grabber.model.ParseUnit;
import com.grabber.thread.ThreadPool;
import com.grabber.util.LoggerUtil;
import com.grabber.util.ParserUtil;

/**
 * 解析线程
 */
public class ParseThread implements Callable<ArrayList<String>> {

	private BlockingQueue<ParseUnit> waitingParsedUrlQueue;// 等待爬取的队列

	private BlockingQueue<HandleUnit> waitingHandledQueue;// 等待后处理的队列

	private Mission mission;// 任务描述

	public ParseThread(BlockingQueue<ParseUnit> waitingParsedUrlQueue,
			BlockingQueue<HandleUnit> waitingHandledQueue, Mission mission) {

		this.waitingParsedUrlQueue = waitingParsedUrlQueue;
		this.waitingHandledQueue = waitingHandledQueue;
		this.mission = mission;
	}

	public ArrayList<String> call() throws Exception {

		String parserDtoName = CommonConstant.getParserName(mission
				.getMissionName());
		try {
			while (true) {
				ParseUnit parseUnit = null;
				try {
					parseUnit = waitingParsedUrlQueue.take();
					LoggerUtil.parseLog
							.info("[parse running] success to take a parseUnit from waitingParsedUrlQueue.");
				} catch (InterruptedException e) {
					LoggerUtil.errorLog
							.error("[parse running] Error happen when take from waitingParsedUrlQueue.",
									e);
				}
				if (parseUnit.getUrl().equals(CommonConstant.CRAWLER_OVER_URL)) {
					return null;
				}

				List<BaseModel> result = ParserUtil.parsePagesByNextPageXpath(
						parseUnit.getUrl(), null, mission,
						parseUnit.getEntranceIndex());
				LoggerUtil.parseLog
						.info("[parse running] sucess to parser [url = "
								+ parseUnit.getUrl() + "][listSize = "
								+ result.size() + "].");

				int begin = 0;
				while (begin < result.size()) {
					List<BaseModel> resultSplit = null;

					if (begin + mission.getHandleUnitMaxSize() < result.size()) {
						resultSplit = result.subList(begin,
								begin + mission.getHandleUnitMaxSize());
					} else {
						resultSplit = result.subList(begin, result.size());
					}
					HandleUnit handleUnit = new HandleUnit(resultSplit);
					waitingHandledQueue.offer(handleUnit);
					begin = begin + mission.getHandleUnitMaxSize();
				}
			}
		} catch (Exception e) {
			LoggerUtil.errorLog.error(
					"[parse running] Error happen when parseing [parserDtoName = "
							+ parserDtoName + "].", e);
		} finally {
			ThreadPool.getInstance().releaseTaskForMission(parserDtoName);
		}

		return null;
	}
}
