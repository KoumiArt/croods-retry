package com.koumi.croods.retry.listener;

@FunctionalInterface
public interface RetryListener {

	void onRetry(RetryContext t);
}
