package basics.beans.annotationconfig.formation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service("formation_spring")
public class FormationSpring implements FormationMetadata {

	@Value("${formation.module:Java}")
	private String module;

	@Value("${formation.salle:salleParDefaut}")
	private String salle;

	@Autowired
	private Environment environment;

	@Override
	public void lireMetadataFormation() {
		System.out.println("Lieu formation :" + environment.getProperty("formation.lieu"));

		System.out.println("Module formation : " + module);
		System.out.println("Salle formation : " + salle);

	}

}
