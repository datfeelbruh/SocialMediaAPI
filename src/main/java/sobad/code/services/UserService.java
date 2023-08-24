package sobad.code.services;

import sobad.code.dtos.UserDtoRequest;
import sobad.code.dtos.UserDtoResponse;

public interface UserService {
    UserDtoResponse createUser(UserDtoRequest userDtoRequest);
}
