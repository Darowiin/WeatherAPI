package ru.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.entity.dto.AuthorizationForm;
import ru.service.AuthorizationService;

import java.util.UUID;

@Controller
public class AuthorizationController {
    private final AuthorizationService authorizationService;

    @Autowired
    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "authorization";
    }

    @PostMapping("/login")
    public String processLogin(@Valid AuthorizationForm authorizationForm,
                               BindingResult bindingResult,
                               Model model,
                               HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "authorization";
        }
        UUID sessionId = authorizationService.getCorrectSessionId(authorizationForm.username(), authorizationForm.password());
        if (sessionId != null) {
            setSessionCookie(response, sessionId);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password.");
            return "authorization";
        }
    }

    private void setSessionCookie(HttpServletResponse response, UUID sessionId) {
        Cookie cookie = new Cookie("sessionId", sessionId.toString());
        response.addCookie(cookie);
    }
}
