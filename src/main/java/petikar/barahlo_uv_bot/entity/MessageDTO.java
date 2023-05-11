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
    private String mediaGroupId;
    private Boolean isWarning;

    //TODO указать что это ссылка на другую таблицу
    private Long idUser;
    private String text;
    private Integer photoSize;
    private LocalDateTime date;
    private boolean isBot;
    private Long chatId;
    private String label;
    private LocalDateTime editDate;
    private String normalizeText;
    private String reason;
    //TODO add videofile, document

}
