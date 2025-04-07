package basics.beans.annotationconfig.universite;

public class DonneesCours {

	private String urlCours, responsable, salleCours;

	public String getUrlCours() {
		return urlCours;
	}

	public void setUrlCours(String urlCours) {
		this.urlCours = urlCours;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public String getSalleCours() {
		return salleCours;
	}

	public void setSalleCours(String salleCours) {
		this.salleCours = salleCours;
	}

	@Override
	public String toString() {
		return "DonneesCours [urlCours=" + urlCours + ", responsable=" + responsable + ", salleCours=" + salleCours
				+ "]";
	}

}
