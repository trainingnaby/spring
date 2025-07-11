package basics.beans.annotationconfig.lazy;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class LazyClasse {
	
	public LazyClasse() {
		System.out.println("creation du bean lazy ...");
	}
	
	public void methodeLazy() {
		System.out.println("Appel d'une méthode du bean lazy ...");
	}

}
