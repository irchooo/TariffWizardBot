package ru.spbkt.bot.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class TariffDraft implements Serializable {
    private final Map<Integer, Integer> parameters = new HashMap<>(); // ID параметра -> Volume
    private String clientFirstName;
    private String clientLastName;
    private String clientPhoneNumber;
    private Integer fixedTariffId;

    public void setParameter(Integer parameterId, Integer volume) {
        parameters.put(parameterId, volume);
    }
}
