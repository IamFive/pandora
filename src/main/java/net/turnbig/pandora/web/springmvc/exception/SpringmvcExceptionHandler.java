/**
 * @(#)SpecExceptionHandler.java 2015年1月12日
 *
 * Copyright 2008-2015 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.web.springmvc.exception;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.common.collect.Maps;
import net.turnbig.pandora.spring.ex.ApiException;
import net.turnbig.pandora.spring.ex.ServiceException;
import net.turnbig.pandora.web.springmvc.view.Result;

/**
 * @author Woo Cupid
 * @date 2015年1月12日
 * @version $Revision$
 */
@ControllerAdvice
public class SpringmvcExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(SpringmvcExceptionHandler.class);

	public String handleNoHandlerFoundException(Exception ex) {
		return "404";
	}

	@ExceptionHandler(value = { ApiException.class })
	public final ResponseEntity<Result> apiExHandler(ApiException ex) {
		// we use rpc-style api response, so, we won't use http status for issue mapping
		logger.info("api exception caught --> {}", ex.getMessage());
		return new ResponseEntity<Result>(Result.failed(ex), HttpStatus.OK);
	}

	@ExceptionHandler(value = { ServiceException.class })
	public final ResponseEntity<Result> serviceExceptionHandler(ServiceException ex) {
		// we use rpc-style api response, so, we won't use http status for issue mapping
		logger.info("service exception caught --> {}", ex.getMessage());
		return new ResponseEntity<Result>(Result.failed(ex), HttpStatus.OK);
	}

	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		HashMap<String, String> errors = extractErros(ex.getBindingResult());
		logger.info("binding exception caught --> {}", ex.getMessage());
		return new ResponseEntity<>(Result.failed(HttpStatus.UNPROCESSABLE_ENTITY.value(), "提交的数据有误，请检查", errors),
				HttpStatus.OK);
	}

	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		HashMap<String, String> errors = extractErros(ex.getBindingResult());
		logger.info("binding exception caught --> {}", ex.getMessage());
		return new ResponseEntity<>(Result.failed(HttpStatus.UNPROCESSABLE_ENTITY.value(), "提交的数据有误，请检查", errors),
				HttpStatus.OK);
	}

	public static HashMap<String, String> extractErros(BindingResult br) {
		HashMap<String, String> errors = Maps.newHashMap();
		List<FieldError> fieldErrors = br.getFieldErrors();
		for (FieldError fieldError : fieldErrors) {
			String field = fieldError.getField();
			String defaultMessage = fieldError.getDefaultMessage();
			errors.put(field, defaultMessage);
		}
		return errors;
	}

}