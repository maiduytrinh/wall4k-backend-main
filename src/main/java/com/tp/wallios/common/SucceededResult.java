package com.tp.wallios.common;

public final class SucceededResult<T> implements ResultContext<T> {

	public static final SucceededResult EMPTY = new SucceededResult(null);

	private final T result;

	private Integer nextPageNumber = 1;

	public SucceededResult(T result) {
		this.result = result;
	}

	public SucceededResult(T result, Integer nextPageNumber) {
		this.result = result;
		this.nextPageNumber = nextPageNumber;
	}

	@Override
	public T result() {
		return result;
	}

	@Override
	public Throwable cause() {
		return null;
	}

	@Override
	public boolean succeeded() {
		return true;
	}

	@Override
	public boolean failed() {
		return false;
	}

	@Override
	public boolean notFound() {
		return false;
	}

	@Override
	public String toString() {
		return "SucceededResult{result=" + result.toString() + "}";
	}

	@Override
	public Integer nextPageNumber() {
		return this.nextPageNumber;
	}

}
