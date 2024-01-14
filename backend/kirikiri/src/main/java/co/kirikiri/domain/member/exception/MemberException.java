package co.kirikiri.domain.member.exception;

import co.kirikiri.common.exception.DomainException;

public class MemberException extends DomainException {

    public MemberException(final String message) {
        super(message);
    }
}
