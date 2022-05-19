package petikar.barahlo_uv_bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import petikar.barahlo_uv_bot.entity.MessageDTO;

import java.time.LocalDateTime;

@Repository
public interface MessageRepository extends CrudRepository<MessageDTO, Long> {

    void deleteMessageDTOByDateBefore(LocalDateTime date);
}
