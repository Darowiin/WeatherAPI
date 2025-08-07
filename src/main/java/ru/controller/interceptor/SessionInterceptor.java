package ru.controller.interceptor;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.entity.User;
import ru.exception.SessionNotFoundException;
import ru.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.UUID;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    private final SessionService sessionService;

    @Autowired
    public SessionInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            try {
                UUID sessionId = Arrays.stream(cookies)
                        .filter(cookie -> "sessionId".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .map(UUID::fromString)
                        .orElseThrow(() -> new SessionNotFoundException("Invalid session ID"));

                User user = sessionService.getUserBySessionId(sessionId);
                request.setAttribute("sessionId", sessionId);
                request.setAttribute("user", user);
                return true;
            } catch (SessionNotFoundException e) {
                response.sendRedirect("/authorization");
                return false;
            }
        } else {
            response.sendRedirect("/authorization");
            return false;
        }
    }
}
