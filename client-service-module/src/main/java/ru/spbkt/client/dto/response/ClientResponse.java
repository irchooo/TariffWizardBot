package ru.spbkt.client.dto.response;

import lombok.Data;

@Data
public class ClientResponse {

    private Long id;
    private Long telegramId;
    private String telegramUsername;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    private String statusName;

    private Integer currentTariffId;
}
