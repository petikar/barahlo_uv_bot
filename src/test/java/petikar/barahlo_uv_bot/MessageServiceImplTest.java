package petikar.barahlo_uv_bot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import petikar.barahlo_uv_bot.entity.MessageDTO;
import petikar.barahlo_uv_bot.repository.MessageRepository;
import petikar.barahlo_uv_bot.service.MessageServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    MessageRepository repository;

    @InjectMocks
    MessageServiceImpl service;


    @Test
    public void findRepeatMessagesTest() {

        //given
        MessageDTO messageDTO = new MessageDTO();

        List<MessageDTO> mockList = new ArrayList<>();
        LocalDateTime dateGiven = LocalDateTime.of(2022, 05, 13, 15, 48, 5);
        int dateIntGiven = 1652431685;

        messageDTO.setIdMessage(1);
        messageDTO.setText("uwiehjfsdnlsk");
        Date date = new Date();
        messageDTO.setDate(dateGiven);
        messageDTO.setIdUser(154L);
        messageDTO.setPhotoSize(30);
        mockList.add(messageDTO);

        messageDTO = new MessageDTO();
        messageDTO.setIdMessage(2);
        messageDTO.setText("fgjfgjygkh");
        messageDTO.setDate(dateGiven);
        messageDTO.setIdUser(154L);
        messageDTO.setPhotoSize(30);
        mockList.add(messageDTO);

        messageDTO = new MessageDTO();
        messageDTO.setIdMessage(3);
        messageDTO.setText("uwiehjfsghkdnlsk");
        messageDTO.setDate(dateGiven);
        messageDTO.setIdUser(154L);
        messageDTO.setPhotoSize(30);
        mockList.add(messageDTO);

        messageDTO = new MessageDTO();
        messageDTO.setIdMessage(4);
        messageDTO.setText("gjhk");
        messageDTO.setDate(dateGiven);
        messageDTO.setIdUser(15L);
        messageDTO.setPhotoSize(15);
        mockList.add(messageDTO);

        messageDTO = new MessageDTO();
        messageDTO.setIdMessage(5);
        messageDTO.setText("hjjl");
        messageDTO.setDate(dateGiven);
        messageDTO.setIdUser(14L);
        messageDTO.setPhotoSize(150);
        mockList.add(messageDTO);

        messageDTO = new MessageDTO();
        messageDTO.setIdMessage(6);
        messageDTO.setText("hjjl");
        messageDTO.setDate(dateGiven);
        messageDTO.setIdUser(154L);
        messageDTO.setPhotoSize(95);
        mockList.add(messageDTO);

        messageDTO = new MessageDTO();
        messageDTO.setIdMessage(7);
        messageDTO.setText("hjjl");
        messageDTO.setDate(dateGiven);
        messageDTO.setIdUser(154L);
        messageDTO.setPhotoSize(95);
        mockList.add(messageDTO);

        messageDTO = new MessageDTO();
        messageDTO.setIdMessage(8);
        messageDTO.setText("sdfgkjhgsdfdghdgsf");
        messageDTO.setDate(dateGiven);
        messageDTO.setIdUser(154L);
        messageDTO.setPhotoSize(69);
        mockList.add(messageDTO);

        //when
        when(repository.findAll()).thenReturn(mockList);
        Set<MessageDTO> repeatMessages = service.findRepeatMessages();
        System.out.println("mock "+mockList);
        System.out.println();
        System.out.println("repeat "+repeatMessages);

        //then
        assertThat(repeatMessages).isNotNull();
        assertThat(repeatMessages.size()).isEqualTo(5);
        assertThat(repeatMessages.contains(mockList.get(0))).isTrue();
        assertThat(repeatMessages.contains(mockList.get(1))).isTrue();
        assertThat(repeatMessages.contains(mockList.get(2))).isTrue();
        assertThat(repeatMessages.contains(mockList.get(5))).isTrue();
        assertThat(repeatMessages.contains(mockList.get(6))).isTrue();
        assertThat(repeatMessages.contains(mockList.get(3))).isFalse();
        assertThat(repeatMessages.contains(mockList.get(4))).isFalse();
        assertThat(repeatMessages.contains(mockList.get(7))).isFalse();
        //1 2 3 6 7
    }

}
