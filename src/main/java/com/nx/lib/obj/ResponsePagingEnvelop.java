package com.nx.lib.obj;

import java.util.Date;

public class ResponsePagingEnvelop<T> {
	private boolean success;

	private T result;
	private Error error;
	private int size;
	private int page;
	private long totalElements;

	public ResponsePagingEnvelop(boolean success, T result, String code, String message, int size, int page, long totalElements){
		this.success = success;
		this.result = result;
		this.error = new Error(code, message);
		this.size = size;
		this.page = page;
		this.totalElements = totalElements;
	}

	public ResponsePagingEnvelop(boolean success, T result, String code, String message){
		this.success = success;
		this.result = result;
		this.error = new Error(code, message);
		this.size = 0;
		this.page = 0;
		this.totalElements = 0;
	}

	public ResponsePagingEnvelop(boolean success, T result, int size, int page, long totalElements){
		this.success = success;
		this.result = result;
		this.size = size;
		this.page = page;
		this.totalElements = totalElements;
	}

	public ResponsePagingEnvelop(boolean success){
		this.success = success;
		this.size = 0;
		this.page = 0;
		this.totalElements = 0;
	}

	public ResponsePagingEnvelop(boolean success, Error error){
		this.success = success;
		this.error = error;
		this.size = 0;
		this.page = 0;
		this.totalElements = 0;
	}

	public ResponsePagingEnvelop(boolean success, T result, Error error){
		this.success = success;
		this.result = result;
		this.error = error;
		this.size = 0;
		this.page = 0;
		this.totalElements = 0;
	}

	public boolean getSuccess() {
		return this.success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}


	public Date getTimestamp() {
		return new Date();
	}


	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public Error getError() {
		return this.error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}
}
