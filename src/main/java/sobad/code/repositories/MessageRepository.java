package sobad.code.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sobad.code.entities.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
