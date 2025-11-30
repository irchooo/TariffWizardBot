package ru.spbkt.tariff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbkt.tariff.model.TariffDetail;
import ru.spbkt.tariff.model.TariffDetailId;

@Repository
public interface TariffDetailRepository extends JpaRepository<TariffDetail, TariffDetailId> {

    // методы не нужны - детали сохраняются каскадно через Tariff

}
