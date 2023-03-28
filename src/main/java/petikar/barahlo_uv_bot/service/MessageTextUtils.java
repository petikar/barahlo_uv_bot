package petikar.barahlo_uv_bot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageTextUtils {

    public static String getTextFromMessage(Message message) {

        String text = "";

        if (message.getText() != null) {
            text = message.getText();
        }
        if (message.getCaption() != null) {
            if (!text.equals("")) {
                text = text + " " + message.getCaption();
            } else {
                text = message.getCaption();
            }

        }
        return text;
    }
}
