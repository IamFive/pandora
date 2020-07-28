package net.turnbig.pandora.springboot.security.rpc;

public class RpcSecurityConstants {

  public static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";

  /**
   * redis hash key for "spring security authentication token"
   */
  public static final String AuthenticationTokenRedisKeyFormat = "spring:security:token:%s:%s";

  public static final String PATH_USERNAME = "$.username";
  public static final String PATH_PASSWORD = "$.password";

  public static String getAuthenticationTokenRedisKeyName(String token, String userId) {
    return String.format(AuthenticationTokenRedisKeyFormat, token, userId);
  }

}
