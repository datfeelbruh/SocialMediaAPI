package sobad.code.dtos;

import lombok.Data;

@Data
public class UserDtoRequest {
    private String username;
    private String email;
    private String password;
}
