package com.github.sulir.dynamidoc.tracing;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.SourceLocation;

import com.github.sulir.dynamidoc.tracing.Trace;
import com.github.sulir.dynamidoc.tracing.TraceEvent;

@Aspect
public class MethodTracer {
	private Trace trace = new Trace();
	
	@Pointcut("execution(* *(..)) && !within(com.github.sulir.dynamidoc..*) && !cflow(adviceexecution())")
	public void method() {}
	
	@Around("method()")
	@SuppressAjWarnings("adviceDidNotMatch")
	public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature();
		SourceLocation location = joinPoint.getSourceLocation();
		Object[] arguments = joinPoint.getArgs();
		TraceEvent event = new TraceEvent(location, signature, arguments);
		
		try {
			Object returnValue = joinPoint.proceed();
			event.setReturnValue(returnValue);
			return returnValue;
		} catch (Throwable exception) {
			event.setException(exception);
			throw exception;
		} finally {
			trace.record(event);
		}
	}
}
