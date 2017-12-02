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
public class ServiceException extends CodeMessageException {

	private static final long serialVersionUID = -6976200038594180447L;

	public ServiceException() {
		super();
	}

	/**
	 * @param code
	 * @param message
	 * @param formatArgs
	 */
	public ServiceException(Integer code, String message, Object... formatArgs) {
		super(code, message, formatArgs);
	}

	/**
	 * @param code
	 * @param message
	 * @param cause
	 */
	public ServiceException(Integer code, String message, Throwable cause) {
		super(code, message, cause);
	}

	/**
	 * @param code
	 * @param message
	 */
	public ServiceException(Integer code, String message) {
		super(code, message);
	}

	/**
	 * @param message
	 * @param formatArgs
	 */
	public ServiceException(String message, Object... formatArgs) {
		super(message, formatArgs);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ServiceException(Throwable cause) {
		super(cause);
	}

}
