package com.nx.lib.exception;

public class ForbiddenException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ForbiddenException(String code, String message){
		super(code, message);
	}
}
