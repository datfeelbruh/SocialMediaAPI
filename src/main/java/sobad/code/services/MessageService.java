package sobad.code.services;

import sobad.code.dtos.MessageDtoResponse;

public interface MessageService {
    MessageDtoResponse sendMessage(Long from, Long to, String message);
}
