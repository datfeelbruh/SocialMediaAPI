package sobad.code.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sobad.code.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
