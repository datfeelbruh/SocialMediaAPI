package sobad.code.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sobad.code.entities.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoResponse {
    private String username;
    private String email;
    private List<User> friends;
    private List<User> friendRequest;
    private List<User> subscriptions;
}
