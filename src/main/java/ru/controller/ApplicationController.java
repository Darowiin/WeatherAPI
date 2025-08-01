package ru.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.entity.User;
import ru.service.SessionService;

import java.util.Arrays;
import java.util.UUID;

@Controller
public class ApplicationController {
    private final SessionService sessionService;

    @Autowired
    public ApplicationController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @RequestMapping("/error")
    public String getErrorPage() {
        return "error";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Arrays.stream(request.getCookies())
                .filter(cookie -> "sessionId".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .map(UUID::fromString).ifPresent(sessionService::invalidateSession);
        Cookie cookie = new Cookie("sessionId", null);
        response.addCookie(cookie);
        return "redirect:/";
    }

    @RequestMapping("/home")
    public String getHomePage(HttpServletRequest request, Model model) {
        UUID sessionId = Arrays.stream(request.getCookies())
                .filter(cookie -> "sessionId".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .map(UUID::fromString)
                .orElse(null);
        if (sessionId == null) {
            return "redirect:/error";
        }

        User user = sessionService.getUserBySessionId(sessionId);
        if (user == null) {
            return "redirect:/error";
        }
        model.addAttribute("username", user.getLogin());

        return "home";
    }
}
