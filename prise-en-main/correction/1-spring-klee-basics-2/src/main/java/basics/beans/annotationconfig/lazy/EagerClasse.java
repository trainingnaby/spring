package basics.beans.annotationconfig.lazy;

import org.springframework.stereotype.Component;

@Component
public class EagerClasse {

	public EagerClasse() {
		System.out.println("Creation du bean EagerClasse aprés le chargement du contexte ...");

	}

	public void methodeEager() {
		System.out.println("Appel d'une méthode du bean eager ...");
	}
}
