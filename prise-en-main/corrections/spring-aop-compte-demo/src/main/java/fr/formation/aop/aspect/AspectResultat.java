package fr.formation.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectResultat {

    @AfterReturning(
            pointcut = "execution(* fr.formation.aop.service.ServiceCompte.consulter*(..))",
            returning = "resultat"
    )
    public void apresRetourNormal(JoinPoint joinPoint, Object resultat) {
        System.out.println("[AOP @AfterReturning] La méthode "
                + joinPoint.getSignature().getName()
                + " a retourné : " + resultat);
    }
}
