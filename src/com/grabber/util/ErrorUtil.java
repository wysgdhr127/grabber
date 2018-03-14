package com.grabber.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.StringUtils;

import com.grabber.config.Mission;
import com.grabber.constant.CommonConstant;
import com.grabber.model.ErrorUnit;
import com.netease.common.util.MD5Util;

public class ErrorUtil {

	private static int ERRORMSG_QUEUE_SIZE = 100;
	private static HashMap<String, Integer> msgCountMap = new HashMap<String, Integer>();
	private static HashMap<String, HashSet<String>> msgMap = new HashMap<String, HashSet<String>>();
	private static BlockingQueue<ErrorUnit> waitingErrorMsgQueue = new ArrayBlockingQueue<ErrorUnit>(
			ERRORMSG_QUEUE_SIZE);

	/**
	 * put 一个ErrorUtil
	 * 
	 * @param errorUtil
	 * @return
	 */
	public static boolean put(ErrorUnit errorUnit) {

		// 初始化条件为允许
		boolean permit = true;
		boolean flag = true;

		String content = errorUnit.getContent();
		String contentMD5 = null;
		if (content != null) {
			contentMD5 = MD5Util.get(content, CommonConstant.ENCODING_DEFAULT);
		}

		synchronized (ErrorUtil.class) {
			Mission mission = errorUnit.getMission();
			// mission=null将作为结束单元，也是合法单元
			if (mission != null) {
				// 如果存在mission，但不存在missionName，则不允许放入队列
				if (StringUtils.isBlank(mission.getMissionName())) {
					permit = false;
				} else {
					String missionName = mission.getMissionName();
					// 如果对报警次数有限制（mission.getMaxMail() >
					// 0），并且存在记录（不存在时，肯定允许第一次put），并且记录值大于限制，则不允许放入队列
					if ((msgCountMap.containsKey(missionName)
							&& mission.getMaxMail() > 0 && msgCountMap
							.get(missionName) >= mission.getMaxMail())
							|| (msgMap.containsKey(missionName)
									&& StringUtils.isNotBlank(contentMD5) && msgMap
									.get(missionName).contains(contentMD5))) {
						permit = false;
					}
				}
			}

			if (permit) {
				// 放入队列
				try {
					flag = waitingErrorMsgQueue.add(errorUnit);
				} catch (Exception e) {
					LoggerUtil.errorLog.error(
							"[ErrorMsgUtil-put] the waitingErrorMsgQueue is full![ERRORMSG_QUEUE_SIZE = "
									+ ERRORMSG_QUEUE_SIZE + "]", e);
				}

				// 修改已放入的次数
				if (mission != null) {

					String missionName = mission.getMissionName();
					if (msgCountMap.containsKey(missionName)) {
						msgCountMap.put(missionName,
								msgCountMap.get(missionName) + 1);
						if (StringUtils.isNotBlank(contentMD5)) {
							msgMap.get(missionName).add(contentMD5);
						}

					} else {
						msgCountMap.put(missionName, 1);
						msgMap.put(missionName, new HashSet<String>());
						if (StringUtils.isNotBlank(contentMD5)) {
							msgMap.get(missionName).add(contentMD5);
						}
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 获取一个ErrorUtil,无则阻塞
	 * 
	 * @return
	 */
	public static ErrorUnit take() {

		try {
			return waitingErrorMsgQueue.take();
		} catch (InterruptedException e) {
			LoggerUtil.errorLog
					.error("[ErrorMsgUtil-take] error happen when take ErrorUtil from waitingErrorMsgQueue.",
							e);
			return null;
		}
	}

	/**
	 * 清空计数器
	 */
	public static void clearMsgCountMap() {

		msgCountMap.clear();
	}
}
