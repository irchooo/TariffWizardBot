package ru.spbkt.bot.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserContext implements Serializable {
    private final Long chatId;
    private BotState currentState;
    private TariffDraft draft;
    private Long clientId; // Внутренний ID клиента из client-service

    public UserContext(Long chatId) {
        this.chatId = chatId;
        this.currentState = BotState.START;
        this.draft = new TariffDraft();
    }

    public void resetDraft() {
        this.draft = new TariffDraft();
    }
}
