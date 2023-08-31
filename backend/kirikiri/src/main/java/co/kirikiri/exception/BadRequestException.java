package co.kirikiri.exception;

public class BadRequestException extends BusinessException {

    public BadRequestException(final String message) {
        super(message);
    }
}
