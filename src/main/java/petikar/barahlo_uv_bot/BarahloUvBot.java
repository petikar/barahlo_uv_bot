package petikar.barahlo_uv_bot;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import petikar.barahlo_uv_bot.service.ForwardMessageService;
import petikar.barahlo_uv_bot.service.SendMessageService;

@Component
public class BarahloUvBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    SendMessageService sendMessageService;
    ForwardMessageService forwardMessageService;

    public BarahloUvBot(SendMessageService sendMessageService, ForwardMessageService forwardMessageService) {
        this.sendMessageService = sendMessageService;
        this.forwardMessageService = forwardMessageService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText() && update.getMessage().getText().equals("/findAndForward")) {
                for (ForwardMessage message : forwardMessageService.findAndForwardDuplicates()) {
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

            } else if (update.getMessage().hasText() && update.getMessage().getText().equals("/groupByUserId")) {

                for (SendMessage message : sendMessageService.findDuplicatesAndSendMeList()) {
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    execute(forwardMessageService.createForwardMessage(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

}

