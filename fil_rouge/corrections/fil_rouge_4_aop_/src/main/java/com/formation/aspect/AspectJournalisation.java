package com.formation.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.formation.domain.Duplicata;



@Component
@Aspect
public class AspectJournalisation {
	
	
	@Before("bean(duplicataService) && execution(* createDuplicata(..))")
	// avant l'exécution de la méthode createDuplicata du bean duplicataService
	public void loguerEntreesMethodes(JoinPoint joinPoint) {
		System.out.println("////////////// Méthode appelée avec les paramètres //////////");
		System.out.println(joinPoint.getArgs());
		System.out.println(joinPoint.getSignature());
	}
	
	
	@Around("bean(duplicataService) && execution(* createDuplicata(..))")
	public Duplicata ajouterPrefixeFr(ProceedingJoinPoint joinPoint) throws Throwable {
		
		//extraction des paramètres de requête
		Object[] arguments = joinPoint.getArgs();
		String userIdOriginal = (String) arguments[0]; //userId en position 1
		int montantOriginal = (int) arguments[1];
		
		//construire un nouveau tableau d'arguments
		Object[] nouveaux_arguments = new Object[] {
				"FR_"+userIdOriginal, Integer.valueOf(montantOriginal)
		};
		
		//appel de la méthode réelle
		Duplicata createdDuplicata = (Duplicata) joinPoint.proceed(nouveaux_arguments);
		
		return createdDuplicata;
		
	}

}
