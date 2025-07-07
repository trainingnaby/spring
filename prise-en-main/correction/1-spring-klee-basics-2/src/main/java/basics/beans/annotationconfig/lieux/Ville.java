package basics.beans.annotationconfig.lieux;

import org.springframework.stereotype.Component;

@Component("ma_ville")
public class Ville {

	private String nomVille = "Lille";

	public String getNomVille() {
		return nomVille;
	}

	public void setNomVille(String nomVille) {
		this.nomVille = nomVille;
	}

	@Override
	public String toString() {
		return "Ville [nomVille=" + nomVille + "]";
	}

}
