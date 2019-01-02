croods-retry


java retry 框架 
demo：

```
Retryer retryer = new Retryer.RetryerBuilder<>().retryIfResult(Objects::isNull)
				// .withStrategy(new FixedDelayRetryStrategy(false, 3, 1, TimeUnit.SECONDS))
				.withStrategy(new LevelRetryStrategy(false, 3, new RetryLevel(1, TimeUnit.SECONDS),
						new RetryLevel(10, TimeUnit.SECONDS), new RetryLevel(60, TimeUnit.SECONDS)))
				.withListener(new RetryListener() {
					@Override
					public <T> void onRetry(RetryContext c) {
						System.out.println(c.isResult());
					}
				}).build();
		retryer.call(1, t -> {
			System.out.println("run:" + t);
			return null;
		});
		System.out.println("running");
``` 


