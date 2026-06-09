package fr.formation.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AspectErreur {

    @AfterThrowing(
            pointcut = "execution(* fr.formation.aop.service.ServiceCompte.*(..))",
            throwing = "exception"
    )
    public void apresException(JoinPoint joinPoint, Exception exception) {
        System.out.println("[AOP @AfterThrowing] Exception dans "
                + joinPoint.getSignature().getName()
                + " : " + exception.getMessage());
    }
}
