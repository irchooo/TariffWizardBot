package ru.spbkt.bot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spbkt.bot.listener.TelegramBotListener;
import ru.spbkt.bot.service.ResponseSender;

@Slf4j
@Service
public class ResponseSenderImpl implements ResponseSender {

    private final TelegramBotListener bot;

    // Внедряем бота лениво, чтобы разорвать цикл Handler -> Sender -> Bot -> Dispatcher -> Handler
    public ResponseSenderImpl(@Lazy TelegramBotListener bot) {
        this.bot = bot;
    }

    @Override
    public void sendMessage(Long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    @Override
    public void sendMessage(Long chatId, String text, Object replyMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        if (replyMarkup instanceof ReplyKeyboard) {
            message.setReplyMarkup((ReplyKeyboard) replyMarkup);
        }
        execute(message);
    }

    @Override
    public void execute(SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения пользователю {}: {}", message.getChatId(), e.getMessage());
        }
    }
}
