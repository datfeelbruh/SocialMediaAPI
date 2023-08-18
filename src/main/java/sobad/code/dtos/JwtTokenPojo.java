package sobad.code.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtTokenPojo {
    private UUID tokenId;
    private String username;
    private Long userId;
    private List<String> authorities;
    private Instant createdAt;
    private Instant expiredAt;
}
