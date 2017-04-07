/**
 * @(#)SpringmvcExceptionResolver.java 2014年3月13日
 *
 * Copyright 2008-2014 by Woo Cupid.
 * All rights reserved.
 * 
 */
package net.turnbig.pandora.web.springmvc.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * 
 * Just a resolver to log exception for all exception.
 * 
 * @author Woo Cupid
 * @date 2014年3月13日
 * @version $Revision$
 */
public class LoggingExceptionResolver extends SimpleMappingExceptionResolver implements Ordered {

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		ModelAndView doResolveException = super.doResolveException(request, response, handler, ex);
		logger.error("unexpect exception throws", ex);
		return doResolveException;
	}

	// public int getOrder() {
	// return HIGHEST_PRECEDENCE;
	// }

}
