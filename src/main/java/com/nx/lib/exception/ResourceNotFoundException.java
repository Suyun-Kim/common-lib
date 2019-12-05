package com.nx.lib.exception;

public class ResourceNotFoundException extends BaseException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String code, String message){
		super(code, message);
	}
}
