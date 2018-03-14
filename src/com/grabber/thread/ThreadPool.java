package com.grabber.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.grabber.constant.CommonConstant;
import com.grabber.thread.task.ErrorThread;
import com.grabber.util.ErrorUtil;
import com.grabber.util.LoggerUtil;

/**
 * @author WY 14 Mar 2018 14:52:47
 */
public class ThreadPool {

	public static int minThreadNum = 50;
	public static int maxThreadNum = 50;
	public static int waitingQueueSize = 100;
	private static ThreadPool instance;
	private ExecutorService exec;
	private int availableThreadNum = 0;
	private Map<String, byte[]> dto2Lock = new HashMap<String, byte[]>();
	private Map<String, Integer> dto2RunningThreadNum = new ConcurrentHashMap<String, Integer>(); // 从key到已运行的线程数
	private Map<String, Integer> dto2CapacityThreadNum = new ConcurrentHashMap<String, Integer>(); // 从key到被申请的线程数

	private ThreadPool(int minThreadNum, int maxThreadNum, int waitingQueueSize) {

		this.availableThreadNum = maxThreadNum;
		exec = new ThreadPoolExecutor(minThreadNum, maxThreadNum, 5 * 60,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
						waitingQueueSize), new ThreadPoolExecutor.AbortPolicy());

	}

	public static ThreadPool getInstance() {

		if (instance == null) {
			synchronized (ThreadPool.class) {
				if (instance == null) {
					instance = new ThreadPool(minThreadNum, maxThreadNum,
							waitingQueueSize);

					// 开启错误处理线程
					ErrorThread errorThread = new ErrorThread();
					errorThread.start();
				}
			}
		}
		return instance;

	}

	/**
	 * 为某个任务申请threadNum个
	 * 
	 * @param missionName
	 * @param threadNum
	 * @return
	 */
	public boolean applyForThread(String dtoName, int threadNum) {

		synchronized (this) {
			if (dto2RunningThreadNum.containsKey(dtoName)) {
				LoggerUtil.errorLog
						.error("[ThreadPool-applyForThread] There is missionName: "
								+ dtoName + " already exist.");
				return false;
			}
			if (availableThreadNum >= threadNum) {
				availableThreadNum -= threadNum;
				dto2RunningThreadNum.put(dtoName, 0);
				dto2CapacityThreadNum.put(dtoName, threadNum);
				dto2Lock.put(dtoName, new byte[0]);
				LoggerUtil.debugLog
						.debug("[ThreadPool-applyForThread] Success to applyForThread [dtoName="
								+ dtoName + "][threadNum=" + threadNum + "].");
				return true;
			} else {
				LoggerUtil.errorLog
						.error("[ThreadPool-applyForThread] The availableThreadNum is less than threadNum [dtoName="
								+ dtoName
								+ "][threadNum="
								+ threadNum
								+ "][availableThreadNum="
								+ availableThreadNum
								+ "].");
				return false;
			}
		}

	}

	/**
	 * 将某个任务从线程池中删除 执行此命令前，请确保改任务的所有线程均已结束
	 * 
	 * @param missionName
	 * @param threadNum
	 */
	public boolean releaseThread(String dtoName) {

		synchronized (this) {
			if (!dto2RunningThreadNum.containsKey(dtoName)) {
				LoggerUtil.debugLog
						.warn("[ThreadPool-releaseThread] Don't need  releaseThread, the dtoName don't exist.[dtoName = "
								+ dtoName + "]");
				return true;
			}

			if (dto2RunningThreadNum.get(dtoName) == 0) {
				availableThreadNum = dto2CapacityThreadNum.get(dtoName)
						+ availableThreadNum;
				dto2RunningThreadNum.remove(dtoName);
				dto2CapacityThreadNum.remove(dtoName);
				dto2Lock.remove(dtoName);
				LoggerUtil.debugLog
						.debug("[ThreadPool-releaseThread] Success to  releaseThread.[dtoName = "
								+ dtoName + "]");
				return true;

			} else {
				LoggerUtil.errorLog
						.error("[ThreadPool-releaseThread] Can't releaseThread, the dtoName don't.[dtoName = "
								+ dtoName + "]");
				return false;
			}
		}
	}

	/**
	 * 提交一个mission的一个线程
	 * 
	 * @param missionName
	 * @return
	 */
	public Future<?> submitTaskForMission(String dtoName, Callable<?> callable) {

		if (dto2RunningThreadNum.containsKey(dtoName)
				&& dto2CapacityThreadNum.containsKey(dtoName)) {
			while (true) {
				synchronized (dto2Lock.get(dtoName)) {
					if (dto2RunningThreadNum.get(dtoName) < dto2CapacityThreadNum
							.get(dtoName)) {
						dto2RunningThreadNum.put(dtoName,
								dto2RunningThreadNum.get(dtoName) + 1);
						LoggerUtil.debugLog
								.debug("[ThreadPool-submitTaskForMission] Success to submitTaskForMission.[dtoName = "
										+ dtoName + "]");
						return exec.submit(callable);
					} else {
						try {
							dto2Lock.get(dtoName).wait();
						} catch (InterruptedException e) {
							LoggerUtil.errorLog
									.error("[ThreadPool-submitTaskForMission] Error happen when submitTaskForMission.[dtoName = "
											+ dtoName + "]", e);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 为此mission释放一个线程
	 * 
	 * @param missionName
	 */
	public boolean releaseTaskForMission(String dtoName) {

		if (dto2RunningThreadNum.containsKey(dtoName)
				&& dto2CapacityThreadNum.containsKey(dtoName)) {
			synchronized (dto2Lock.get(dtoName)) {
				if (dto2RunningThreadNum.get(dtoName) > 0) {
					dto2RunningThreadNum.put(dtoName,
							dto2RunningThreadNum.get(dtoName) - 1);
					dto2Lock.get(dtoName).notifyAll();
					LoggerUtil.debugLog
							.debug("[ThreadPool-releaseTaskForMission] Success to releaseTaskForMission.[dtoName = "
									+ dtoName + "]");
					return true;
				} else {
					LoggerUtil.errorLog
							.error("[ThreadPool-submitTaskForMission] There is not enough thread to releaseTaskForMission.[dtoName = "
									+ dtoName
									+ "][runningThreadNum="
									+ dto2RunningThreadNum.get(dtoName) + "]");
					return false;
				}
			}
		} else {
			LoggerUtil.errorLog
					.error("[ThreadPool-submitTaskForMission] There is no such mission in ThreadPool.[dtoName = "
							+ dtoName + "]");
			return false;
		}
	}

	/**
	 * 得到此任务目前正在运行的线程数
	 * 
	 * @param dtoName
	 * @return
	 */
	public int getRunningTaskForMission(String dtoName) {

		if (dto2RunningThreadNum.containsKey(dtoName)) {
			return dto2RunningThreadNum.get(dtoName);
		}
		return 0;
	}

	/**
	 * 结束线程池
	 */
	public void shutdown() {

		exec.shutdown();
		// 线程池结束时，ErrorMsgThread加入结束标志
		ErrorUtil.put(CommonConstant.END_ERRORUNIT);
	}
}
