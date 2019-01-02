package com.koumi.croods.retry.strategy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

public class LevelRetryStrategy implements RetryStrategy {

	private int numOfRetry;
	private Queue<Long> queue;
	private boolean block;
	
	public LevelRetryStrategy(int numOfRetry, RetryLevel... level) {
		this(true, numOfRetry, level);
	}

	public LevelRetryStrategy(boolean block, int numOfRetry, RetryLevel... level) {
		this.block = block;
		this.numOfRetry = numOfRetry;
		queue = new LinkedList<Long>();
		Arrays.sort(level, Comparator.comparing(RetryLevel::getTime));
		for (int i = 0; i < level.length; i++) {
			RetryLevel l = level[i];
			queue.offer(l.getTime());
		}
	}
	
	@Override
	public boolean block() {
		return block;
	}

	@Override
	public int numOfRetry() {
		return numOfRetry;
	}

	@Override
	public long waitTime() {
		return queue.poll();
	}

}
