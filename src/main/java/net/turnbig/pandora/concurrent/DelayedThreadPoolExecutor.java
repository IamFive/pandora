/**
 * @(#)DelayedThreadPoolExecutor.java 2015年6月12日
 *
 * Copyright 2008-2015 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author Woo Cupid
 * @date 2015年6月12日
 * @version $Revision$
 */
public class DelayedThreadPoolExecutor extends ThreadPoolExecutor {

	private static final Logger logger = LoggerFactory.getLogger(DelayedThreadPoolExecutor.class);

	/**
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param d 
	 * @param keepAliveTime
	 * @param unit
	 * @param workQueue
	 */
	@SuppressWarnings("rawtypes")
	public DelayedThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
		super(corePoolSize, maximumPoolSize, 0L, TimeUnit.SECONDS, new DelayQueue(),
				new ThreadFactoryBuilder().setNameFormat("Common-delay-pool-%d").build());

		logger.info("Submit a do nothing task to initial delayed thread pool executor");
		// dont know why the first task is not delayed
		for (int i = 0; i < maximumPoolSize; i++) {
			final int idx = i;
			this.submit(new Runnable() {
				@Override
				public void run() {
					logger.info("Initial delayed thread pool executor {}", idx);
				}
			}, 100, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return new DelayedFutureTask<T>(runnable, value);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new DelayedFutureTask<T>(callable);
	}

	/**
	 * submit task with default delay - 60 SENCONDS
	 */
	@Override
	public Future<?> submit(Runnable task) {
		return super.submit(new DelayedTask(task, 60, TimeUnit.SECONDS));
	}

	/**
	 * submit task with spec delay
	 */
	public Future<?> submit(Runnable task, long delay, TimeUnit unit) {
		return super.submit(new DelayedTask(task, delay, unit));
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		throw new RuntimeException("not implemented");
	}

	static class DelayedFutureTask<V> extends FutureTask<V> implements Delayed {

		DelayedTask delayedTask;

		DelayedFutureTask(Callable<V> callable) {
			super(callable);
			delayedTask = (DelayedTask) callable;
		}

		DelayedFutureTask(Runnable runnable, V result) {
			super(runnable, result);
			delayedTask = (DelayedTask) runnable;
		}

		@Override
		public int compareTo(Delayed o) {
			return delayedTask.compareTo(((DelayedFutureTask<?>) o).getDelayedTask());
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return delayedTask.getDelay(unit);
		}

		/**
		 * @return the delayedTask
		 */
		public DelayedTask getDelayedTask() {
			return delayedTask;
		}

	}

}
