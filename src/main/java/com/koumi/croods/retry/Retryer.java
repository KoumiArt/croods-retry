package com.koumi.croods.retry;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.koumi.croods.retry.listener.RetryContext;
import com.koumi.croods.retry.listener.RetryListener;
import com.koumi.croods.retry.strategy.FixedDelayRetryStrategy;
import com.koumi.croods.retry.strategy.RetryStrategy;

/**
 * @author nicksun
 *
 */
public class Retryer {

	private static final Logger LOG = LoggerFactory.getLogger(Retryer.class);
	private RetryStrategy strategy;
	private RetryListener listener;
	private Class<? extends Throwable> retryIfException;
	private Function<Object, Boolean> retryIfResult;

	private <T> Retryer(RetryerBuilder<T> builder) {
		this.strategy = builder.strategy;
		this.listener = builder.listener;
		this.retryIfException = builder.retryIfException;
		this.retryIfResult = builder.retryIfResult;
		if (Objects.isNull(strategy)) {
			this.strategy = new FixedDelayRetryStrategy(3);
		}
	}
	
	/**
	 * Handle RuntimeException
	 * 
	 * @param supplier
	 */
	public <T> void call(Supplier<T> supplier) {
		call(null, p -> {
			return supplier.get();
		});
	}
	
	/**
	 * Handle RuntimeException
	 * @param runnable
	 */
	public void call(Runnable runnable) {
		call(() -> {
			runnable.run();
			return null;
		});
	}
	
	/**
	 * Handle RuntimeException
	 * @param params
	 * @param con
	 */
	public <T> void call(T params, Consumer<T> consumer) {
		call(params, p -> {
			consumer.accept(params);
			return null;
		});
	}

	/**
	 * Handle Exception
	 * @param params
	 * @param func
	 */
	public <T, R> void call(T params, RetryFunction<T, R> func) {
		int num = strategy.numOfRetry();
		R r = null;
		try {
			r = func.retry(params);
			if (!retryIfResult.apply(r)) {
				return;
			}
		} catch (Throwable e) {
			if (num <= 0) {
				return;
			}
			if (Objects.nonNull(retryIfException) && !retryIfException.isInstance(e)) {
				return;
			}
			if (strategy.block()) {
				retry(num, params, func);
			} else {
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						retry(num, params, func);
						timer.cancel();
					}
				}, strategy.waitTime());
			}
		}
	}

	private <T, R> void retry(int num, T params, RetryFunction<T, R> func) {
		int n = num;
		RetryContext c = new RetryContext();
		c.setParams(params);
		c.setResult(true);
		try {
			func.retry(params);
			listener.onRetry(c);
		} catch (Throwable e) {
			c.setResult(false);
			listener.onRetry(c);
			n--;
			if (n > 0) {
				long waitTime = this.strategy.waitTime();
				if (waitTime > 0) {
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e1) {
						LOG.warn("InterruptedException:", e1);
					}
				}
				retry(n, params, func);
			}
		}
	}

	public RetryStrategy getStrategy() {
		return strategy;
	}

	public RetryListener getListener() {
		return listener;
	}

	public Class<?> getRetryIfException() {
		return retryIfException;
	}

	public static class RetryerBuilder<T> {

		private RetryStrategy strategy;
		private RetryListener listener;
		private Class<? extends Throwable> retryIfException;
		private Function<Object, Boolean> retryIfResult;

		public RetryerBuilder<T> withStrategy(RetryStrategy strategy) {
			this.strategy = strategy;
			return this;
		}

		public RetryerBuilder<T> withListener(RetryListener listener) {
			this.listener = listener;
			return this;
		}

		public RetryerBuilder<T> retryIfException(Class<? extends Throwable> e) {
			this.retryIfException = e;
			return this;
		}

		public RetryerBuilder<T> retryIfResult(Function<Object, Boolean> func) {
			this.retryIfResult = func;
			return this;
		}

		public Retryer build() {
			return new Retryer(this);
		}
	}
}
