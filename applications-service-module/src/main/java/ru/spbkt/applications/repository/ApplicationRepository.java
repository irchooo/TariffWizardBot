package ru.spbkt.applications.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbkt.applications.model.Application;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Жадная загрузка статуса и под-сущностей, чтобы избежать N+1
    @EntityGraph(attributePaths = {"status", "tariffApplication", "customApplication", "customApplication.details"})
    List<Application> findAllByClientId(Long clientId);

    @EntityGraph(attributePaths = {"status", "tariffApplication", "customApplication", "customApplication.details"})
    Optional<Application> findById(Long id);
}

