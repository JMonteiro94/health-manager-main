package com.myhealth.healthmanagermain.aop.timer;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class MeasureTimeAdvice {

  @Around("@annotation(com.myhealth.healthmanagermain.aop.timer.MeasureTime)")
  public Object measureTime(ProceedingJoinPoint point) throws Throwable {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Object object = point.proceed();
    stopWatch.stop();
    log.info(String.format("Time taken by class <%s> and method <%s> is %s ms.",
        point.getSignature().getDeclaringTypeName(),
        point.getSignature().getName(),
        stopWatch.getTotalTimeMillis()));
    return object;
  }
}
