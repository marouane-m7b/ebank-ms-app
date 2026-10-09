package net.mahboub.ebankbot.telegram;

import jakarta.annotation.PostConstruct;
import net.mahboub.ebankbot.agents.EbankAIAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${telegram.token}")
    private String telegramBotToken;

    private final EbankAIAgent aiAgent;

    public TelegramBot(EbankAIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }

    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Unable to register Telegram bot", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "springproject45bot";
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update == null || !update.hasMessage() || update.getMessage().getChatId() == null) {
            return;
        }

        String messageText = update.getMessage().getText();
        if (messageText == null || messageText.isBlank()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        try {
            sendTypingQuestion(chatId);
            String answer = aiAgent.chat(messageText);
            sendTextMessage(chatId, answer == null ? "Je ne sais pas" : answer);
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Unable to respond to Telegram message", e);
        }
    }

    private void sendTextMessage(long chatId, String text) throws TelegramApiException {
        execute(new SendMessage(String.valueOf(chatId), text));
    }

    private void sendTypingQuestion(long chatId) throws TelegramApiException {
        SendChatAction action = new SendChatAction();
        action.setChatId(String.valueOf(chatId));
        action.setAction(ActionType.TYPING);
        execute(action);
    }
}
