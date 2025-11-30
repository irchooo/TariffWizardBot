package ru.spbkt.applications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationsApplication {

    public static void main(String[] args) {
        // Проверка видимости модулей перед запуском Spring
        if (!checkModuleVisibility()) {
            System.exit(1); // Останавливаем если модули не видны
        }

        SpringApplication.run(ApplicationsApplication.class, args);
    }

    private static boolean checkModuleVisibility() {
        System.out.println("🔍 Проверка видимости модулей...");

        try {
            // Проверяем основные классы приложений
            Class<?> clientAppClass = Class.forName("ru.spbkt.client.ClientApplication");
            Class<?> tariffAppClass = Class.forName("ru.spbkt.tariff.TariffApplication");

            // Проверяем тестовые классы
            Class<?> testClientClass = Class.forName("ru.spbkt.client.TestClientModule");
            Class<?> testTariffClass = Class.forName("ru.spbkt.tariff.TestTariffModule");

            System.out.println("✅ Модуль клиентов: " + clientAppClass.getName());
            System.out.println("✅ Модуль тарифов: " + tariffAppClass.getName());
            System.out.println("✅ Тестовый класс клиентов: " + testClientClass.getName());
            System.out.println("✅ Тестовый класс тарифов: " + testTariffClass.getName());

            // Проверяем статические методы
            Object clientInfo = testClientClass.getMethod("getModuleInfo").invoke(null);
            Object tariffInfo = testTariffClass.getMethod("getModuleInfo").invoke(null);

            System.out.println("📋 Инфо клиентов: " + clientInfo);
            System.out.println("📋 Инфо тарифов: " + tariffInfo);
            System.out.println("🎉 Все модули корректно видят друг друга!");

            return true;

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Модуль не найден: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Ошибка доступа к модулю: " + e.getMessage());
            return false;
        }
    }

}
