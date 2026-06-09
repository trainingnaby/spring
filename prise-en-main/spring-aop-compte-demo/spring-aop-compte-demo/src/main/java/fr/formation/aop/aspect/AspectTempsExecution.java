package fr.formation.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectTempsExecution {

    @Around("execution(* fr.formation.aop.service.ServiceCompte.*(..))")
    public Object mesurerTempsExecution(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long debut = System.nanoTime();

        System.out.println("[AOP @Around] Début autour de : " + proceedingJoinPoint.getSignature().getName());

        try {
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
