package com.tp.wallios.rest.common;

public final class Pagination {
	private int pageNumber;
	private int pageSize;
	private int total;

	public Pagination(){}

	public Pagination(int pageSize, int pageNumber, int total){
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.total = total;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
