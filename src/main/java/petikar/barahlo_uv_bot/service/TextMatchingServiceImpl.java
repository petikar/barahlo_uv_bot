package petikar.barahlo_uv_bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import petikar.barahlo_uv_bot.entity.MessageDTO;
import petikar.barahlo_uv_bot.entity.UserDTO;
import petikar.barahlo_uv_bot.entity.UserMapper;

import java.util.*;

import static me.xdrop.fuzzywuzzy.FuzzySearch.tokenSetRatio;

@Service
public class TextMatchingServiceImpl implements TextMatchingService {

    private final MessageService service;
    private final UserMapper userMapper;

    public TextMatchingServiceImpl(MessageService service, UserMapper userMapper) {
        this.service = service;
        this.userMapper = userMapper;
    }

    @Override
    public List<MessageDTO> findAndSendReallySimilarMessage(Update update) {

        List<MessageDTO> result = new ArrayList<>();

        List<MessageDTO> messageDTOs = service.findAll();

        Message message = update.getMessage();

        String text = "";

        if (update.getMessage().hasText()) {
            text = update.getMessage().getText();
        }
        if (update.getMessage().getCaption() != null) {
            text = text + update.getMessage().getCaption(); //TODO вроде может быть или только текст, или только описание
        }

        UserDTO userDTO = userMapper.toDto(update.getMessage().getFrom());

//действия ниже распараллелить
        Optional<List<PhotoSize>> optional = Optional.ofNullable(update.getMessage().getPhoto());
        if (optional.isPresent()) {
            int size = message.getPhoto().size() - 1;
            Integer photoSize = update.getMessage().getPhoto().get(size).getFileSize();
            result.addAll(findAndSendReallySimilarMessageByPhotoSize(messageDTOs, photoSize));
        }

        if (text != null) {
            result.addAll(findAndSendReallySimilarMessageByText(messageDTOs, userDTO, text));
        }

        return result;
    }

    private Set<MessageDTO> findAndSendReallySimilarMessageByPhotoSize(List<MessageDTO> messageDTOs, int photoSize) {

        Set<MessageDTO> resultSet = new HashSet<>();

        for (MessageDTO messageDTO : messageDTOs) {
            if (messageDTO.getPhotoSize() != null) {
                if (messageDTO.getPhotoSize() == photoSize) {
                    messageDTO.setText(
                            "\uD83C\uDF06\uD83C\uDF06\uD83C\uDF06\uD83C\uDF06\uD83C\uDF06\n Соответствие по размеру фото: \n" +
                                    "\uD83C\uDF06 IdMessage = " + messageDTO.getIdMessage() + "\n" +
                                    "\uD83C\uDF06 MediaGroupId = " + messageDTO.getMediaGroupId() + "\n" +
                                    "\uD83C\uDF06 Размер фото = " + photoSize + "\n" +
                                    "\uD83C\uDF06 Ранее размещал пользователь с id = " + messageDTO.getIdUser() + "\n" +
                                    "\uD83C\uDF06 Прежняя дата размещения = " + "\n" + messageDTO.getDate()
                    );
                    //TODO из другой таблицы достать имя пользователя

                    resultSet.add(messageDTO);
                }
            }
        }
        return resultSet;
    }

    private Set<MessageDTO> findAndSendReallySimilarMessageByText(List<MessageDTO> messageDTOs, UserDTO userDTO, String text) {

        Set<MessageDTO> resultSet = new HashSet<>();

        for (MessageDTO messageDTO : messageDTOs) {
            if (!messageDTO.getText().equals("without text")) {
                // int result = ratio(text, messageDTO.getText());
                // int result = tokenSortPartialRatio(text, messageDTO.getText());
                int result = tokenSetRatio(text, messageDTO.getText());
                //int result = weightedRatio(text, messageDTO.getText());

                String newText = "";

                if ((result > 65) && (userDTO.getId().equals(messageDTO.getIdUser()))) {

                    String fullname = NameUtils.getFullNameFromDTO(userDTO);

                    newText = "\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\uD83E\uDDDA\u200D♂\nРанее сообщение было размещено тем же пользователем, с id = "
                            + messageDTO.getIdUser() + " " + fullname + "\n";
                    messageDTO.setText(newText +
                            "\uD83E\uDDDA\u200D♂ result = " + result + "%\n" +
                            "\uD83E\uDDDA\u200D♂ IdMessage = " + messageDTO.getIdMessage() + "\n" +
                            "\uD83E\uDDDA\u200D♂ Новый размещённый текст = " + text + "\n" +
                            "\uD83E\uDDDA\u200D♂ Текст, размещённый ранее = " + messageDTO.getText() + "\n" +
                            "\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\uD83E\uDDDA\u200D♂\uD83E\uDD96\n Метка предупреждения, поставленная ранее = " + messageDTO.getIsWarning() + "\n" +
                            "\uD83E\uDDDA\u200D♂ Прежняя дата размещения = " + messageDTO.getDate()
                    );
                    resultSet.add(messageDTO);
                } else if (result > 88) {
                    newText = "Совпадение с сообщением ДРУГОГО ПОЛЬЗОВАТЕЛЯ\n" +
                            "\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\uD83D\uDC39\n Ранее размещал пользователь с id = " + messageDTO.getIdUser() + "\n";
                    messageDTO.setText(newText +
                            "\uD83D\uDC39 result = " + result + "%\n" +
                            "\uD83D\uDC39 IdMessage = " + messageDTO.getIdMessage() + "\n" +
                            "\uD83D\uDC39 Новый размещённый текст = " + text + "\n" +
                            "\uD83D\uDC39 Текст, размещённый ранее = " + messageDTO.getText() + "\n" +
                            "\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\uD83D\uDC39\uD83E\uDD96\n Метка предупреждения, поставленная ранее = " + messageDTO.getIsWarning() + "\n" +
                            "\uD83D\uDC39 Прежняя дата размещения = " + messageDTO.getDate()
                    );
                    resultSet.add(messageDTO);
                }
            }
        }
        return resultSet;
    }
}
