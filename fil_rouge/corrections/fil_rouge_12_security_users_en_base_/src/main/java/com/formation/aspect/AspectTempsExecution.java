package com.formation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AspectTempsExecution {
	
	
	@Around("@annotation(com.formation.aspect.MonitorerTempsExecution)")
	public Object monitorerTempsExec(ProceedingJoinPoint joinPoint) throws Throwable {
		 long debutAppel = System.currentTimeMillis();
		 
		 // appel de la méthode réelle
		 Object object = joinPoint.proceed();
		 
		 long dureeAppel = System.currentTimeMillis() - debutAppel;
		 
		 System.out.println("Durée d'exécution de la méthode : "+dureeAppel);
		 
		 return object;
	}

}
