package com.example.firsttestbot;

import com.example.firsttestbot.entyty.Currency;
import com.example.firsttestbot.service.CurrencyConversionService;
import com.example.firsttestbot.service.CurrencyModeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TestBot extends TelegramLongPollingBot {
    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    private final CurrencyConversionService conversionService = CurrencyConversionService.getInstance();

    public TestBot(String botToken) {
        super(botToken);
    }

    @Override
    public String getBotUsername() {
        return "@myFirstTestForJavaBot";
    }

    @SneakyThrows
    public static void main(String[] args) {
        TestBot bot = new TestBot("6376396781:AAE1SIQk9ZqmQu6y-BZr144l3UKX3s9e-6Q");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        Currency newCurrency = Currency.valueOf(param[1]);
        switch (action) {
            case "original":
                currencyModeService.setOriginalCurrency(message.getChatId(), newCurrency);
                log.info("в чате {} выбрана original валюта {}", message.getChatId(), newCurrency);
                break;
            case "target":
                currencyModeService.setTargetCurrency(message.getChatId(), newCurrency);
                log.info("в чате {} выбрана original валюта {}", message.getChatId(), newCurrency);
                break;
        }
        List<List<InlineKeyboardButton>> buttons = createButtons(message);
        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build()).build());
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity = message.getEntities().stream()
                    .filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                String command = message.getText()
                        .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                log.info("в чате {} выбрана комманда {}", message.getChatId(), command);
                switch (command) {
                    case "/set_currency":
                        List<List<InlineKeyboardButton>> buttons = createButtons(message);
                        execute(SendMessage.builder().chatId(message.getChatId())
                                .text("пожалуйста, выберите вашу валюту и валюту, в которую хотите конвертировать. Затем введите сумму")
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .build());
                }
            }
        }
        if (message.hasText()) {
            String messageTest = message.getText();
            Optional<Double> value = parseDouble(messageTest);
            Currency original = currencyModeService.getOriginalCurrency(message.getChatId());
            Currency target = currencyModeService.getTargetCurrency(message.getChatId());
            double rate = conversionService.getConversionRatio(original, target);
            if (value.isPresent()) {
                execute(SendMessage.builder().chatId(message.getChatId())
                        .text(String.format("%4.2f %s это %4.2f %s", value.get(), original, (value.get() * rate), target))
                        .build());
            }
        }
    }

    private Optional<Double> parseDouble(String messageText) {
        try {
            return Optional.of(Double.parseDouble(messageText));
        } catch (NumberFormatException exp) {
            return Optional.empty();
        }
    }

    private List<List<InlineKeyboardButton>> createButtons(Message message) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        Currency original = currencyModeService.getOriginalCurrency(message.getChatId());
        Currency target = currencyModeService.getTargetCurrency(message.getChatId());
        for (Currency currency : Currency.values()) {
            buttons.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(getCurrencyButton(original, currency)).callbackData("original:" + currency).build(),
                    InlineKeyboardButton.builder()
                            .text(getCurrencyButton(target, currency)).callbackData("target:" + currency).build()));

        }
        return buttons;
    }

    String getCurrencyButton(Currency saved, Currency current) {
        String currencyButton = current.name();
        switch (current.name()) {
            case "USD":
                currencyButton += "\uD83C\uDDFA\uD83C\uDDF8";
                break;
            case "GBP":
                currencyButton += "\uD83C\uDDEC\uD83C\uDDE7";
                break;
            case "JPY":
                currencyButton += "\uD83C\uDDEF\uD83C\uDDF5";
                break;
            case "RUB":
                currencyButton += "\uD83C\uDDF7\uD83C\uDDFA";
                break;
            case "EUR":
                currencyButton += "\uD83C\uDDEA\uD83C\uDDFA";
                break;
            case "CNY":
                currencyButton += "\uD83C\uDDE8\uD83C\uDDF3";
        }
        return saved == current ? currencyButton + " ✔️" : currencyButton;
    }
}
