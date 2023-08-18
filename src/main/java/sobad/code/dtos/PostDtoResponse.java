package sobad.code.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDtoResponse {
    private String title;
    private String description;
    private String imageSrc;
    private Instant createdAt;
}
