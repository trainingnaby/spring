package com.formation.domain;

public class Addresse {

	private String idAddresse;
	private String ville;
	private int codePostal;
	private String rue;
	private int numero;

	public String getIdAddresse() {
		return idAddresse;
	}

	public void setIdAddresse(String idAddresse) {
		this.idAddresse = idAddresse;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public int getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(int codePostal) {
		this.codePostal = codePostal;
	}

	public String getRue() {
		return rue;
	}

	public void setRue(String rue) {
		this.rue = rue;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	@Override
	public String toString() {
		return "Addresse [idAddresse=" + idAddresse + ", ville=" + ville + ", codePostal=" + codePostal + ", rue=" + rue
				+ ", numero=" + numero + "]";
	}

}
