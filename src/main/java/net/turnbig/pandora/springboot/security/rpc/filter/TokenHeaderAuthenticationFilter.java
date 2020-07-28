package net.turnbig.pandora.springboot.security.rpc.filter;

import lombok.extern.slf4j.Slf4j;
import net.turnbig.pandora.springboot.security.rpc.RpcSecurityConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenHeaderAuthenticationFilter extends OncePerRequestFilter {

  WebAuthenticationDetailsSource webAuthenticationDetailsSource = new WebAuthenticationDetailsSource();

  StringRedisTemplate redisTemplate;
  UserDetailsService userDetailsService;

  public TokenHeaderAuthenticationFilter(StringRedisTemplate redisTemplate, UserDetailsService userDetailsService) {
    this.redisTemplate = redisTemplate;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = request.getHeader(RpcSecurityConstants.HEADER_X_AUTH_TOKEN);
    if (token != null) {
      String tokenRedisHashKey = RpcSecurityConstants.getAuthenticationTokenRedisKeyName(token, "*");
      Set<String> keys = this.redisTemplate.keys(tokenRedisHashKey);
      if (keys != null && keys.size() == 1) {
        ValueOperations<String, String> tokenOps = redisTemplate.opsForValue();
        String tokenRedisKey = keys.iterator().next();
        String username = tokenOps.get(tokenRedisKey);
        // TODO(turnbig)
        this.redisTemplate.expire(tokenRedisKey, 30, TimeUnit.MINUTES);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          // In this case, we reload user every time.
          // May we use offline data from redis (if we put user profile into redis)?
          UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
          authentication.setDetails(webAuthenticationDetailsSource.buildDetails(
              request));
          logger.info("Authenticate user `" + username + "` by token successfully.");
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
