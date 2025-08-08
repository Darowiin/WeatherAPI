package ru.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import ru.exception.InvalidLocationDataException;
import ru.exception.SessionNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SessionNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleSessionNotFound(SessionNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", "Your session has expired. Please sign in again.");
        model.addAttribute("errorCode", HttpStatus.UNAUTHORIZED.value());
        return "error";
    }

    @ExceptionHandler(InvalidLocationDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleInvalidLocationData(InvalidLocationDataException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("errorCode", HttpStatus.BAD_REQUEST.value());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Exception ex, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("errorDetails", ex.getMessage());
        return "error";
    }
}
