package petikar.barahlo_uv_bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import petikar.barahlo_uv_bot.entity.*;
import petikar.barahlo_uv_bot.repository.MessageRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class WarningServiceImpl implements WarningService {

    MessageRepository messageRepository;

    MessageMapper messageMapper;
    UserMapper userMapper;

    ForwardMessageService forwardMessageService;
    SendMessageService sendMessageService;

    public WarningServiceImpl(MessageRepository messageRepository,
                              MessageMapper messageMapper,
                              UserMapper userMapper,
                              ForwardMessageService forwardMessageService,
                              SendMessageService sendMessageService) {
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.forwardMessageService = forwardMessageService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public boolean isWarning(Integer id) {
        return messageRepository.getIsWarningById(id);
    }

    @Override
    @Transactional
    public SendMessage setWarning(Message message, User user) {

        //TODO ставить метку по всем сообщениям с одинаковой группой

        //Сюда передавать сообщение, на которое ответ         message.getReplyToMessage();

        UserDTO userDTO = userMapper.toDto(user);
        MessageDTO messageDTO = messageMapper.toDto(message);

        String label = messageRepository.getById(messageDTO.getIdMessage()).getLabel();

        Set<MessageDTO> warningSet = new HashSet<>();

        warningSet.add(messageDTO);

        messageDTO.setIsWarning(true);

        if (messageDTO.getMediaGroupId()!=null) {
            warningSet.addAll(messageRepository.findAllByMediaGroupId(messageDTO.getMediaGroupId()));
        }

        for (MessageDTO mDTO: warningSet) {
            mDTO.setIsWarning(Boolean.TRUE);
            mDTO.setLabel(label);
            messageRepository.save(mDTO);
        }

        SendMessage sendMessage = sendMessageSetWarning(messageDTO, userDTO);

        return sendMessage;
    }

    private SendMessage sendMessageSetWarning(MessageDTO messageDTO, UserDTO userDTO) {

        System.out.println("sendMessageSetWarning(UserDTO userDTO) Класс WarningServiceImpl");

        String userName = NameUtils.getFullNameFromDTO(userDTO);

        //messageDTO.setText("Пользователю " + userName + " с id = " + userDTO.getId() + " установлено предупреждение");

        SendMessage sendMessage = sendMessageService.createSendMessage("\n \uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80 \nПользователю " + userName + " с id = " + userDTO.getId() + " установлено предупреждение \n \uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80\uD83E\uDD80 \n на сообщение " + messageDTO);
        //    forwardMessageService.sendMessage(messageDTO);
        System.out.println(messageDTO.getText());
        return sendMessage;
    }
}
