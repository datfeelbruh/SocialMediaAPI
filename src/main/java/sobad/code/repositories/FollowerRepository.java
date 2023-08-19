package sobad.code.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sobad.code.entities.Follower;

public interface FollowerRepository extends JpaRepository<Follower, Long> {
}
