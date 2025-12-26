package com.kz.magazine.interceptor;

import com.kz.magazine.service.VisitorLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class VisitorInterceptor implements HandlerInterceptor {

    private final VisitorLogService visitorLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Exclude static resources and API calls if needed?
        // Usually we want to track page loads. But this is an API server.
        // If we track every API call, PV will be huge.
        // We should track only "Main" content fetching or explicit "Page View" events?
        // Or specific endpoints like /api/contents/{id} and /api/contents (list)?
        // For simplicity, let's track all GET requests to /api/contents/** and
        // /api/events/**

        String uri = request.getRequestURI();
        if (request.getMethod().equals("GET") && (uri.startsWith("/api/contents") || uri.startsWith("/api/events"))) {
            String ip = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            visitorLogService.logVisit(ip, userAgent);
        }
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
