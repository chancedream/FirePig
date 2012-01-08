package my.framework;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class FrequencyLimitedResource {
	
	public static final long DEFAULT_BLOCK_DURATION = 30;
	
	private static final Logger logger = Logger.getLogger(FrequencyLimitedResource.class);
	
	private long blockDuration;
	private boolean blockLast;
	private ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1);
	private long interval, start;
	private Semaphore semaphore;
	
	public FrequencyLimitedResource(long interval) {
		this(interval, DEFAULT_BLOCK_DURATION);
	}
	
	public FrequencyLimitedResource(long interval, long blockDuration) {
		this.interval = interval;
		this.blockDuration = blockDuration;
		this.semaphore = new Semaphore(1, true);
	}
	
	public void acquire() throws InterruptedException {
		semaphore.acquire();
		start = now();
	}	
	
	public void release() {
		blockLast = false;
		long end = now();
		long remain = interval - (end - start);
		if (remain > 0) {
			if (logger.isDebugEnabled()) logger.debug("Wait " + remain + " ms");
			executor.schedule(new Runnable() {
				@Override public void run() {
					semaphore.release();
				}
			}, remain, TimeUnit.MILLISECONDS);			
		} else {
			semaphore.release();
		}
	}
	
	public void releaseAndBlock() {
		if (blockLast) blockDuration *= 2;
		blockLast = true;
		if (logger.isInfoEnabled()) logger.info("Block " + blockDuration + " sec");
		executor.schedule(new Runnable() {
			@Override public void run() {
				semaphore.release();
			}
		}, blockDuration, TimeUnit.SECONDS);
	}
	
	private long now() {
		return new Date().getTime();
	}

}
