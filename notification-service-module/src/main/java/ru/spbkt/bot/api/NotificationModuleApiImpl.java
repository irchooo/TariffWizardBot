package ru.spbkt.bot.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.spbkt.bot.service.ResponseSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationModuleApiImpl implements NotificationModuleApi {

    private final ResponseSender responseSender;

    @Override
    public void sendNotification(Long telegramId, String text) {
        log.info("Sending notification to user {}: {}", telegramId, text);
        // Используем сервис отправки, который мы реализовали ранее
        responseSender.sendMessage(telegramId, text);
    }
}
