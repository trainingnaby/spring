package com.formation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AspectNormalize {

	// intercepte l'appel à la méthode createDuplicata du service DuplicataService
	// et
	// et rajoute FR_ devant le userId si il n'est pas déjà présent
	// ex: createDuplicata("1234", 2000) => createDuplicata("FR_1234", 2000)
	@Around("execution(* com.formation.service.DuplicataService.createDuplicata(..))")
	public Object normalizeUserId(ProceedingJoinPoint joinPoint) throws Throwable {
		
		// récupère les arguments de la méthode interceptée
		Object[] args = joinPoint.getArgs();
		
		if (args.length > 0 && args[0] instanceof String) {
			// récupère le userId (premier argument de la méthode)
			String userId = (String) args[0];
			if (!userId.startsWith("FR_")) {
				// rajoute FR_ devant le userId
				args[0] = "FR_" + userId;
			}
		}
		// continue l'exécution de la méthode interceptée avec les arguments modifiés
		return joinPoint.proceed(args);
	}

}
