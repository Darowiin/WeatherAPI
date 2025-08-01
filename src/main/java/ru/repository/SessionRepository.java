package ru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.entity.Session;
import ru.entity.User;

import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Session findByUser(User user);
    Session findById(UUID id);
}
