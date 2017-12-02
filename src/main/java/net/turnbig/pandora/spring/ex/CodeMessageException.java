/**
 * @(#)ApiException.java 2015年1月12日
 *
 * Copyright 2008-2015 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.spring.ex;

import java.text.MessageFormat;

/**
 * @author Woo Cupid
 * @date 2015年1月12日
 * @version $Revision$
 */
public class CodeMessageException extends RuntimeException {

	private static final long serialVersionUID = 3837058182200378159L;

	Integer code = 400;

	public CodeMessageException() {
		super();
	}

	public CodeMessageException(String message) {
		super(message);
	}

	/**
	 * 
	 * example:
	 * throws new ApiException("user {0} is invalid", user.getId());
	 * 
	 * @param message
	 * @param formatArgs
	 */
	public CodeMessageException(String message, Object... formatArgs) {
		super(MessageFormat.format(message, formatArgs));
	}

	public CodeMessageException(Throwable cause) {
		super(cause);
	}

	public CodeMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	// ========== construct with codes ============== //

	public CodeMessageException(Integer code, String message) {
		this(message);
		this.code = code;
	}

	public CodeMessageException(Integer code, String message, Object... formatArgs) {
		this(message, formatArgs);
		this.code = code;
	}

	public CodeMessageException(Integer code, String message, Throwable cause) {
		this(message, cause);
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CodeMessageException [code=" + code + ", message=" + getMessage() + "]";
	}

}
