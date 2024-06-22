package com.tp.wallios.rdbms.api;

import java.util.List;

public interface Pageable<T> {
	
	/**
	 * Get limit
	 * 
	 * @return Integer value
	 */
	Integer getLimit();
	
	/**
	 * Get offset
	 * 
	 * @return Integer value
	 */
	Integer getOffset();
	
	/**
	 * Get list
	 * 
	 * @return
	 */
	List<T> getList();
	
	/**
	 * Set the list 
	 * @param list
	 */
	void setList(List<T> list);
	
	/**
	 * Has next page
	 * 
	 * @return true/false
	 */
	Boolean hasNext();
	
	/**
	 * Has previous page
	 * 
	 * @return true/false
	 */
	Boolean hasPrevious();
	
	/**
	 * Is last page
	 * @return true/false
	 */
	Boolean isLast();

	/**
	 * Get next pageable
	 * @return
	 */
	Pageable<T> nextPageable();
	
	/**
	 * Get next pageable
	 * 
	 * @return
	 */
	Pageable<T> previousPageable();
	
	/**
	 * Get size of list
	 * @return
	 */
	Integer size();
	
}
