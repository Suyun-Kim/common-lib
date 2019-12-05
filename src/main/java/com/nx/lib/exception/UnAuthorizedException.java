package com.nx.lib.exception;

public class UnAuthorizedException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnAuthorizedException(String code, String message){
		super(code, message);
	}
}
