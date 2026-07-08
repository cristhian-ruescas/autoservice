package com.autoservice.infrastructure.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Aspect
@Component
public class TransactionalUseCaseAspect {

    private final TransactionTemplate transactionTemplate;

    public TransactionalUseCaseAspect(final PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Around("execution(* com.autoservice.application..*UseCase.execute(..))")
    public Object transactionalUseCase(final ProceedingJoinPoint joinPoint) {
        return this.transactionTemplate.execute(status -> {
            try {
                return joinPoint.proceed();
            } catch (final RuntimeException | Error error) {
                throw error;
            } catch (final Throwable throwable) {
                throw new IllegalStateException(throwable);
            }
        });
    }
}
