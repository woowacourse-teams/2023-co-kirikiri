package co.kirikiri.domain.member.exception;

import co.kirikiri.domain.exception.DomainException;

public class MemberException extends DomainException {

    public MemberException(final String message) {
        super(message);
    }
}
