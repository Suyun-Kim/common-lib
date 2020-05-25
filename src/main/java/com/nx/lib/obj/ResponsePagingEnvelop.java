package com.nx.lib.obj;

import org.springframework.data.domain.Page;

public class ResponsePagingEnvelop<T> {
	private boolean success;

	private PageResults<T> result;
	private Error error;

	public ResponsePagingEnvelop(boolean success, T content, String code, String message, int size, int page, long totalElements){
		PageResults<T> pageResults = new PageResults(content, size, page, totalElements);
		this.result = pageResults;
		this.success = success;
		this.error = new Error(code, message);
	}

	public ResponsePagingEnvelop(boolean success, T content, int size, int page, long totalElements){
		PageResults<T> pageResults = new PageResults(content, size, page, totalElements);
		this.result = pageResults;
		this.success = success;
	}

	public ResponsePagingEnvelop(boolean success, Page page, String code, String message){
		PageResults<T> pageResults = new PageResults(page.getContent(), page.getSize(), page.getPageable().getPageNumber(), page.getTotalElements());
		this.result = pageResults;
		this.success = success;
		this.error = new Error(code, message);
	}

	public ResponsePagingEnvelop(boolean success, Page page){
		PageResults<T> pageResults = new PageResults(page.getContent(), page.getSize(), page.getPageable().getPageNumber(), page.getTotalElements());
		this.result = pageResults;
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public PageResults<T> getResult() {
		return result;
	}

	public void setResult(PageResults<T> result) {
		this.result = result;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}
}
