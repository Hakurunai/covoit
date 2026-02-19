package fr.greta.cda.s4_covoitmobile.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
	
	/**
	 * All methods from services packages are spied on
	 */
	@Pointcut("execution(* fr.greta.cda.s4_covoitmobile.services.*.*(..))")
	public void serviceMethods() {}
	
	@Around("serviceMethods()")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		//this data is provided through our AuthTokenFilter
		String userId = MDC.get("userId");
		String userTag = (userId != null) ? "[User:" + userId + "]" : "[Anonymous]";
		
		String className = joinPoint.getSignature().getDeclaringTypeName();
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();
		
		log.info("{} call methods : {}.{}() with args : {}",
			userTag, className, methodName, Arrays.toString(args));
		
		long start = System.currentTimeMillis();
		
		try
		{
			//here we are executing the real method
			Object proceed = joinPoint.proceed();
			
			long executionTime = System.currentTimeMillis() - start;
			log.info("{} end method call : {}.{}(), execution time = {}ms",
				userTag, className, methodName, executionTime);
			
			return proceed;
		}
		catch (Throwable e)
		{
			log.error("{} error in {}.{}() : {}",
				userTag, className, methodName, e.getMessage());
			throw e; //throw again the error, she won't be treated here
		}
	}
}