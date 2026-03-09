package com.github.soillux.util;

import com.github.soillux.exception.custom.AuthenticationException;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class UserUtil {

  private static final String ANONYMOUS_USER = "anonymousUser";

  public static Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || isAnonymousUser(authentication)) {
      throw new AuthenticationException("Anonymous user is not allowed to access this resource");
    }

    return (Long) authentication.getPrincipal();
  }

  private boolean isAnonymousUser(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    return principal != null && principal.toString().equals(ANONYMOUS_USER);
  }
}
