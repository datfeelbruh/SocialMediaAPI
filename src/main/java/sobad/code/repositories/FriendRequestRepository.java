package sobad.code.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sobad.code.entities.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
}
