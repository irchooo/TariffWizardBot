package ru.spbkt.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.integration.ClientServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuNavigationHandler implements InputHandler {

    private final TariffCatalogHandler catalogHandler;       // Делегируем логику
    private final TariffConstructorHandler constructorHandler; // Делегируем логику
    private final ResponseSender responseSender;
    private final ClientServiceClient clientService;

    @Override
    public List<BotState> getSupportedStates() {
        return List.of(BotState.MAIN_MENU);
    }

    @Override
    public void handle(Update update, UserContext context) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text = update.getMessage().getText();

        switch (text) {
            case "📊 Каталог" -> {
                context.setCurrentState(BotState.CATALOG_VIEW);
                catalogHandler.showCatalog(context);
            }
            case "🛠️ Конструктор" -> {
                context.setCurrentState(BotState.CONSTRUCTOR_START);
                constructorHandler.startConstructor(context);
            }
            case "👤 Профиль" -> {
                // Можно выделить в ProfileHandler, но для краткости тут
                var client = clientService.getClientByTelegramId(context.getChatId());
                if (client.isPresent()) {
                    var c = client.get();
                    String msg = String.format("👤 <b>Ваш профиль</b>\n\nИмя: %s %s\nСтатус: %s\nТариф: %s",
                            c.getFirstName(), c.getLastName(), c.getStatusName(),
                            c.getCurrentTariffId() != null ? "ID " + c.getCurrentTariffId() : "Не подключен");
                    responseSender.sendMessage(context.getChatId(), msg);
                }
            }
            case "📝 Заявки" -> responseSender.sendMessage(context.getChatId(), "Функционал просмотра заявок в разработке.");
            case "📞 Поддержка" -> responseSender.sendMessage(context.getChatId(), "Служба поддержки: +7 (800) 555-35-35\nEmail: support@spbkt.ru");
            default -> responseSender.sendMessage(context.getChatId(), "Используйте кнопки меню.");
        }
    }
}
