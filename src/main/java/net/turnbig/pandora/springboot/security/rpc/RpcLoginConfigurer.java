package net.turnbig.pandora.springboot.security.rpc;

import net.turnbig.pandora.springboot.security.rpc.filter.JsonPayloadAuthenticationFilter;
import net.turnbig.pandora.springboot.security.rpc.handler.RpcAuthenticationPostHandler;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class RpcLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
    AbstractAuthenticationFilterConfigurer<H, RpcLoginConfigurer<H>, JsonPayloadAuthenticationFilter> {

  private final StringRedisTemplate stringRedisTemplate;

  public RpcLoginConfigurer(String usernamePath, String passwordPath, StringRedisTemplate stringRedisTemplate) {
    this(usernamePath, passwordPath, null, stringRedisTemplate);
  }

  /**
   * construct a spring-security RPC style AuthenticationFilterConfigurer
   * <p>
   * Note:: json path should in format of (JsonPath)[https://github.com/json-path/JsonPath]
   *
   * @param usernamePath       json path of payload for username
   * @param passwordPath       json path of payload for password
   * @param loginProcessingUrl login action processing URL that spring-security should watch
   * @param stringRedisTemplate redis-template used for token operations
   */
  public RpcLoginConfigurer(String usernamePath, String passwordPath, String loginProcessingUrl, StringRedisTemplate stringRedisTemplate) {
    super(new JsonPayloadAuthenticationFilter(usernamePath, passwordPath), loginProcessingUrl);
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  public void init(H http) throws Exception {
    super.init(http);

    // setup authentication post handler
    RpcAuthenticationPostHandler handler = new RpcAuthenticationPostHandler(this.stringRedisTemplate);
    this.successHandler(handler);
    this.failureHandler(handler);
  }

  @Override
  protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
    return new AntPathRequestMatcher(loginProcessingUrl, "POST");
  }

  public RpcLoginConfigurer<H> usernamePath(String usernamePath) {
    getAuthenticationFilter().setUsernamePath(usernamePath);
    return this;
  }

  public RpcLoginConfigurer<H> passwordPath(String passwordPath) {
    getAuthenticationFilter().setPasswordPath(passwordPath);
    return this;
  }

}
