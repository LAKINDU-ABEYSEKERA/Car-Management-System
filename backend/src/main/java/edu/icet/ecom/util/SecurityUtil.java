package edu.icet.ecom.util;

import edu.icet.ecom.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /**
     * Extracts the email of the currently logged-in user directly from the JWT Security Context.
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new BusinessException("No authenticated user found in the security context.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Remember, getUsername() returns the email in our setup!
        } else {
            return principal.toString();
        }
    }
}