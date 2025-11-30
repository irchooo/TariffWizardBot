package ru.spbkt.tariff.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TariffDetailId implements Serializable {

    @Column(name = "tariff_id")
    private Integer tariffId;

    @Column(name = "parameter_id")
    private Integer parameterId;

}
