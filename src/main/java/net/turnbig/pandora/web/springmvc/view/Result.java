/**
 * @(#)ApiResult.java 2015年1月12日
 *
 * Copyright 2008-2015 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.web.springmvc.view;

import java.io.Serializable;

import net.turnbig.pandora.spring.ex.CodeMessageException;

/**
 * @author Woo Cupid
 * @date 2015年1月12日
 * @version $Revision$
 */
public class Result implements Serializable {

	private static final long serialVersionUID = 2279137377128961786L;

	private Boolean result = true;
	private String message = "request has been processed"; // default success message
	private Integer code = 200; // default success code
	private Object data = null;

	public static Result success(Object data) {
		Result result = new Result();
		result.setData(data);
		return result;
	}

	public static Result success() {
		Result result = new Result();
		return result;
	}

	public static Result failed(ResultMessage message) {
		return Result.failed(message.getCode(), message.getMessage());
	}

	public static Result failed(String message) {
		return Result.failed(400, message);
	}

	public static Result failed(Integer code, String message) {
		Result result = new Result();
		result.setResult(false);
		result.setMessage(message);
		result.setCode(code);
		return result;
	}

	public static Result failed(Integer code, String message, Object data) {
		Result result = new Result();
		result.setResult(false);
		result.setMessage(message);
		result.setCode(code);
		result.setData(data);
		return result;
	}

	public static Result failed(CodeMessageException e) {
		return Result.failed(e.getCode(), e.getMessage());
	}

	/**
	 * @return the result
	 */
	public Boolean getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(Boolean result) {
		this.result = result;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
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

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

}
