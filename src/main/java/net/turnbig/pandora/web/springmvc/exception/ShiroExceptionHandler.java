package net.turnbig.pandora.web.springmvc.exception;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ShiroExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { UnauthorizedException.class })
	public final ModelAndView handleException(ServletRequest request, HttpServletResponse response,
			HttpServletRequest hr) throws IOException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		return null;
	}

}