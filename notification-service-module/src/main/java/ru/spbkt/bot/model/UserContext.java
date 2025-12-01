package ru.spbkt.bot.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Контекст пользователя: хранит его текущее состояние и черновики данных.
 */
@Data
@NoArgsConstructor
public class UserContext implements Serializable {
    private Long telegramId;
    private Long chatId;

    /** Текущее состояние в диалоге */
    private BotState state = BotState.START;

    /** Черновик кастомного тарифа */
    private TariffDraft tariffDraft = new TariffDraft();

    /** Временное хранение ID готового тарифа */
    private Integer selectedFixedTariffId;

    /** Временное хранение ID заявки для отмены */
    private Long applicationIdToCancel;

    /** * Временное поле для регистрации (хранит Имя Фамилию, пока ждем телефон).
     * Добавлено согласно логике RegistrationHandler.
     */
    private String tempRegistrationData;

    public UserContext(Long telegramId, Long chatId) {
        this.telegramId = telegramId;
        this.chatId = chatId;
    }
}
