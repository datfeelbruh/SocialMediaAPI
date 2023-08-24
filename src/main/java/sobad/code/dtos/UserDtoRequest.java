package sobad.code.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoRequest {
    private String username;
    private String email;
    @Size(min = 6, max = 255, message = "Пароль должен быть не короче 6 символов!")
    @NotBlank(message = "Пароль не может быть пустым.")
    private String password;
}
