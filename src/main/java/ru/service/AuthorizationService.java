package ru.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.entity.Session;
import ru.entity.User;
import ru.repository.UserRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthorizationService {
    private final UserRepository userRepository;
    private final SessionService sessionService;

    @Autowired
    public AuthorizationService(UserRepository userRepository, SessionService sessionService) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
    }

    @Transactional
    public User findByLoginAndPassword(String login, String password) {
        return userRepository.findByLogin(login)
                .filter(user -> BCrypt.checkpw(password, user.getHashPassword()))
                .orElse(null);
    }

    @Transactional
    public UUID createSession(String login, String password) {
        User user = findByLoginAndPassword(login, password);
        if (user != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR_OF_DAY, 4);
            Date expiresAt = calendar.getTime();

            Session session = Session.builder()
                    .user(user)
                    .expiresAt(expiresAt)
                    .build();
            sessionService.save(session);
            return session.getId();
        }
        return null;
    }

    @Transactional
    public UUID getCorrectSessionId(String login, String password) {
        User user = findByLoginAndPassword(login, password);
        if (user != null) {
            Session session = sessionService.findByUser(user);
            if (session != null && sessionService.isSessionValid(session)) {
                return session.getId();
            } else if (session != null) {
                sessionService.deleteSession(session);
            }
            return createSession(login, password);
        } else {
            return null;
        }
    }
}