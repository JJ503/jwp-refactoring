package kitchenpos.application.exception;

public class NotFoundTableGroupException extends IllegalArgumentException {

    public NotFoundTableGroupException(final String message) {
        super(message);
    }
}