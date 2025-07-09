package com.formation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.formation.domain.Duplicata;

@Component
@Aspect
public class MonAspect {
	
	// Intercepte l'appel de la méthode createDuplicata dans le bean duplicataService
	@Around("bean(duplicataService) && execution(* createDuplicata(..))") 
	public Duplicata add_fr_to_users(ProceedingJoinPoint joinPoint) throws Throwable {
		
		// Recuperation de l'argument userId (premier argument de la méthode)
		String userIdOriginal = (String) joinPoint.getArgs()[0];
		
		// construction des nouveaux arguments pour l'appel de la méthode avec l'argument userId modifié
		Object[] newArgs = {"FR_" + userIdOriginal, (Integer) joinPoint.getArgs()[1]};
		
		// Appel de la méthode originale avec l'argument userId modifié
		Duplicata duplicata = (Duplicata) joinPoint.proceed(newArgs);
		
		return duplicata;
		
	}

}
