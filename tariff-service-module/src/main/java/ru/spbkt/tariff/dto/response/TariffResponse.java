package ru.spbkt.tariff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffResponse {

    private Integer id;
    private String name;
    private String description;
    private List<TariffDetailResponse> details;

}
