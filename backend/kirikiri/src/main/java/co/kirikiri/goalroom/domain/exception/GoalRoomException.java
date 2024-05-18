package co.kirikiri.goalroom.domain.exception;

import co.kirikiri.common.exception.domain.DomainException;

public class GoalRoomException extends DomainException {

    public GoalRoomException(final String message) {
        super(message);
    }
}
