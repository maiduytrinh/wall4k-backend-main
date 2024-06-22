package com.tp.wallios.rdbms;

import java.util.List;

import com.tp.wallios.rdbms.api.Pageable;

public class PageImpl<T> implements Pageable<T> {

	private final Integer offset;
	private final Integer limit;
	
	private List<T> list;
	
	public PageImpl(Integer offset, Integer limit) {
		this.offset = offset;
		this.limit = limit;
	}

	@Override
	public Integer getOffset() {
		return this.offset;
	}
	
	@Override
	public Integer getLimit() {
		return this.limit;
	}

	@Override
	public List<T> getList() {
		return this.list;
	}
	
	public void setList(List<T> list) {
		this.list = list;
	}

	@Override
	public Boolean hasNext() {
		return this.list != null && this.list.size() == this.limit && this.list.size() > 0;
	}

	@Override
	public Boolean hasPrevious() {
		return this.list != null && this.list.size() == this.limit && this.offset > this.limit;
	}

	@Override
	public Boolean isLast() {
		return this.list == null || (this.list != null && this.list.size() == 0);
	}

	@Override
	public Pageable<T> nextPageable() {
		if (this.list != null && this.list.size() > 0) {
		    return new PageImpl<T>(this.offset + this.limit, this.limit);
		} else {
			return new PageImpl<T>(0, this.limit);
		}
	}

	@Override
	public Pageable<T> previousPageable() {
		if (this.offset >= this.limit && this.limit > 0) {
			return new PageImpl<T>(this.offset - this.limit, this.limit);
		} else {
			return new PageImpl<T>(0, this.limit);
		}
		
	}
	
	@Override
	public Integer size() {
		return this.list == null ? 0 : this.list.size();
	}

}
