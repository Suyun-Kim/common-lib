package com.nx.lib.obj;

public class ResponsePagingEnvelop<T> {
	private boolean success;

	private PageResults<T> result;
	private Error error;

	public ResponsePagingEnvelop(boolean success, T contents, String code, String message, int size, int page, long totalElements){
		result = new PageResults();
		result.setContents(contents);
		result.setSize(size);
		result.setPage(page);
		result.setTotalElements(totalElements);
		this.success = success;
		this.error = new Error(code, message);
	}

	public ResponsePagingEnvelop(boolean success, T contents, int size, int page, long totalElements){
		result = new PageResults();
		result.setContents(contents);
		result.setSize(size);
		result.setPage(page);
		result.setTotalElements(totalElements);
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
