package petikar.barahlo_uv_bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import petikar.barahlo_uv_bot.entity.UserDTO;

@Repository
public interface UserRepository extends CrudRepository<UserDTO, Long> {

    UserDTO getUserDTOById(Long id);

    default boolean getIsCommercialById(Long id){
        return getUserDTOById(id).isCommercial();
    }
}
