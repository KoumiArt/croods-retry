package com.koumi.croods.retry.listener;

@FunctionalInterface
public interface RetryListener {

	<T> void onRetry(RetryContext t);
}
