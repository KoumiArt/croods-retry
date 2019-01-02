package com.koumi.croods.retry.strategy;

import java.util.concurrent.TimeUnit;

public class FixedDelayRetryStrategy implements RetryStrategy {

	private int numOfRetry;
	private long waitTime;
	private boolean block;
	
	public FixedDelayRetryStrategy(int numOfRetry) {
		this.numOfRetry = numOfRetry;
		this.waitTime = 0;
	}

	public FixedDelayRetryStrategy(int numOfRetry, long time, TimeUnit unit) {
		this.numOfRetry = numOfRetry;
		this.waitTime = unit.toMillis(time);
	}
	
	public FixedDelayRetryStrategy(boolean block, int numOfRetry, long time, TimeUnit unit) {
		this.block = block;
		this.numOfRetry = numOfRetry;
		this.waitTime = unit.toMillis(time);
	}
	
	@Override
	public boolean block() {
		return block;
	}

	@Override
	public long waitTime() {
		return waitTime;
	}

	@Override
	public int numOfRetry() {
		return numOfRetry;
	}

}
