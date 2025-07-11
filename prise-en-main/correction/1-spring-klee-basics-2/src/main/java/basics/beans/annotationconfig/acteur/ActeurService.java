package basics.beans.annotationconfig.acteur;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActeurService {

	@Value("#{acteur.nom matches '[a-zA-Z\\s]{3,20}'}")
	private boolean nomValide;
	@Value("#{acteur.genre matches '[M|F]'}")
	private boolean genreValide;
	@Value("#{acteur.email matches '^[\\w!#$%&*+/=?`{|}~^-]+(?:\\.[\\w!#$%&*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$'}")
	private boolean emailValide;
	@Value("#{acteur.nom.toUpperCase()}")
	private String nomActeur;
	@Value("#{T(java.time.LocalDateTime).now().toString()}")
	private String exempleDate;

	public boolean isNomValide() {
		return nomValide;
	}

	public void setNomValide(boolean nomValide) {
		this.nomValide = nomValide;
	}

	public boolean isGenreValide() {
		return genreValide;
	}

	public void setGenreValide(boolean genreValide) {
		this.genreValide = genreValide;
	}

	public boolean isEmailValide() {
		return emailValide;
	}

	public void setEmailValide(boolean emailValide) {
		this.emailValide = emailValide;
	}

	public String getNomActeur() {
		return nomActeur;
	}

	public void setNomActeur(String nomActeur) {
		this.nomActeur = nomActeur;
	}

	public String getExempleDate() {
		return exempleDate;
	}

	public void setExempleDate(String exempleDate) {
		this.exempleDate = exempleDate;
	}
	
	

}
