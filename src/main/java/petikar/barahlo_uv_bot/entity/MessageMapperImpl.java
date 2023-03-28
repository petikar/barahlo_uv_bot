package petikar.barahlo_uv_bot.entity;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import petikar.barahlo_uv_bot.service.MessageTextUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageDTO toDto(Message message) {
        MessageDTO dto = new MessageDTO();
        String text = MessageTextUtils.getTextFromMessage(message);
        dto.setText(text);
        dto.setIdMessage(message.getMessageId());
        dto.setDate(DateConverter.intToDate(message.getDate()));
        dto.setIdUser(message.getFrom().getId());

        if (message.getPhoto() != null) {
            int size = message.getPhoto().size()-1;
            dto.setPhotoSize(message.getPhoto().get(size).getFileSize());
        }

        dto.setChatId(message.getChatId());
        dto.setMediaGroupId(message.getMediaGroupId());

        return dto;

    }

    @Override
    public Message toEntity(MessageDTO dto) {
        Message message = new Message();
        message.setMessageId(dto.getIdMessage());
        message.setText(dto.getText());
        Chat chat = new Chat();
        chat.setId(dto.getChatId());
        message.setChat(chat);
        User user = new User();
        user.setId(dto.getIdUser());
        message.setFrom(user);

        message.setDate(DateConverter.dateToInteger(dto.getDate()));

        List<PhotoSize> sizes = new ArrayList<>();
        Integer size = dto.getPhotoSize();
        if (size != null) {

            PhotoSize photoSize = new PhotoSize();
            photoSize.setFileSize(size);
            sizes.add(photoSize);

        }
        message.setPhoto(sizes);
        message.setMediaGroupId(dto.getMediaGroupId());
        return message;
    }
}