package co.kirikiri.service.exception;

public class BadRequestException extends BusinessException {

    public BadRequestException(final String message) {
        super(message);
    }
}
