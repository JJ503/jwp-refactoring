package kitchenpos.application.exception;

public class NotFoundProductException extends IllegalArgumentException {

    public NotFoundProductException(final String message) {
        super(message);
    }
}
