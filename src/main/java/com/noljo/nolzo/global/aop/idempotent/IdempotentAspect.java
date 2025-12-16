package com.noljo.nolzo.global.aop.idempotent;

import com.noljo.nolzo.global.aop.utils.AspectUtils;
import com.noljo.nolzo.global.error.exception.SeatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IdempotentAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(com.noljo.nolzo.global.aop.idempotent.Idempotent)")
    public Object checkIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Idempotent annotation = signature.getMethod().getAnnotation(Idempotent.class);

        String idempotentKey = AspectUtils.parseKey(signature, joinPoint, annotation.key(), annotation.prefix());
        log.info("멱등성 체크 시작: {}", idempotentKey);
        return checkAndSet(joinPoint, annotation, idempotentKey);
    }

    private Object checkAndSet(ProceedingJoinPoint joinPoint, Idempotent annotation, String idempotentKey)
            throws Throwable {
        try {
            log.info("Redis에 키 설정 시도: {}", idempotentKey);
            Boolean isSet = redisTemplate.opsForValue().setIfAbsent(
                    idempotentKey,
                    "1",
                    annotation.expiration(),
                    annotation.timeUnit()
            );

            if (Boolean.FALSE.equals(isSet)) {
                Class<? extends RuntimeException> exClass = annotation.exceptionOnDuplicate();
                log.info("멱등성 위반: {}", idempotentKey);
                try {
                    throw exClass.getConstructor(String.class)
                            .newInstance("중복 요청입니다. key=" + idempotentKey);
                } catch (NoSuchMethodException ignore) {
                    throw exClass.getConstructor().newInstance();
                }
            }

            log.info("멱등성 키 설정 성공: {}", idempotentKey);
            log.info("메서드 실행 시작: {}", joinPoint.getSignature().getName());
            Object result = joinPoint.proceed();
            log.info("메서드 실행 완료: {}", joinPoint.getSignature().getName());
            return result;

        } catch (SeatException e) {
            log.info("비즈니스 예외 발생, 멱등성 키 삭제: {}", idempotentKey);
            redisTemplate.delete(idempotentKey);
            throw e;
        } catch (Exception e) {
            log.error("멱등성 체크 중 오류 발생: {}", e.getMessage(), e);
            if (e instanceof RuntimeException) {
                throw e;
            }
            throw new RuntimeException("멱등성 체크 중 오류가 발생했습니다.", e);
        }
    }
}
