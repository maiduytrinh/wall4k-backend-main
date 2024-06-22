package com.tp.wallios.rest.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

public final class Response<T> implements Serializable {

	@JsonInclude(Include.NON_NULL)
	private T data;
	@JsonInclude(Include.NON_NULL)
	private Status status = null;
	@JsonInclude(Include.NON_NULL)
	private Pagination pagination = null;
	@JsonInclude(Include.NON_NULL)
	private Link links = null;
	@JsonInclude(Include.NON_NULL)
	private Integer nextPage = 1;

	private Response() {

	}

	private Response(int code, String message, T data) {
		this.status = new Status(code, message);
		this.data = data;
	}

	private Response(int code, String message, T data, int nextPage) {
		this.status = new Status(code, message);
		this.data = data;
		this.nextPage = nextPage;
	}

	public static <T> Response<T> ok(T data) {
		return response(ResponseCode.SUCCESS, ResponseMsg.SUCCESS, data);
	}

	public static <T> Response<T> ok(T data, int nextPage) {
		return response(ResponseCode.SUCCESS, ResponseMsg.SUCCESS, data, nextPage);
	}

	public static <T> Response<T> ok(String message, T data) {
		return response(ResponseCode.SUCCESS, message, data);
	}

	public static <T> Response<T> created(T data) {
		return response(ResponseCode.CREATED, ResponseMsg.SUCCESS, data);
	}

	public static <T> Response<T> created(String message, T data) {
		return response(ResponseCode.CREATED, message, data);
	}

	public static <T> Response<T> accepted(String msg) {
		return response(ResponseCode.ACCEPTED, msg, null);
	}

	public static <T> Response<T> response(int code, String messsage, T data) {
		Response<T> response = new Response<>(code, messsage, data);
		return response;
	}

	public static <T> Response<T> response(int code, String messsage, T data, int nextPage) {
		Response<T> response = new Response<>(code, messsage, data, nextPage);
		return response;
	}

	public static <T> Response<T> fail(T data) {
		return response(ResponseCode.FAIL, null, data);
	}

	public static <T> Response<T> fail(String msg) {
		return response(ResponseCode.FAIL, msg, null);
	}

	public static <T> Response<T> notFound(T data) {
		return response(ResponseCode.NOT_FOUND, null, data);
	}

	public static <T> Response<T> notFound(String msg, T data) {
		return response(ResponseCode.NOT_FOUND, msg, data);
	}

	public static <T> Response<T> notFound(String msg) {
		return response(ResponseCode.NOT_FOUND, msg, null);
	}

	static class ResponseMsg {
		public static String SUCCESS = "Success";
		public static String CREATED_SUCCESS = "Created Success";
	}

	static class ResponseCode {
		public static int SUCCESS = 200;
		public static int CREATED = 201;
		public static int NOT_FOUND = 404;
		public static int FAIL = 400;
		public static int ACCEPTED = 202;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	public Link getLinks() {
		return links;
	}

	public void setLinks(Link links) {
		this.links = links;
	}

}
