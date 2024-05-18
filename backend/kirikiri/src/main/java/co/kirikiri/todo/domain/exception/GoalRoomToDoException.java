package co.kirikiri.todo.domain.exception;

import co.kirikiri.common.exception.domain.DomainException;

public class GoalRoomToDoException extends DomainException {

    public GoalRoomToDoException(final String message) {
        super(message);
    }
}
