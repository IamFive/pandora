package net.turnbig.pandora.springboot.security.rpc;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.security.rpc")
public class SpringSecurityRpcProperties {

  String loginUrl;
  String logoutUrl;
  String[] anonUrls;
  boolean disableCsrf = true;

}