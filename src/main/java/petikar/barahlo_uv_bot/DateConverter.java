package petikar.barahlo_uv_bot;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class DateConverter {
    public static Integer dateToInteger(LocalDateTime date) {
        //TODO Y2038 problem
        return Math.toIntExact(date.toInstant(ZoneOffset.of("+07:00")).getEpochSecond());
    }


    public static LocalDateTime intToDate(Integer timeStamp) {
        LocalDateTime date = LocalDateTime.ofEpochSecond(timeStamp, 0, ZoneOffset.of("+07:00"));
        return date;
    }

}
