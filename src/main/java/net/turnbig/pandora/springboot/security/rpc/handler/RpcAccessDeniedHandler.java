package net.turnbig.pandora.springboot.security.rpc.handler;

import net.turnbig.pandora.mapper.JsonMapper;
import net.turnbig.pandora.web.Servlets;
import net.turnbig.pandora.web.springmvc.view.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RpcAccessDeniedHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

  public static final int CODE_LOGIN_REQUIRED = 4010;
  public static final int CODE_NOT_PERMIT = 4011;

  /**
   * Access denied handler when user does not login
   *
   * @param request
   * @param response
   * @param authException
   * @throws IOException
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    Result loginRequired = Result.failed(CODE_LOGIN_REQUIRED, "login required");
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    Servlets
        .output(response, MediaType.APPLICATION_JSON_VALUE, JsonMapper.nonEmptyMapper().toJson(loginRequired));
  }


  /**
   * Access denied handler when user has login
   *
   * @param request
   * @param response
   * @param accessDeniedException
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    Result loginRequired = Result.failed(CODE_NOT_PERMIT, "not permit");
    response.setStatus(HttpStatus.FORBIDDEN.value());
    Servlets
        .output(response, MediaType.APPLICATION_JSON_VALUE, JsonMapper.nonEmptyMapper().toJson(loginRequired));
  }
}
