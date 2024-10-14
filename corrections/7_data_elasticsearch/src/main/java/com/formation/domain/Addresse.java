package com.formation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Addresse {

	@Id
	@Column(name = "id_addresse")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long idAddresse;
	private String ville;
	private int codePostal;
	private String rue;
	private int numero;

	public long getIdAddresse() {
		return idAddresse;
	}

	public void setIdAddresse(long idAddresse) {
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
