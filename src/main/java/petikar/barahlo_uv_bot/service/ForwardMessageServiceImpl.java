package petikar.barahlo_uv_bot.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import petikar.barahlo_uv_bot.ConfigProperties;
import petikar.barahlo_uv_bot.entity.MessageDTO;
import petikar.barahlo_uv_bot.entity.MessageMapper;

import java.util.HashSet;
import java.util.Set;

@Service
public class ForwardMessageServiceImpl implements ForwardMessageService {

    private final MessageMapper mapper = Mappers.getMapper(MessageMapper.class);

    private final MessageService service;

    private final ConfigProperties configProperties;

    public ForwardMessageServiceImpl(MessageService service, ConfigProperties configProperties) {
        this.service = service;
        this.configProperties = configProperties;
    }

    @Override
    public ForwardMessage createForwardMessage(Update update) {
        ForwardMessage forwardMessage;
        if (update.hasMessage()) {
            Message message = update.getMessage();
            service.save(message);
            forwardMessage = ForwardMessage.builder()
                    .chatId(configProperties.getHistoryId())
                    .fromChatId(String.valueOf(message.getChatId()))
                    .messageId(message.getMessageId()
                    )
                    .build();
        } else {
            forwardMessage = null;
        }
        return forwardMessage;
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
                .chatId(configProperties.getHistoryId())
                .fromChatId(String.valueOf(message.getChatId()))
                .messageId(message.getMessageId()
                )
                .build();

        return forwardMessage;
    }

}
