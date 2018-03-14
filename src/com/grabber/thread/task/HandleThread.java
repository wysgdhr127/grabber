package com.grabber.thread.task;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import com.grabber.config.Mission;
import com.grabber.constant.CommonConstant;
import com.grabber.model.BaseModel;
import com.grabber.model.HandleUnit;
import com.grabber.thread.ThreadPool;
import com.grabber.util.LoggerUtil;

/**
 * 后处理线程
 */
public class HandleThread implements Callable<ArrayList<String>> {

	private BlockingQueue<HandleUnit> waitingHandledQueue;// 等待后处理的队列

	private Mission mission;

	public HandleThread(BlockingQueue<HandleUnit> waitingHandledQueue, Mission mission) {

		this.waitingHandledQueue = waitingHandledQueue;
		this.mission = mission;
	}

	@Override
	public ArrayList<String> call() throws Exception {

		String handleDtoName = CommonConstant.getHandleName(mission.getMissionName());
		HandleUnit handleUnit = null;
		try {
			while (true) {
				try {
					handleUnit = (HandleUnit) waitingHandledQueue.take();
				} catch (InterruptedException e) {
					LoggerUtil.errorLog.error(
							"[handle running] Error happen when take handleUnit from waitingHandledQueue [handleDtoName = "
									+ handleDtoName + "].", e);
				}
				if (handleUnit.getObjectList() == null) {
					return null;
				}
				for (BaseModel t : handleUnit.getObjectList()) {
					mission.getHandleStrategy().handleObject(t);
				}
				LoggerUtil.handleLog.info("[handle running] sucess to deal with result[size = "
						+ handleUnit.getObjectList().size() + "].");
			}
		} catch (Exception e) {
			LoggerUtil.errorLog.error("[handle running] Error happen when handling [handleDtoName = " + handleDtoName
					+ "].", e);
		} finally {
			ThreadPool.getInstance().releaseTaskForMission(handleDtoName);
			if (ThreadPool.getInstance().getRunningTaskForMission(handleDtoName) == 0) {
				/*
				 * if (mission.getExporter() != null) {
				 * mission.getExporter().close(); }
				 */
			}
		}
		return null;
	}
}
