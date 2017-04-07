/**
 * @(#)ServiceException.java 2016年3月21日
 *
 * Copyright 2008-2016 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.spring.ex;

/**
 * @author Woo Cupid
 * @date 2016年3月21日
 * @version $Revision$
 */
public class ApiException extends CodeMessageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6976200038594180447L;

	/**
	 * 
	 */
	public ApiException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param code
	 * @param message
	 * @param formatArgs
	 */
	public ApiException(Integer code, String message, Object... formatArgs) {
		super(code, message, formatArgs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param code
	 * @param message
	 * @param cause
	 */
	public ApiException(Integer code, String message, Throwable cause) {
		super(code, message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param code
	 * @param message
	 */
	public ApiException(Integer code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param formatArgs
	 */
	public ApiException(String message, Object... formatArgs) {
		super(message, formatArgs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ApiException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ApiException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
