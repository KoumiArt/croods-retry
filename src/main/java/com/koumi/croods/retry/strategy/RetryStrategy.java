package com.koumi.croods.retry.strategy;

public interface RetryStrategy {

	default boolean block() {
		return true;
	}

	int numOfRetry();

	long waitTime();
}
