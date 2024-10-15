package com.formation.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SalesforceAuthenticationProvider implements AuthenticationProvider{

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		// récupérer les infos entrées dans le formulaire
		String login = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		
		//Réellement voici les actions à faire 
		
		// 1 - on appelle le endpoint du service Salesforce pour s'authentifier (en lui passant le login et le pwd)
		//2 - si ok, le service renvoie les données du user (username, roles ...)
		//3 - Construire un objet de type Authentication et le remplir avec les infos reçues du service
		//4 - Spring va "brancher" ce code dans le contexte
		
		// simulation de l'appel au service, on conside ok dans le cas suivant
		if(login.equals("naby") && pwd.equals("naby")) {
			final List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			final UserDetails principal = new User(login, pwd, authorities);
			final Authentication auth = new UsernamePasswordAuthenticationToken(principal, pwd, authorities);
			return auth;
		}
		return null;
	}

	// Le provider définir quelle d'authentification il peut gérer : par login/pwd ? via un header http ? via jwt ..
	@Override
	public boolean supports(Class<?> authentication) {
		// ce provider peut éventuellement authentifier via un login et un mot de passe
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
