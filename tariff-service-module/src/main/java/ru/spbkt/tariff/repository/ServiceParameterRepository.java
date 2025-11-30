package ru.spbkt.tariff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbkt.tariff.model.ServiceParameter;

import java.util.List;

@Repository
public interface ServiceParameterRepository extends JpaRepository<ServiceParameter, Integer> {

    List<ServiceParameter> findAllByIsActiveTrue();

    boolean existsByName(String name);

}
