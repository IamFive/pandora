package net.turnbig.pandora.springboot.security.rpc.filter;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JsonPayloadAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


  /**
   * json path for username
   */
  String usernamePath;

  /**
   * json path for password
   */
  String passwordPath;


  /**
   * json path should in format of (JsonPath)[https://github.com/json-path/JsonPath]
   *
   * @param usernamePath json path of payload for username
   * @param passwordPath json path of payload for password
   */
  public JsonPayloadAuthenticationFilter(String usernamePath, String passwordPath) {
    this.usernamePath = usernamePath;
    this.passwordPath = passwordPath;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
      AuthenticationException {
    UsernamePasswordAuthenticationToken token;
    try (InputStream is = request.getInputStream()) {
      DocumentContext payload = JsonPath.parse(is);
      String username = payload.read(usernamePath, String.class);
      String password = payload.read(passwordPath, String.class);
      token = new UsernamePasswordAuthenticationToken(username, password);
    } catch (IOException e) {
      log.error("Could not load authentication payload", e);
      token = new UsernamePasswordAuthenticationToken("", "");
    }

    setDetails(request, token);

    return this.getAuthenticationManager().authenticate(token);
  }


  public void setUsernamePath(String usernamePath) {
    this.usernamePath = usernamePath;
  }

  public void setPasswordPath(String passwordPath) {
    this.passwordPath = passwordPath;
  }

}
