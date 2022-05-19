package petikar.barahlo_uv_bot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class MessageDTO {
    @Id
    private Integer idMessage;
    private Long chatId;
    private Long idUser;
    private String text;
    private Integer photoSize;
    private LocalDateTime date;

    //TODO add firstName, isBot, lastName, userName or use UserDto

    //TODO add videofile, document

}
