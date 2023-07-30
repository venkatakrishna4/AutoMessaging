package com.krish.automessaging.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthUtils {

    public String getLoggedInUserId() {
        Authentication contextHolder = SecurityContextHolder.getContext().getAuthentication();
        return Objects.nonNull(contextHolder) ? contextHolder.getName() : null;
    }

    public String getClientIP(HttpServletRequest request) {
        if (Objects.nonNull(request)) {
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            return Objects.isNull(ipAddress) ? request.getRemoteAddr() : ipAddress;
        }
        return null;
    }
}
