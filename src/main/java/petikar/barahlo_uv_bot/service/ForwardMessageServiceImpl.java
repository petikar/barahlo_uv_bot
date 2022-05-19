package petikar.barahlo_uv_bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class ForwardMessageServiceImpl implements ForwardMessageService {

    private final MessageService service;

    @Value("${bot.historyId}")
    private String historyId;

    public ForwardMessageServiceImpl(MessageService service) {
        this.service = service;
    }

    @Override
    public ForwardMessage forwardMessage(Update update) {
        ForwardMessage forwardMessage;
        if (update.hasMessage()) {
            Message message = update.getMessage();
            service.save(message);
            /*try {
                bot.execute(ForwardMessage.builder()
                        .chatId(bot.getHistoryId())
                        .fromChatId(String.valueOf(message.getChatId()))
                        .messageId(message.getMessageId()
                        )
                        .build());
                service.save(message);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }*/
            forwardMessage = ForwardMessage.builder()
                    .chatId(historyId)
                    .fromChatId(String.valueOf(message.getChatId()))
                    .messageId(message.getMessageId()
                    )
                    .build();
        } else {
            forwardMessage = null;
        }
        return forwardMessage;
    }
}
