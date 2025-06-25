package com.formation.projection;

public interface AppUserProjection {

	 String getNumeroFiscal();
	
	 String getAuthorities();
	
	 String getUsername();
	
	 AddresseProjection getAddresse();
	
	interface AddresseProjection {
		String getVille();
		int getCodePostal();
	}
}
