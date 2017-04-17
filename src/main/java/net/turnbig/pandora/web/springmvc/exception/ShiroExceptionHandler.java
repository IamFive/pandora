package net.turnbig.pandora.web.springmvc.exception;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ShiroExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Not permit
	 * 
	 * @param request
	 * @param response
	 * @param hr
	 * @throws IOException
	 */
	@ExceptionHandler(value = { UnauthorizedException.class })
	public final void handleUnauthorizedException(ServletRequest request, HttpServletResponse response,
			HttpServletRequest hr) throws IOException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	/**
	 * Not login
	 * 
	 * @param request
	 * @param response
	 * @param hr
	 * @throws IOException
	 */
	@ExceptionHandler(value = { UnauthenticatedException.class })
	public final ModelAndView handleUnauthenticatedException(ServletRequest request, HttpServletResponse response,
			HttpServletRequest hr) throws IOException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		return null;
	}

}