package ru.spbkt.applications.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbkt.applications.model.ApplicationStatus;

import java.util.Optional;

@Repository
public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, Integer> {
    Optional<ApplicationStatus> findByName(String name);
}
