package com.kotlin.migration.aop

import mu.KotlinLogging
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
@Aspect
open class LoggingAspect {


    @Pointcut("execution(* com.kotlin.migration..*.*(..))")
    private fun cut() {
    }

    @Before("cut()")
    fun logging(joinPoint: JoinPoint) {
        val methodSignature: MethodSignature = joinPoint.signature as MethodSignature
        log.info("[LoggingAspect] {}", methodSignature.toShortString())
    }

}