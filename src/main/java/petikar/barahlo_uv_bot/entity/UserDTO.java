package petikar.barahlo_uv_bot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class UserDTO {

    @Id
    private Long id;

    private String userName;
    private String firstName;
    private String lastName;

    private boolean isBot;
    private boolean isCommercial;


}
