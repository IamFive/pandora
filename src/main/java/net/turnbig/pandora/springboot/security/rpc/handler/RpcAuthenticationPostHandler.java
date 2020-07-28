package net.turnbig.pandora.springboot.security.rpc.handler;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.turnbig.pandora.mapper.JsonMapper;
import net.turnbig.pandora.springboot.security.rpc.RpcSecurityConstants;
import net.turnbig.pandora.springboot.security.rpc.RpcUserDetails;
import net.turnbig.pandora.utils.Q;
import net.turnbig.pandora.web.Servlets;
import net.turnbig.pandora.web.springmvc.view.Result;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Post handler for "RPC authentication" including success and failure callback.
 */
@Slf4j
public class RpcAuthenticationPostHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

  StringRedisTemplate stringRedisTemplate;

  public RpcAuthenticationPostHandler(StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    log.info("User `{}` login successfully.", authentication.getName());
    RpcUserDetails userDetails = (RpcUserDetails) authentication.getPrincipal();

    // String token = request.getSession().getId();
    String token = null;
    while(true) {
      token = UUID.randomUUID().toString();
      Set<String> keys = this.stringRedisTemplate.keys(
          RpcSecurityConstants.getAuthenticationTokenRedisKeyName(token, "*"));
      if (keys == null || keys.size() == 0) {
        break;
      }
    }

    String authenticationTokenRedisKeyName = RpcSecurityConstants.getAuthenticationTokenRedisKeyName(token, userDetails.getIdentify());
    ValueOperations<String, String> tokenOps = this.stringRedisTemplate.opsForValue();
    tokenOps.set(authenticationTokenRedisKeyName, userDetails.getUsername());
    // TODO use configured timeout
    this.stringRedisTemplate.expire(authenticationTokenRedisKeyName, 30, TimeUnit.MINUTES);

    Result success = Result.success(Q.newHashMap("token", token, "profile", userDetails.profile()));
    Servlets.output(response, MediaType.APPLICATION_JSON_VALUE, JsonMapper.nonEmptyMapper().toJson(success));
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    log.info("User login failed, reason:: {}", exception.getMessage());
    Result failed = Result.failed(10000, exception.getMessage());
    String content = JsonMapper.nonEmptyMapper().toJson(failed);
    Servlets.output(response, MediaType.APPLICATION_JSON_VALUE, content);
  }
}