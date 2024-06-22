package com.tp.wallios.common;

public class NotFoundResult<T> implements ResultContext<T> {
	
	private final Throwable cause;
	
	public NotFoundResult(Throwable t) {
		this.cause = t;
	}
	
	public NotFoundResult(String message) {
		this.cause = new OnlyMessageThrowable(message);
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
		return false;
	}
	
	@Override
	public boolean notFound() {
		return true;
	}
	
	@Override
	public String toString() {
		return "NotFoundResult";
	}

    @Override
    public Integer nextPageNumber() {
        return 0;
    }
}