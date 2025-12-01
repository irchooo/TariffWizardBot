package ru.spbkt.bot.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Временная модель для хранения выбранных параметров при создании кастомного тарифа.
 * Аналогично CustomTariffRequest, но для хранения состояния.
 */
@Data
public class TariffDraft implements Serializable {
    /** Количество Гб (0, 10, 20, ...) */
    private Integer internetGb;
    /** Количество минут (0, 200, 500, ...) */
    private Integer minutes;
    /** Количество СМС (0, 50, 100, ...) */
    private Integer sms;
}
