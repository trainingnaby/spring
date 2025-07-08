package basics.beans.annotationconfig.acteur;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class Acteur {

	@Value("${acteur.nom:inconnu}")
	private String nom;
	@Value("${acteur.email:exemple@email.com}")
	private String email;
	@Value("${acteur.genre:M}")
	private Character genre;
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Character getGenre() {
		return genre;
	}
	public void setGenre(Character genre) {
		this.genre = genre;
	}
	
	

}
