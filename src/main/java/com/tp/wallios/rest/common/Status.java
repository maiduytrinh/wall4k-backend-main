package com.tp.wallios.rest.common;

public final class Status {
	
	private final int statusCode;
	private final String message;
	
	public Status(int statusCode, String message) {
		this.message = message;
		this.statusCode = statusCode;
	}
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getMessage() {
		return message;
	}
		
	

}
