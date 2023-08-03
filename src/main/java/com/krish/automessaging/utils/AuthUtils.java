package com.krish.automessaging.utils;

import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/**
 * The Class AuthUtils.
 */
@Component
public class AuthUtils {

    /**
     * Gets the logged in user id.
     *
     * @return the logged in user id
     */
    public String getLoggedInUserId() {
        Authentication contextHolder = SecurityContextHolder.getContext().getAuthentication();
        return Objects.nonNull(contextHolder) ? contextHolder.getName() : null;
    }

    /**
     * Gets the client IP.
     *
     * @param request
     *            the request
     *
     * @return the client IP
     */
    public String getClientIP(HttpServletRequest request) {
        if (Objects.nonNull(request)) {
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            return Objects.isNull(ipAddress) ? request.getRemoteAddr() : ipAddress;
        }
        return null;
    }
}
