package sobad.code.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDtoResponse {
    private String from;
    private String to;
    private String message;
    private String timestamp;
}
