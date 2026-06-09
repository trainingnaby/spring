package fr.formation.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectTempsExecution {

    @Around("execution(* fr.formation.aop.service.ServiceCompte.*(..))")
    // cible : tous les méthodes de la classe ServiceCompte
    // permet de mesurer le temps d'exécution de ces méthodes
    public Object mesurerTempsExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long debut = System.nanoTime();

        System.out.println("[AOP @Around] Début autour de : " + proceedingJoinPoint.getSignature().getName());

        try {
        	
        	// appel de la méthode cible (la méthode de ServiceCompte) et récupération du résultat
            Object resultat = proceedingJoinPoint.proceed();
            return resultat;
        } finally {
            long fin = System.nanoTime();
            long duree = fin - debut;

            System.out.println("[AOP @Around] Fin autour de : "
                    + proceedingJoinPoint.getSignature().getName()
                    + " - durée : " + duree + " ns");
        }
    }
}
