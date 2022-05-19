package petikar.barahlo_uv_bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import petikar.barahlo_uv_bot.entity.MessageDTO;

import java.util.Set;

@Service
public interface HistoryService {

    Set<ForwardMessage> findAndForwardDuplicates();

    ForwardMessage sendMessage(MessageDTO dto);

    SendMessage sendMessage(String text);

}
