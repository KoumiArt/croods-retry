package com.koumi.croods.retry.listener;

public class RetryContext {

	private boolean result;
	
	private Object params;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}
	
}
