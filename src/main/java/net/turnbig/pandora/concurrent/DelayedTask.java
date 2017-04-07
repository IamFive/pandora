/**
 * @(#)DelayedTask.java 2015年6月12日
 *
 * Copyright 2008-2015 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.concurrent;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Woo Cupid
 * @date 2015年6月12日
 * @version $Revision$
 */
public class DelayedTask implements Delayed, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(DelayedTask.class);

	private final long trigger;
	private final Runnable runnable;

	public DelayedTask(Runnable runnable, long delay, TimeUnit unit) {
		this.runnable = runnable;
		this.trigger = (System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, unit));
	}

	/**
	 * no priority support
	 */
	@Override
	public int compareTo(Delayed o) {
		long other = ((DelayedTask) o).trigger;
		int returnValue;
		if (this.trigger < other) {
			returnValue = -1;
		} else {
			if (this.trigger > other)
				returnValue = 1;
			else
				returnValue = 0;
		}
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
	 */
	@Override
	public long getDelay(TimeUnit timeUnit) {
		long n = this.trigger - System.currentTimeMillis();
		logger.debug("delay is {} milli-seconds", n);
		return timeUnit.convert(n, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		runnable.run();
	}

}
