package fr.formation.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectFinTraitement {

    @After("execution(* fr.formation.aop.service.ServiceCompte.*(..))")
    // cible : tous les méthodes de la classe ServiceCompte peu importe les paramètres
    // s'exécute après l'exécution de la méthode ciblée, que celle-ci se termine normalement ou par une exception
    public void apresExecution(JoinPoint joinPoint) {
        System.out.println("[AOP @After] Après exécution de : "
                + joinPoint.getSignature().getName()
                + " peu importe succès ou exception");
    }
}
