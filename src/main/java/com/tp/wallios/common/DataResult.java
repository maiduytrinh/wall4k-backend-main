package com.tp.wallios.common;

import java.util.List;

public class DataResult<T> {
	
	private final List<T> result;
	
	private Integer nextPageNumber = 1;

	public DataResult(List<T> data, Integer nextPageNumber) {
		this.result = data;
		this.nextPageNumber = nextPageNumber;
	}

	public List<T> getResult() {
		return result;
	}

	public Integer getNextPageNumber() {
		return nextPageNumber;
	}

}
