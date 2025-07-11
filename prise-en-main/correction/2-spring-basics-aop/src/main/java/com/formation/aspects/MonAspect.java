package com.formation.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class MonAspect {

	@Around("execution(* com.formation.*.*.envoyer(..))") // pointcut pour selectionner les méthodes qui seront
															// interceptées par l'aspect
	public void loguerTempsExecMethodeEnvoyer(ProceedingJoinPoint joinPoint) throws Throwable { // ProceedingJoinPoint
																								// permet d'executer la
																								// méthode réelle

		System.out.println(("///////// je suis dans l'aspect ////////////"));
		StopWatch stopWatch = new StopWatch();
		// démarrer comptage temps execution
		stopWatch.start();

		// possibilité modifier les paramètres de la méthode
		// joinPoint.getArgs()[0] = "Message modifié par l'aspect";

		// executer la méthode réelle
		joinPoint.proceed();

		// stop comptage temps d'excution
		stopWatch.stop();

		System.out.println("La durée d'execution de la méthode " + joinPoint.getSignature() + " est "
				+ stopWatch.getTotalTimeMillis());
		System.out.println(("///////// fin aspect ////////////"));
	}

	@Before("execution(* com.formation.*.*.set*(..))")
	public void exempleAspectBefore(JoinPoint joinPoint) {
		System.out.println(
				"Aspect - signature de la méthode appelée : " + joinPoint.getTarget() + "#" + joinPoint.getSignature());
	}

}
