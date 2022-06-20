package petikar.barahlo_uv_bot.service;

import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import petikar.barahlo_uv_bot.entity.MessageDTO;

import java.util.Set;

public interface ForwardMessageService {

    /**
     * This service is used when you need to use the ForwardMessage object
     *
     */

    ForwardMessage createForwardMessage(Update update);

    Set<ForwardMessage> findAndForwardDuplicates();

    ForwardMessage sendMessage(MessageDTO dto);
}
