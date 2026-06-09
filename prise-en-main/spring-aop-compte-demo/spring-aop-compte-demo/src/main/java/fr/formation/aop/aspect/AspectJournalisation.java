package fr.formation.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectJournalisation {

    @Before("execution(* fr.formation.aop.service.ServiceCompte.*(..))")
    // cible toutes les méthodes de la classe ServiceCompte peu importe leur nom et leurs paramètres
    public void avantAppelMethode(JoinPoint joinPoint) {
        System.out.println("[AOP @Before] Avant appel de la méthode : " + joinPoint.getSignature().getName());
    }
}
