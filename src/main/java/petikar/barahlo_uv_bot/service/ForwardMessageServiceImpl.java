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

    private final MessageService messageService;

    private final ConfigProperties configProperties;

    public ForwardMessageServiceImpl(MessageService messageService, ConfigProperties configProperties) {
        this.messageService = messageService;
        this.configProperties = configProperties;
    }

    @Override
    public ForwardMessage createForwardMessage(Update update) {
        ForwardMessage forwardMessage = null;

        //TODO mediaGroupId
        if (update.hasMessage()) {
            Message message = update.getMessage();

            forwardMessage = ForwardMessage.builder()
                    .chatId(configProperties.getHistoryId())
                    .fromChatId(String.valueOf(message.getChatId()))
                    .messageId(message.getMessageId())
                    .build();
            messageService.save(message);

        } else if (update.hasEditedMessage()) {
            Message message = update.getEditedMessage();
            forwardMessage = ForwardMessage.builder()
                    .chatId(configProperties.getHistoryId())
                    .fromChatId(String.valueOf(message.getChatId()))
                    .messageId(message.getMessageId()
                    )
                    .build();
            messageService.save(message);
        }

        return forwardMessage;
    }

    @Override
    public Set<ForwardMessage> findAndForwardDuplicates() {
        Set<MessageDTO> repeatMessagesDTO = messageService.findRepeatMessages();
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
                .messageId(message.getMessageId())
                .build();

        return forwardMessage;
    }

}
