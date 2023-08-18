package sobad.code.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sobad.code.jwt.Token;

public interface TokenRepository extends JpaRepository<Token, String> {
}
