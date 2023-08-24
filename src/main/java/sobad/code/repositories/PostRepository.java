package sobad.code.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sobad.code.entities.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "select * from posts as p left join followers f on f.follower_id = p.user_id "
            + "where f.user_id = ?1", nativeQuery = true)
    List<Post> findAllFollowUsersPosts(Long id, Pageable pageable);
}
