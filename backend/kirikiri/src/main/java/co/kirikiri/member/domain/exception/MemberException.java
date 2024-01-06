package co.kirikiri.member.domain.exception;

import co.kirikiri.common.exception.domain.DomainException;

public class MemberException extends DomainException {

    public MemberException(final String message) {
        super(message);
    }
}
