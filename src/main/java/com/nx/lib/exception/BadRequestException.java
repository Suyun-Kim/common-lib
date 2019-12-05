package com.nx.lib.exception;

public class BadRequestException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadRequestException(String code, String message){
		super(code, message);
	}
}
