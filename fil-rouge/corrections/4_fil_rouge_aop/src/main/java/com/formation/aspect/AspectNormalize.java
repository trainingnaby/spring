package com.formation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectNormalize {

	
	@Around("execution(* com.formation.service.DuplicataService.createDuplicata(..))")
	public Object normalizeUserId(ProceedingJoinPoint joinPoint) throws Throwable {
		
		// Récupération des arguments de la méthode interceptée
		Object[] args = joinPoint.getArgs();
		if(args.length > 0 && args[0] instanceof String) {
			String userId = (String) args[0];
			// Modification de l'argument userId pour ajouter le préfixe "FR_" si nécessaire
			if(!userId.startsWith("FR_")) {
				args[0] = "FR_" + userId;
			}
		}
		
		return joinPoint.proceed(args); // Appel de la méthode interceptée avec les arguments modifiés
		
	}
}
