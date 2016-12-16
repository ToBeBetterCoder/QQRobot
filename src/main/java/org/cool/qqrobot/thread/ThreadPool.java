package org.cool.qqrobot.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.cool.qqrobot.common.Const;
/**
 * 线程池
 * @author zhoukl
 *
 */
public final class ThreadPool {
	private static ThreadPool threadPool = new ThreadPool();
	private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Const.FIXED_THREAD_POOL_NUM);
	private static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(Const.SCHEDULED_THREAD_POOL_NUM);
	
	private ThreadPool() {}
	public static ThreadPool getInstance() {
		return threadPool;
	}
	public ExecutorService getFixedThreadPool() {
		return fixedThreadPool; 
	}
	public ScheduledExecutorService getScheduledThreadPool() {
		return scheduledThreadPool;
	}
}
