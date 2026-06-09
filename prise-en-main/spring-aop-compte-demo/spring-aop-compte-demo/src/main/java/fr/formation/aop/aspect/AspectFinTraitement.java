package fr.formation.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectFinTraitement {

    @After("execution(* fr.formation.aop.service.ServiceCompte.*(..))")
    public void apresExecution(JoinPoint joinPoint) {
        System.out.println("[AOP @After] Après exécution de : "
                + joinPoint.getSignature().getName()
                + " peu importe succès ou exception");
    }
}
