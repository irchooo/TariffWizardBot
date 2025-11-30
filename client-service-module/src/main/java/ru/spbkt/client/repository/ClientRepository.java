package ru.spbkt.client.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbkt.client.model.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Поиск клиента по Telegram ID.
     * Используем EntityGraph, чтобы одним запросом достать клиента и его статус.
     */
    @EntityGraph(attributePaths = "status")
    Optional<Client> findByTelegramId(Long telegramId);

    /**
     * Проверка существования по номеру телефона (для валидации при регистрации).
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Проверка существования по Telegram ID.
     */
    boolean existsByTelegramId(Long telegramId);
}
