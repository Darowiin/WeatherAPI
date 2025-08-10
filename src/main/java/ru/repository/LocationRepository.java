package ru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.entity.Location;
import ru.entity.User;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByUser(User user);
    boolean existsById(Integer id);
    void deleteById(Integer id);
    boolean existsByNameAndUser(String locationName, User user);
}
