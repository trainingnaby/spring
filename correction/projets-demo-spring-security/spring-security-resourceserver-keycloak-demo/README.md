
//////////// Projet pour démontrer l'utilisation d'une application comme client JWT de keycloak ///////////////

Préalable : créer un client sur keycloack et des users
	Le client sur keycloak doit avoir ces attributs (laisser le reste par défaut):
		Client authentication : off
		Standard flow : non coché
		Direct access grants : coché
		
///////////// flow ////////////////

Le client demande un token jwt à keycloak, exemple (à importer sur postman) : 
	curl -X POST "http://localhost:8080/VOTRE_REALM/protocol/openid-connect/token" -H "Content-Type: application/x-www-form-urlencoded" -d "client_id=VOTRE_CLIENT_ID" -d "grant_type=password" -d "username=VOTRE_USER_" -d "password=LE_PWD_DU_USER"
	
Ensuite il utilise la valeur du token (à extraire de la réponse) pour appeler les endpoints sécurisés (à importer sur postman):
	curl --location 'http://localhost:8082/private' \
	--header 'Authorization: Bearer VOTRE_TOKEN_JWT' 