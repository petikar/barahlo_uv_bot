package petikar.barahlo_uv_bot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import petikar.barahlo_uv_bot.entity.UserDTO;

@Component
public class NameUtils {
    public static String getFullName(User user) {

        String fullname = "";

        if (user.getUserName() != null) {
            fullname = "Ник = " + user.getUserName();
        }
        if (!fullname.equals("")) {
            fullname = fullname + ", имя = " + user.getFirstName();
        } else {
            fullname = user.getFirstName();
        }
        if (user.getLastName() != null) {
            fullname = fullname + ", фамилия = " + user.getLastName();
        }
        return fullname;
    }

    public static String getFullNameFromDTO(UserDTO user) {

        String fullname = "";

        if (user.getUserName() != null) {
            fullname = "Ник = " + user.getUserName();
        }
        if (!fullname.equals("")) {
            fullname = fullname + ", имя = " + user.getFirstName();
        } else {
            fullname = user.getFirstName();
        }
        if (user.getLastName() != null) {
            fullname = fullname + ", фамилия = " + user.getLastName();
        }

        return fullname;
    }
}
