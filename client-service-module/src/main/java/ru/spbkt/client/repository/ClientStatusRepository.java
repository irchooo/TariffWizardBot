package ru.spbkt.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbkt.client.model.ClientStatus;

import java.util.Optional;

@Repository
public interface ClientStatusRepository extends JpaRepository<ClientStatus, Integer> {

    // Нужен для поиска статуса "НОВЫЙ" при регистрации
    Optional<ClientStatus> findByName(String name);
}
