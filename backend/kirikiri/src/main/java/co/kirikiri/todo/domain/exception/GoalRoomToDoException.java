package co.kirikiri.todo.domain.exception;

import co.kirikiri.common.exception.DomainException;

public class GoalRoomToDoException extends DomainException {
    
    public GoalRoomToDoException(final String message) {
        super(message);
    }
}
