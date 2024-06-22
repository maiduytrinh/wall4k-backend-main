package com.tp.wallios.common;

public final class FailedResult<T> implements ResultContext<T> {
	
	private final Throwable cause;
	
	public FailedResult(Throwable t) {
		this.cause = t;
	}
	
	public FailedResult(String failureMessage) {
		this.cause = new OnlyMessageThrowable(failureMessage);
	}
	
	
	@Override
	public T result() {
		return null;
	}

	@Override
	public Throwable cause() {
		return cause;
	}

	@Override
	public boolean succeeded() {
		return false;
	}

	@Override
	public boolean failed() {
		return true;
	}
	
	@Override
	public boolean notFound() {
		return false;
	}
	
	@Override
	public String toString() {
		return "FailedResult";
	}

    @Override
    public Integer nextPageNumber() {
        return 0;
    }
	
}
