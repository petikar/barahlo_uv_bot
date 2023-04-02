package petikar.barahlo_uv_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import petikar.barahlo_uv_bot.entity.MessageDTO;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageDTO, Integer> {

    void deleteMessageDTOByDateBefore(LocalDateTime date);

    List<MessageDTO> findByIdUser(Long userId);

    List<MessageDTO> findAllByDateAfter(LocalDateTime date);

    List<MessageDTO> findAllByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<MessageDTO> findAllByMediaGroupId(String mediaGroupId);

    default boolean getIsWarningById(Integer id){
        return getMessageDTOByIdMessage(id).getIsWarning();
    }

    MessageDTO getMessageDTOByIdMessage(Integer idMessage);

}
