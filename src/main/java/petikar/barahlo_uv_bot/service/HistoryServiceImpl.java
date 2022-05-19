package petikar.barahlo_uv_bot.service;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import petikar.barahlo_uv_bot.entity.MessageDTO;
import petikar.barahlo_uv_bot.entity.MessageMapper;

import java.util.HashSet;
import java.util.Set;

@Service
public class HistoryServiceImpl implements HistoryService {

    private final MessageService service;

    //TODO получать во всех классах из одного класса c настройками, не отдельно в каждом случае
    @Value("${bot.historyId}")
    private String historyId;

    private final MessageMapper mapper = Mappers.getMapper(MessageMapper.class);

    public HistoryServiceImpl(MessageService service) {
        this.service = service;
    }

    @Override
    public Set<ForwardMessage> findAndForwardDuplicates() {
        Set<MessageDTO> repeatMessagesDTO = service.findRepeatMessages();
        Set<ForwardMessage> repeatMessages = new HashSet<>();

        for (MessageDTO messageDTO : repeatMessagesDTO) {
            repeatMessages.add(sendMessage(messageDTO));
        }
        return repeatMessages;
    }

    @Override
    public ForwardMessage sendMessage(MessageDTO messageDTO) {

        Message message = mapper.toEntity(messageDTO);

        ForwardMessage forwardMessage = ForwardMessage.builder()
                .chatId(historyId)
                .fromChatId(String.valueOf(message.getChatId()))
                .messageId(message.getMessageId()
                )
                .build();

        return forwardMessage;
    }

    @Override
    public SendMessage sendMessage(String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(historyId);
        sendMessage.setText(text);

        return sendMessage;
    }
}
