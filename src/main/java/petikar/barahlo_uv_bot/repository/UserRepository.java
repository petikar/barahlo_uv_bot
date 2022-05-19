package petikar.barahlo_uv_bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import petikar.barahlo_uv_bot.entity.UserDTO;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserDTO, Long> {

    List<UserDTO> findByName(String name);

}
