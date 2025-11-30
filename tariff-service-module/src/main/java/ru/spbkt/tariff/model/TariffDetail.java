package ru.spbkt.tariff.model;

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
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tariff_details")
public class TariffDetail {

    @EmbeddedId
    private TariffDetailId id = new TariffDetailId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tariffId") // Связывает с полем tariffId внутри TariffDetailId
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parameterId") // Связывает с полем parameterId внутри TariffDetailId
    @JoinColumn(name = "parameter_id")
    private ServiceParameter parameter;

    @Column(nullable = false)
    private Integer volume;

    @Column(name = "price_coefficient", nullable = false, precision = 4, scale = 2)
    private BigDecimal priceCoefficient = BigDecimal.valueOf(1.0);

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
