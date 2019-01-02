package com.koumi.croods.retry.strategy;

import java.util.concurrent.TimeUnit;

public class RetryLevel {

	private long time;
	private TimeUnit unit;

	public RetryLevel(long time, TimeUnit unit) {
		this.time = time;
		this.unit = unit;
	}

	public long getTime() {
		return unit.toMillis(time);
	}
}
