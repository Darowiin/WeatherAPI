package service;

import config.TestConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.entity.dto.RegistrationForm;
import ru.exception.DuplicateLoginException;
import ru.repository.UserRepository;
import ru.service.RegistrationService;
import ru.service.SessionService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegistrationServiceTest {
    private RegistrationForm registrationForm;

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionService sessionService;

    @Test
    public void shouldSaveUser() {
        registrationForm = new RegistrationForm("user1", "password", "password");
        registrationService.save(registrationForm.username(), registrationForm.password());
        assertTrue(userRepository.existsByLogin("user1"));
    }

    @Test
    public void shouldRejectDuplicate() {
        registrationForm = new RegistrationForm("user1", "password", "password");
        registrationService.save(registrationForm.username(), registrationForm.password());
        assertThrows(DuplicateLoginException.class, () -> registrationService.save(registrationForm.username(), registrationForm.password()));
    }

    @Test
    public void shouldRejectInvalidUsername() {
        RegistrationForm registrationForm = new RegistrationForm("us", "password", "password");


        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(registrationForm);

        assertFalse(violations.isEmpty());
        assertEquals("username", violations.iterator().next().getPropertyPath().toString());

        factory.close();
    }

    @Test
    public void shouldRejectInvalidPassword() {
        RegistrationForm registrationForm = new RegistrationForm("user1", "pass", "pass");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(registrationForm);

        assertFalse(violations.isEmpty());
        assertEquals("password", violations.iterator().next().getPropertyPath().toString());

        factory.close();
    }
}
