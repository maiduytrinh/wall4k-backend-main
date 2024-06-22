package com.tp.wallios.common;

import java.util.List;

public interface ListAccess<E> {

	/**
	 * <p>
	 * Retrieves an array of object from the list. The returned array size is
	 * expected to be equals to the length argument.
	 * </p>
	 *
	 *
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return the array
	 * @throws Exception
	 *             any exception that would prevent access to the list
	 * @throws IllegalArgumentException
	 *             if the index value or the length value are not correct
	 */
	E[] load(int offset, int limit) throws Exception, IllegalArgumentException;

	/**
	 * <p>
	 * Retrieves an list of object from the list. The returned list size is expected
	 * to be equals to the length argument.
	 * </p>
	 *
	 *
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return the list
	 * @throws Exception
	 *             any exception that would prevent access to the list
	 * @throws IllegalArgumentException
	 *             if the index value or the length value are not correct
	 */
	List<E> sublist(int offset, int limit) throws Exception, IllegalArgumentException;

	/**
	 * Returns the list size.
	 *
	 * @return the size
	 * @throws Exception
	 *             any exception that would prevent access to the list
	 */
	int getSize() throws Exception;

}