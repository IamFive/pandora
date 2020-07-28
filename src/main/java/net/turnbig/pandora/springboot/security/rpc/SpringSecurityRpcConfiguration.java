/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.turnbig.pandora.springboot.security.rpc;

import net.turnbig.pandora.springboot.security.ShiroLikeMethodSecurityConfiguration;
import net.turnbig.pandora.springboot.security.rpc.filter.JsonPayloadAuthenticationFilter;
import net.turnbig.pandora.springboot.security.rpc.filter.TokenHeaderAuthenticationFilter;
import net.turnbig.pandora.springboot.security.rpc.handler.RpcAccessDeniedHandler;
import net.turnbig.pandora.springboot.security.rpc.handler.RpcLogoutHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableConfigurationProperties(SpringSecurityRpcProperties.class)
@Import({ShiroLikeMethodSecurityConfiguration.class})
public class SpringSecurityRpcConfiguration extends WebSecurityConfigurerAdapter {

  private final SpringSecurityRpcProperties properties;
  private final UserDetailsService userDetailsService;
  private final StringRedisTemplate stringRedisTemplate;

  public SpringSecurityRpcConfiguration(SpringSecurityRpcProperties properties, UserDetailsService userDetailsService,
      StringRedisTemplate stringRedisTemplate) {
    this.properties = properties;
    this.userDetailsService = userDetailsService;
    this.stringRedisTemplate = stringRedisTemplate;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests(authorize -> {
              authorize
                  .antMatchers(HttpMethod.OPTIONS).permitAll()          // allow all option request
                  .antMatchers(properties.getAnonUrls()).permitAll()    // allow specified anon urls
                  .antMatchers(properties.getLoginUrl()).permitAll()    // allow login url
                  .antMatchers(properties.getLogoutUrl()).permitAll()   // allow login out
                  .anyRequest().authenticated();                        // default rule
            }
        )
        // use stateless session management
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    if (properties.isDisableCsrf()) {
      http.csrf().disable();
    }

    configureRpcAuthentication(http);
  }

  /**
   * configure RPC authentication workflow chain.
   *
   * <li>using {@link RpcLoginConfigurer} instead of default {@link FormLoginConfigurer}</li>
   * <li>using {@link JsonPayloadAuthenticationFilter} instead of default {@link UsernamePasswordAuthenticationFilter}</li>
   * <li>adapter access denied request handler</li>
   *
   * @param http HTTP request security configuration
   * @throws Exception if configure fails
   */
  private void configureRpcAuthentication(HttpSecurity http) throws Exception {
    // setup rpc login authentication configuration.
    RpcLoginConfigurer<HttpSecurity> rpcLoginConfigurer = new RpcLoginConfigurer<>(RpcSecurityConstants.PATH_USERNAME,
        RpcSecurityConstants.PATH_PASSWORD, properties.getLoginUrl(), stringRedisTemplate);
    http.apply(rpcLoginConfigurer);

    // setup HTTP header token RPC authentication filter
    TokenHeaderAuthenticationFilter tokenHeaderAuthenticationFilter
        = new TokenHeaderAuthenticationFilter(stringRedisTemplate, userDetailsService);
    http.addFilterBefore(tokenHeaderAuthenticationFilter, JsonPayloadAuthenticationFilter.class);

    // setup access denied rpc request handler
    RpcAccessDeniedHandler accessDeniedHandler = new RpcAccessDeniedHandler();
    http.exceptionHandling()
        .authenticationEntryPoint(accessDeniedHandler)
        .accessDeniedHandler(accessDeniedHandler);

    // setup rpc logout handler
    RpcLogoutHandler logoutHandler = new RpcLogoutHandler(stringRedisTemplate);
    http.logout()
        .logoutUrl(properties.getLogoutUrl())
        .addLogoutHandler(logoutHandler)
        .logoutSuccessHandler(logoutHandler);
  }

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

}
