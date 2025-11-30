package ru.spbkt.tariff.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbkt.tariff.model.Tariff;

import java.util.List;
import java.util.Optional;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Integer> {

    @EntityGraph(attributePaths = "details")
    List<Tariff> findAllByIsActiveTrue();

    @EntityGraph(attributePaths = "details")
    Optional<Tariff> findById(Integer id);

}
