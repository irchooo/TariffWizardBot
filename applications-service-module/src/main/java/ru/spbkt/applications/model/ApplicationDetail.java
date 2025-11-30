package ru.spbkt.applications.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "application_details")
public class ApplicationDetail {

    @EmbeddedId
    private ApplicationDetailId id = new ApplicationDetailId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customApplicationId") // Связывает поле id.customApplicationId с этим объектом
    @JoinColumn(name = "custom_application_id")
    private CustomApplication customApplication;

    // Loose Coupling: ID параметра из модуля tariff-service
    // Мы дублируем его здесь как поле (read-only), так как оно часть ключа
    @Column(name = "parameter_id", insertable = false, updatable = false)
    private Integer parameterId;

    @Column(nullable = false)
    private Integer volume;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;
}
