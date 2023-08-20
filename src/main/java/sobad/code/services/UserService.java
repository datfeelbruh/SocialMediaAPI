package sobad.code.services;

import sobad.code.dtos.UserDtoRequest;
import sobad.code.dtos.UserDtoResponse;
import sobad.code.entities.User;

public interface UserService {
    UserDtoResponse createUser(UserDtoRequest userDtoRequest);
    UserDtoResponse getUser(Long id);
}
