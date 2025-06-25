
package com.example.bibliotheque.aop;

import com.example.bibliotheque.entity.Livre;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Aspect
@Component
public class LivreBusinessAspect {

    @Before("execution(* com.example.bibliotheque.service.LivreService.archiverLivre(..)) && args(id)")
    public void checkBookAgeBeforeArchiving(JoinPoint joinPoint, Long id) {
        Object target = joinPoint.getTarget();
        if (target instanceof com.example.bibliotheque.service.LivreService service) {
            Livre livre = service.getLivreById(id);
            if (livre.getDatePublication() != null &&
                livre.getDatePublication().isAfter(LocalDate.now().minusMonths(6))) {
                throw new RuntimeException("Impossible d'archiver un livre publié il y a moins de 6 mois.");
            }
        }
    }
}
