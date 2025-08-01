package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.entity.Session;
import ru.entity.User;
import ru.repository.SessionRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public void save(Session session) {
        sessionRepository.save(session);
    }

    @Transactional
    public Session findByUser(User user) {
        return sessionRepository.findByUser(user);
    }

    @Transactional
    public Session findById(UUID sessionId) {
        return sessionRepository.findById(sessionId);
    }

    @Transactional
    public void deleteSession(Session session) {
        sessionRepository.delete(session);
    }

    @Transactional
    public boolean isSessionValid(Session session) {
        return session != null && session.getExpiresAt().after(new Date());
    }

    @Transactional
    public User getUserBySessionId(UUID sessionId) {
        Session session = findById(sessionId);
        if (isSessionValid(session)) {
            return session.getUser();
        } else {
            if (session != null) {
                deleteSession(session);
            }
            return null;
        }
    }

    @Transactional
    public void invalidateSession(UUID sessionId) {
        Session session = findById(sessionId);
        if (session != null) {
            deleteSession(session);
        }
    }
}