package sobad.code.services;

import sobad.code.dtos.TokenDtoResponse;
import sobad.code.dtos.UserDtoRequest;

public interface AuthService {
    TokenDtoResponse authenticateUser(UserDtoRequest userDtoRequest);
}
