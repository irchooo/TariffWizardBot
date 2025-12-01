package ru.spbkt.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Свойства конфигурации для Telegram-бота, считываемые из application.yaml.
 * Префикс: app.bot
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.bot")
public class BotProperties {
    /** Токен бота, должен браться из переменной окружения ${BOT_TOKEN} */
    private String token;
    /** Имя бота, должно браться из переменной окружения ${BOT_USERNAME} */
    private String username;
}
