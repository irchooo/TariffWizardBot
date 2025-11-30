package ru.spbkt.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.applications.dto.request.FixedTariffApplicationRequest;
import ru.spbkt.bot.integration.ApplicationServiceClient;
import ru.spbkt.bot.integration.TariffServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.util.KeyboardFactory;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TariffCatalogHandler implements InputHandler {

    private final TariffServiceClient tariffService;
    private final ApplicationServiceClient applicationService;
    private final ResponseSender responseSender;

    @Override
    public List<BotState> getSupportedStates() {
        return List.of(BotState.CATALOG_VIEW);
    }

    public void showCatalog(UserContext context) {
        var tariffs = tariffService.getAllTariffs();
        SendMessage msg = new SendMessage(context.getChatId().toString(), "📦 Доступные тарифы:");
        msg.setReplyMarkup(KeyboardFactory.createTariffListKeyboard(tariffs));
        responseSender.sendMessage(msg);
    }

    @Override
    public void handle(Update update, UserContext context) {
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            if (data.startsWith("TARIFF_")) {
                Integer tariffId = Integer.parseInt(data.split("_")[1]);
                createApplication(context, tariffId);
            }
        }
    }

    private void createApplication(UserContext context, Integer tariffId) {
        FixedTariffApplicationRequest req = new FixedTariffApplicationRequest();
        req.setClientId(context.getClientId());
        req.setTariffId(tariffId);

        try {
            var app = applicationService.createFixedApplication(req);
            responseSender.sendMessage(context.getChatId(), "✅ Заявка №" + app.getId() + " успешно создана!");
            context.setCurrentState(BotState.MAIN_MENU);
        } catch (Exception e) {
            responseSender.sendMessage(context.getChatId(), "Ошибка создания заявки: " + e.getMessage());
        }
    }
}
