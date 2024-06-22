package com.tp.wallios.common;

/**
 * Represents the result of an action that may, or may not, have occurred yet.
 * 
 * @author thanhvc
 *
 * @param <T>
 */
public interface  ResultContext<T> extends BaseResult<T> {

	/**
	 * Create a succeeded future with a null result
	 * 
	 * @return Succeeded Result
	 */
	static <T> SucceededResult<T> succeededResult() {
		return (SucceededResult<T>) SucceededResult.EMPTY;
	}

	/**
	 * Created a succeeded with the specified result.
	 * 
	 * @param
	 * @return the result
	 */
	static <T> SucceededResult<T> succeededResult(T result) {
		if (result == null) {
			return succeededResult();
		} else {
			return new SucceededResult<>(result);
		}
	}
	
	/**
	 * Created a succeeded with the specified result.
	 * 
	 * @param
	 * @return the result
	 */
	static <T> SucceededResult<T> succeededResult(T result, Integer nextPageNumber) {
		if (result == null) {
			return succeededResult();
		} else {
			return new SucceededResult<>(result, nextPageNumber);
		}
	}

	/**
	 * Create a failed result with the specified failure cause.
	 * 
	 * @param t
	 *            result the failure cause as a Throwable
	 * @return the result
	 */
	static <T> FailedResult<T> failedResult(Throwable t) {
		return new FailedResult<>(t);
	}
	
	/**
	 * Create a failed result with the specified failure message.
	 *
	 * @param failureMessage
	 *            the failure message
	 * @param <T>
	 *            the result type
	 * @return the result
	 */
	static <T> FailedResult<T> failedResult(String failureMessage) {
		return new FailedResult<>(failureMessage);
	}

	/**
	 * Create a not found result with the specified message.
	 *
	 * @param message
	 *            the failure message
	 * @param <T>
	 *            the result type
	 * @return the result
	 */
	static <T> NotFoundResult<T> notFoundResult(String message) {
		return new NotFoundResult<>(message);
	}

	/**
	 * Create a not found result with the specified cause.
	 * 
	 * @param t
	 *            result the failure cause as a Throwable
	 * @return the result
	 */
	static <T> NotFoundResult<T> notFoundResult(Throwable t) {
		return new NotFoundResult<>(t);
	}

	

	/**
	 * The result of the operation. This will be null if the operation failed.
	 * 
	 * @return the result or null if the operation failed.
	 */
	@Override
	T result();
	
	/**
	 * The result of the operation. This will be null if the operation failed.
	 * 
	 * @return the result or null if the operation failed.
	 */
	@Override
	Integer nextPageNumber();

	/**
	 * A Throwable describing failure. This will be null if the operation succeeded.
	 * 
	 * @return the cause or null if the operation succeeded.
	 */
	Throwable cause();

	/**
	 * Did it succeed?
	 * 
	 * @return true if it succeded or false otherwise
	 */
	@Override
	boolean succeeded();

	/**
	 * Did it fail?
	 * 
	 * @return true if it failed or false otherwise
	 */
	@Override
	boolean failed();
	
	 /**
	   * Did it not found?
	   *
	   * @return true if it not found or false otherwise
	   */
	@Override
	boolean notFound();

}
