package com.example.dentalclinicschedulingplatform.utils;

import com.example.dentalclinicschedulingplatform.entity.UserType;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@UtilityClass
public class SecurityUtils {
    public static boolean hasRole(UserType userType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return authorities.stream()
                    .anyMatch(authority -> authority.getAuthority().equals(userType.toString()));
        }
        return false;
    }

    public static String getRoleName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_NOT_FOUND"); // Default value if no roles found
        }
        return "NO_AUTHENTICATION";
    }
}
