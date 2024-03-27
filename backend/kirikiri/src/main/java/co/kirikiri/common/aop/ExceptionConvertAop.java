package co.kirikiri.common.aop;

import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.ServerException;
import co.kirikiri.common.exception.domain.DomainException;
import co.kirikiri.common.exception.domain.UnexpectedDomainException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionConvertAop {

    @AfterThrowing(pointcut = "within(@co.kirikiri.common.aop.ExceptionConvert *)", throwing = "exception")
    public void convertException(final Throwable exception) {
        if (exception instanceof UnexpectedDomainException) {
            throw new ServerException(exception.getMessage());
        }
        if (exception instanceof DomainException) {
            throw new BadRequestException(exception.getMessage());
        }
    }
}
