package petikar.barahlo_uv_bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Set;

public interface SendMessageService {
    /**
     * This service is used when you need to use the SendMessage object
     */

    SendMessage createSendMessageImportant(String text);

    SendMessage createSendMessageHistory(String text);

    Set<SendMessage> findDuplicatesAndSendMeList();

    Set<SendMessage> findDuplicatesAndSendMeListForCommercialSender(Long userId);
}
