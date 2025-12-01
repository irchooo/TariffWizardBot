package ru.spbkt.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Свойства конфигурации для адресов других сервисов.
 * Префикс: services
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {
    /** Адрес Client-service-module: http://localhost:8081/api/v1/clients */
    private Service client = new Service();
    /** Адрес Tariff-service-module: http://localhost:8080/api/v1 */
    private Service tariff = new Service();
    /** Адрес Applications-service-module: http://localhost:8082/api/v1/applications */
    private Service application = new Service();

    @Data
    public static class Service {
        private String url;
    }
}
