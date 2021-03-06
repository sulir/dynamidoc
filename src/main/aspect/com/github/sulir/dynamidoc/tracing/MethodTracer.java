package com.github.sulir.dynamidoc.tracing;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;

@Aspect
public class MethodTracer {
	private Trace trace = new Trace();
	
	@Pointcut("execution(synthetic * *(..)) || "
			+ "(within(is(EnumType)) && (execution(* values()) || execution(* valueOf(String))))")
	void withoutSource() {}

	@Pointcut("within(com.github.sulir.dynamidoc..*) || cflow(call(* toString()))")
	void infiniteRecursion() {}
	
	@Pointcut("within(*..*Test || *..*TestCase || *..*TestSuite)")
	void test() {}
	
	@Pointcut("execution(* *(..)) && !withoutSource() && !infiniteRecursion() && "
			+ "!test() && !within(is(AnonymousType))")
	void loggedMethod() {}
	
	@Around("loggedMethod()")
	@SuppressAjWarnings("adviceDidNotMatch")
	public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		SourceLocation location = joinPoint.getSourceLocation();
		Object[] arguments = joinPoint.getArgs();
		TraceEvent event = new TraceEvent(location, signature, arguments);

		try {
			event.setStateBefore(joinPoint.getThis());
			Object returnValue = joinPoint.proceed();
			
			if (!signature.getReturnType().equals(Void.TYPE))
				event.setReturnValue(returnValue);
			
			return returnValue;
		} catch (Throwable exception) {
			event.setException(exception);
			throw exception;
		} finally {
			event.setStateAfter(joinPoint.getThis());
			trace.record(event);
		}
	}
}
