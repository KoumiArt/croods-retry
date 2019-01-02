package com.koumi.croods.retry;

@FunctionalInterface
public interface RetryFunction<T, R> {

	/**
	 * Performs this operation
	 * 
	 * @throws Exception
	 */
	R retry(T t) throws Exception;
}
