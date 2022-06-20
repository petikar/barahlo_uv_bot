package petikar.barahlo_uv_bot.entity;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MessageMapperTest {

    private final MessageMapper mapper = new MessageMapperImpl();

    @Test
    public void toMessageTest() {

        //given
        MessageDTO messageDTO = new MessageDTO();
        LocalDateTime date = LocalDateTime.of(2022, 05, 10, 15, 48, 5);
        Integer intDate = 1652172485;

        messageDTO.setIdMessage(12);
        messageDTO.setText("uwiehjfsdnlsk");
        messageDTO.setDate(date);
        messageDTO.setIdUser(154L);
        messageDTO.setPhotoSize(948);
        messageDTO.setChatId(32L);

        //when
        Message message = mapper.toEntity(messageDTO);

        //then
        assertThat(message).isNotNull();
        assertThat(message.getFrom().getId()).isEqualTo(154L);
        assertThat(message.getMessageId()).isEqualTo(12L);
        assertThat(message.getText()).isEqualTo("uwiehjfsdnlsk");
        assertThat(message.getDate()).isEqualTo(intDate);
        assertThat(message.getChatId()).isEqualTo(32L);
        List<Integer> sizes = message.getPhoto().stream()
                .map(PhotoSize::getFileSize)
                .collect(Collectors.toList());
        assertThat(sizes.get(0)).isEqualTo(948L);
    }

    @Test
    public void toDtoTest() {

        //given
        Message message = new Message();
        LocalDateTime date = LocalDateTime.of(2022, 05, 10, 15, 48, 5);
        Integer intDate = 1652172485;

        message.setMessageId(132);
        User user = new User();
        user.setId(951L);
        message.setFrom(user);
        message.setDate(intDate);
        message.setText("wegfudsvjkl");
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileSize(12);
        List<PhotoSize> photoSizes = new LinkedList<>();
        photoSizes.add(photoSize);
        photoSize = new PhotoSize();
        photoSize.setFileSize(2);
        photoSizes.add(photoSize);
        photoSize = new PhotoSize();
        photoSize.setFileSize(5);
        photoSizes.add(photoSize);
        message.setPhoto(photoSizes);
        photoSize = new PhotoSize();
        photoSize.setFileSize(8);
        photoSizes.add(photoSize);
        Chat chat = new Chat();
        chat.setId(12L);
        message.setChat(chat);

        //when
        MessageDTO messageDTO = mapper.toDto(message);

        //then
        assertThat(messageDTO).isNotNull();
        assertThat(messageDTO.getIdMessage()).isEqualTo(132);
        assertThat(messageDTO.getDate()).isEqualTo(date);
        assertThat(messageDTO.getText()).isEqualTo("wegfudsvjkl");
        assertThat(messageDTO.getIdUser()).isEqualTo(951L);
        assertThat(messageDTO.getChatId()).isEqualTo(12L);
        assertThat(messageDTO.getPhotoSize()).isEqualTo(message.getPhoto().get(3).getFileSize());
    }

}