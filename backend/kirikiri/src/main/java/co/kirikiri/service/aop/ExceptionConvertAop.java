package co.kirikiri.service.aop;

import co.kirikiri.domain.exception.DomainException;
import co.kirikiri.domain.exception.UnexpectedDomainException;
import co.kirikiri.service.exception.BadRequestException;
import co.kirikiri.service.exception.ServerException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionConvertAop {

    @AfterThrowing(pointcut = "within(@co.kirikiri.service.aop.ExceptionConvert *)", throwing = "exception")
    public void convertException(final Throwable exception) {
        if (exception instanceof UnexpectedDomainException) {
            throw new ServerException(exception.getMessage());
        }
        if (exception instanceof DomainException) {
            throw new BadRequestException(exception.getMessage());
        }
    }
}
