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
import ru.entity.dto.RegistrationForm;
import ru.exception.DuplicateLoginException;
import ru.service.AuthorizationService;
import ru.service.RegistrationService;

import java.util.UUID;

@Controller
public class RegistrationController {
    private final RegistrationService registrationService;
    private final AuthorizationService authorizationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService, AuthorizationService authorizationService) {
        this.registrationService = registrationService;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/registration")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("/registration")
    public String processRegistration(@Valid RegistrationForm registrationForm,
                                      BindingResult bindingResult,
                                      Model model,
                                      HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "registration";
        }
        try {
            registrationService.save(registrationForm.username(), registrationForm.password());
        } catch (DuplicateLoginException e) {
            model.addAttribute("error", "Такой пользователь уже существует. Пожалуйста, выберите другое имя пользователя.");
            return "registration";
        }
        UUID sessionId = authorizationService.createSession(registrationForm.username(), registrationForm.password());
        setSessionCookie(response, sessionId);
        return "redirect:/home";
    }

    private void setSessionCookie(HttpServletResponse response, UUID sessionId) {
        Cookie cookie = new Cookie("sessionId", sessionId.toString());
        response.addCookie(cookie);
    }
}