package sobad.code.exceptions;

public class SelfRequestException extends RuntimeException {
    public SelfRequestException(String message) {
        super(message);
    }

    public SelfRequestException() {
    }
}
