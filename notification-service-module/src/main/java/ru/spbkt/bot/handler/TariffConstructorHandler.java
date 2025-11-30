package ru.spbkt.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.applications.dto.request.CustomTariffApplicationRequest;
import ru.spbkt.bot.integration.ApplicationServiceClient;
import ru.spbkt.bot.integration.TariffServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.util.KeyboardFactory;
import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TariffConstructorHandler implements InputHandler {

    private final TariffServiceClient tariffService;
    private final ApplicationServiceClient applicationService;
    private final ResponseSender responseSender;

    @Override
    public List<BotState> getSupportedStates() {
        return List.of(BotState.CONSTRUCTOR_START, BotState.CONSTRUCTOR_GB, BotState.CONSTRUCTOR_MINUTES, BotState.CONSTRUCTOR_REVIEW);
    }

    public void startConstructor(UserContext context) {
        context.resetDraft();
        context.setCurrentState(BotState.CONSTRUCTOR_GB);
        askParameter(context, 1); // 1 = ID Интернета
    }

    private void askParameter(UserContext context, int parameterId) {
        // Получаем параметры (в реале кэшировать или брать из базы)
        var params = tariffService.getConstructorParameters();
        SendMessage msg = new SendMessage(context.getChatId().toString(), "Выберите объем: " + (parameterId == 1 ? "Интернет" : "Минуты"));
        msg.setReplyMarkup(KeyboardFactory.createParameterSelectionKeyboard(params, parameterId));
        responseSender.sendMessage(msg);
    }

    @Override
    public void handle(Update update, UserContext context) {
        if (!update.hasCallbackQuery()) return;

        String data = update.getCallbackQuery().getData();
        // data: PARAM_1_10 (paramId=1, volume=10)

        if (data.startsWith("PARAM_")) {
            String[] parts = data.split("_");
            Integer paramId = Integer.parseInt(parts[1]);
            Integer volume = Integer.parseInt(parts[2]);

            context.getDraft().setParameter(paramId, volume);

            if (paramId == 1) {
                // После Интернета переходим к Минутам
                context.setCurrentState(BotState.CONSTRUCTOR_MINUTES);
                askParameter(context, 2); // 2 = ID Минут
            } else if (paramId == 2) {
                // После Минут переходим к ревью
                context.setCurrentState(BotState.CONSTRUCTOR_REVIEW);
                showReview(context);
            }
        } else if (data.equals("ORDER_CONFIRM")) {
            submitOrder(context);
        } else if (data.equals("ORDER_CANCEL")) {
            context.setCurrentState(BotState.MAIN_MENU);
            responseSender.sendMessage(context.getChatId(), "Конструктор отменен.");
        }
    }

    private void showReview(UserContext context) {
        // Расчет цены
        CustomTariffRequest req = new CustomTariffRequest();
        List<CustomTariffRequest.SelectedParameter> params = new ArrayList<>();

        context.getDraft().getParameters().forEach((k, v) -> {
            var p = new CustomTariffRequest.SelectedParameter();
            p.setParameterId(k);
            p.setVolume(v);
            params.add(p);
        });
        req.setParameters(params);

        TariffCalculationResponse calc = tariffService.calculateCustomPrice(req);

        StringBuilder sb = new StringBuilder("📋 <b>Ваш тариф:</b>\n");
        calc.getDetails().forEach(d ->
                sb.append(String.format("- %s: %d %s (%s руб)\n", d.getParameterName(), d.getVolume(), d.getUnit(), d.getTotalItemCost()))
        );
        sb.append(String.format("\n<b>Итого: %s руб/мес</b>", calc.getTotalCost()));

        SendMessage msg = new SendMessage(context.getChatId().toString(), sb.toString());
        msg.setParseMode("HTML");
        msg.setReplyMarkup(KeyboardFactory.createConfirmOrderKeyboard());
        responseSender.sendMessage(msg);
    }

    private void submitOrder(UserContext context) {
        CustomTariffApplicationRequest req = new CustomTariffApplicationRequest();
        req.setClientId(context.getClientId());

        List<CustomTariffApplicationRequest.CustomParameterRequest> params = new ArrayList<>();
        context.getDraft().getParameters().forEach((k, v) -> {
            var p = new CustomTariffApplicationRequest.CustomParameterRequest();
            p.setParameterId(k);
            p.setVolume(v);
            params.add(p);
        });
        req.setParameters(params);

        try {
            var app = applicationService.createCustomApplication(req);
            responseSender.sendMessage(context.getChatId(), "✅ Кастомная заявка №" + app.getId() + " создана!");
            context.setCurrentState(BotState.MAIN_MENU);
        } catch (Exception e) {
            responseSender.sendMessage(context.getChatId(), "Ошибка: " + e.getMessage());
        }
    }
}
