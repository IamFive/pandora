package net.turnbig.pandora.springboot.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

public class ShiroLikeMethodSecurityConfiguration {

  /**
   * Creates the root object for expression evaluation.
   */
  @Component
  static class ShiroLikeMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
        Authentication authentication, MethodInvocation invocation) {
      ShiroLikeMethodSecurityExpressionRoot root = new ShiroLikeMethodSecurityExpressionRoot(
          authentication);
      root.setThis(invocation.getThis());
      root.setPermissionEvaluator(getPermissionEvaluator());
      root.setTrustResolver(getTrustResolver());
      root.setRoleHierarchy(getRoleHierarchy());
      root.setDefaultRolePrefix(getDefaultRolePrefix());
      return root;
    }
  }

  /**
   * Mostly logic from source class {@link org.springframework.security.access.expression.method.MethodSecurityExpressionRoot}, replace
   * default {@link SecurityExpressionRoot#hasAuthority(String)} and {@link SecurityExpressionRoot#hasAnyAuthority(String...)} expression to
   * support Apache-Shiro like wildcard authority like "*", "admin:*", "admin:user:*".
   */
  static class ShiroLikeMethodSecurityExpressionRoot implements MethodSecurityExpressionOperations {

    protected static final String WILDCARD_TOKEN = "*";
    protected static final String PART_DIVIDER_TOKEN = ":";
    protected static final String SUBPART_DIVIDER_TOKEN = ",";
    protected static final boolean DEFAULT_CASE_SENSITIVE = false;

    protected final Authentication authentication;
    private AuthenticationTrustResolver trustResolver;
    private RoleHierarchy roleHierarchy;
    private Set<String> roles;
    private String defaultRolePrefix = "ROLE_";

    private Object filterObject;
    private Object returnObject;
    private Object target;

    /**
     * Allows "permitAll" expression
     */
    public final boolean permitAll = true;

    /**
     * Allows "denyAll" expression
     */
    public final boolean denyAll = false;
    private PermissionEvaluator permissionEvaluator;
    public final String read = "read";
    public final String write = "write";
    public final String create = "create";
    public final String delete = "delete";
    public final String admin = "administration";

    /**
     * Creates a new instance
     *
     * @param authentication the {@link Authentication} to use. Cannot be null.
     */
    public ShiroLikeMethodSecurityExpressionRoot(Authentication authentication) {
      if (authentication == null) {
        throw new IllegalArgumentException("Authentication object cannot be null");
      }
      this.authentication = authentication;
    }

    public final boolean hasAuthority(String authority) {
      return hasAnyAuthority(authority);
    }

    public final boolean hasAnyAuthority(String... authorities) {
      return hasAnyAuthorityName(Arrays.asList(authorities));
    }

    public final boolean hasRole(String role) {
      return hasAnyRole(role);
    }

    public final boolean hasAnyRole(String... roles) {
      List<String> collect = Arrays.stream(roles)
          .map(role -> getRoleWithDefaultPrefix(defaultRolePrefix, role))
          .collect(Collectors.toList());
      return hasAnyAuthorityName(collect);
    }

    private boolean hasAnyAuthorityName(final List<String> authorities) {
      Set<String> authoritySet = getAuthoritySet();

      boolean exactMatches = authoritySet.stream().anyMatch(authorities::contains);
      if (exactMatches) {
        return true;
      }

      boolean wildcardMatches = authoritySet.stream().anyMatch(grantAuthority -> {
        // "*" matches everything
        if (WILDCARD_TOKEN.equals(grantAuthority)) {
          return true;
        }

        // exactly matches
        if (authorities.contains(grantAuthority)) {
          return true;
        }

        final List<List<String>> grantParts = getParts(grantAuthority, DEFAULT_CASE_SENSITIVE);
        return authorities.stream().anyMatch(requiredAuthority -> {
          List<List<String>> requiredParts = getParts(requiredAuthority, DEFAULT_CASE_SENSITIVE);

          int i = 0;
          for (List<String> otherPart : requiredParts) {
            // If this permission has less parts than the other permission, everything after the number of parts contained
            // in this permission is automatically implied, so return true
            if (grantParts.size() - 1 < i) {
              return true;
            } else {
              List<String> part = grantParts.get(i);
              if (!part.contains(WILDCARD_TOKEN) && !part.containsAll(otherPart)) {
                return false;
              }
              i++;
            }
          }

          // If this permission has more parts than the other parts, only imply it if all of the other parts are wildcards
          for (; i < grantParts.size(); i++) {
            List<String> part = grantParts.get(i);
            if (!part.contains(WILDCARD_TOKEN)) {
              return false;
            }
          }

          return true;
        });
      });

      return wildcardMatches;
    }

    protected List<List<String>> getParts(String wildcardString, boolean caseSensitive) {
      if (wildcardString == null || wildcardString.trim().isEmpty()) {
        throw new IllegalArgumentException("Wildcard string cannot be null or empty. Make sure permission strings are properly formatted.");
      }

      if (!caseSensitive) {
        wildcardString = wildcardString.toLowerCase();
      }

      List<List<String>> parts = new ArrayList<>();
      String[] mainParts = wildcardString.split(PART_DIVIDER_TOKEN);
      for (String part : mainParts) {
        String[] subParts = part.split(SUBPART_DIVIDER_TOKEN);
        if (subParts.length == 0) {
          throw new IllegalArgumentException(
              "Wildcard string cannot contain parts with only dividers. Make sure permission strings are properly formatted.");
        }
        parts.add(Arrays.asList(subParts));
      }

      if (parts.isEmpty()) {
        throw new IllegalArgumentException(
            "Wildcard string cannot contain only dividers. Make sure permission strings are properly formatted.");
      }

      return parts;
    }

    public final Authentication getAuthentication() {
      return authentication;
    }

    public final boolean permitAll() {
      return true;
    }

    public final boolean denyAll() {
      return false;
    }

    public final boolean isAnonymous() {
      return trustResolver.isAnonymous(authentication);
    }

    public final boolean isAuthenticated() {
      return !isAnonymous();
    }

    public final boolean isRememberMe() {
      return trustResolver.isRememberMe(authentication);
    }

    public final boolean isFullyAuthenticated() {
      return !trustResolver.isAnonymous(authentication)
          && !trustResolver.isRememberMe(authentication);
    }

    /**
     * Convenience method to access {@link Authentication#getPrincipal()} from {@link #getAuthentication()}
     *
     * @return
     */
    public Object getPrincipal() {
      return authentication.getPrincipal();
    }

    public void setTrustResolver(AuthenticationTrustResolver trustResolver) {
      this.trustResolver = trustResolver;
    }

    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
      this.roleHierarchy = roleHierarchy;
    }

    /**
     * <p>
     * Sets the default prefix to be added to {@link #hasAnyRole(String...)} or {@link #hasRole(String)}. For example, if hasRole("ADMIN")
     * or hasRole("ROLE_ADMIN") is passed in, then the role ROLE_ADMIN will be used when the defaultRolePrefix is "ROLE_" (default).
     * </p>
     *
     * <p>
     * If null or empty, then no default role prefix is used.
     * </p>
     *
     * @param defaultRolePrefix the default prefix to add to roles. Default "ROLE_".
     */
    public void setDefaultRolePrefix(String defaultRolePrefix) {
      this.defaultRolePrefix = defaultRolePrefix;
    }

    private Set<String> getAuthoritySet() {
      if (roles == null) {
        roles = new HashSet<>();
        Collection<? extends GrantedAuthority> userAuthorities = authentication
            .getAuthorities();

        if (roleHierarchy != null) {
          userAuthorities = roleHierarchy
              .getReachableGrantedAuthorities(userAuthorities);
        }

        roles = AuthorityUtils.authorityListToSet(userAuthorities);
      }

      return roles;
    }

    public boolean hasPermission(Object target, Object permission) {
      return permissionEvaluator.hasPermission(authentication, target, permission);
    }

    public boolean hasPermission(Object targetId, String targetType, Object permission) {
      return permissionEvaluator.hasPermission(authentication, (Serializable) targetId,
          targetType, permission);
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
      this.permissionEvaluator = permissionEvaluator;
    }

    /**
     * Prefixes role with defaultRolePrefix if defaultRolePrefix is non-null and if role does not already start with defaultRolePrefix.
     *
     * @param defaultRolePrefix
     * @param role
     * @return
     */
    private static String getRoleWithDefaultPrefix(String defaultRolePrefix, String role) {
      if (role == null) {
        return role;
      }
      if (defaultRolePrefix == null || defaultRolePrefix.length() == 0) {
        return role;
      }
      if (role.startsWith(defaultRolePrefix)) {
        return role;
      }
      return defaultRolePrefix + role;
    }

    public void setFilterObject(Object filterObject) {
      this.filterObject = filterObject;
    }

    public Object getFilterObject() {
      return filterObject;
    }

    public void setReturnObject(Object returnObject) {
      this.returnObject = returnObject;
    }

    public Object getReturnObject() {
      return returnObject;
    }

    /**
     * Sets the "this" property for use in expressions. Typically this will be the "this" property of the {@code JoinPoint} representing the
     * method invocation which is being protected.
     *
     * @param target the target object on which the method in is being invoked.
     */
    void setThis(Object target) {
      this.target = target;
    }

    public Object getThis() {
      return target;
    }
  }
}
