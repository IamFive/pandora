package net.turnbig.pandora.springboot.security.rpc.handler;

import net.turnbig.pandora.mapper.JsonMapper;
import net.turnbig.pandora.web.Servlets;
import net.turnbig.pandora.web.springmvc.view.Result;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RpcLogoutHandler implements LogoutHandler, LogoutSuccessHandler {

  StringRedisTemplate stringRedisTemplate;

  public RpcLogoutHandler(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  /**
   * processed before logout
   *
   * @param request
   * @param response
   * @param authentication
   */
  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

  }

  /**
   * processed after logout
   *
   * @param request
   * @param response
   * @param authentication
   * @throws IOException
   */
  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    Result success = Result.success();
    Servlets.output(response, MediaType.APPLICATION_JSON_VALUE, JsonMapper.nonEmptyMapper().toJson(success));
  }
}