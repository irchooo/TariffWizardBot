package ru.spbkt.applications.model;

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
public class ApplicationDetailId implements Serializable {

    @Column(name = "custom_application_id")
    private Long customApplicationId;

    @Column(name = "parameter_id")
    private Integer parameterId;
}
