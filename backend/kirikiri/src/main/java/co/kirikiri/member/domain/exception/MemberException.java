package co.kirikiri.member.domain.exception;

import co.kirikiri.common.exception.DomainException;

public class MemberException extends DomainException {

    public MemberException(final String message) {
        super(message);
    }
}
