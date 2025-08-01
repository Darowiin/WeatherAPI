package ru.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.exception.DuplicateLoginException;
import ru.repository.UserRepository;
import ru.entity.User;

@Service
public class RegistrationService {
    UserRepository userRepository;

    @Autowired
    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void save(String username, String password) {

        if (userRepository.existsByLogin(username)) {
            throw new DuplicateLoginException("Login already exists: " + username);
        }
        String encodedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = User.builder()
                .login(username)
                .hashPassword(encodedPassword)
                .build();

        userRepository.save(user);
    }
}
