# Démo Spring Security 6 - OAuth2 Login avec GitHub

Ce projet montre comment sécuriser une application Spring Boot avec **Spring Security 6** en utilisant **GitHub comme serveur d'autorisation OAuth2**.

L'objectif pédagogique est de comprendre le scénario suivant :

```text
Utilisateur
    ↓
Application Spring Boot
    ↓ redirection
GitHub Authorization Server
    ↓ callback
Application Spring Boot
    ↓
Session utilisateur Spring Security
```

Dans cette démo, l'application Spring Boot ne gère pas elle-même le mot de passe de l'utilisateur. Elle délègue l'authentification à GitHub grâce à OAuth2 Login.

---

## 1. Concepts illustrés

Cette démo permet d'expliquer :

- le rôle d'un **client OAuth2** ;
- le rôle d'un **Authorization Server** ;
- le flow **Authorization Code** ;
- la redirection vers GitHub ;
- la notion de **callback URL** ;
- la récupération du profil utilisateur GitHub ;
- la différence entre authentification GitHub et session locale Spring ;
- le logout côté application Spring.

---

## 2. Technologies utilisées

- Java 17+
- Maven
- Spring Boot 3.5.14
- Spring Security 6
- Spring Security OAuth2 Client
- Thymeleaf
- GitHub OAuth App

Pourquoi Spring Boot 3.x ?

Spring Boot 3.x utilise Spring Security 6. Spring Boot 4.x est associé à Spring Security 7. Pour une formation centrée sur Spring Security 6, Spring Boot 3.x est donc le bon choix.

---

## 3. Structure du projet

```text
spring-security-oauth2-github-demo
├── pom.xml
├── README.md
└── src
    └── main
        ├── java
        │   └── com.example.oauth2github
        │       ├── Oauth2GithubDemoApplication.java
        │       ├── config
        │       │   └── SecurityConfig.java
        │       └── controller
        │           └── PageController.java
        └── resources
            ├── application.yml
            ├── static
            │   └── css
            │       └── style.css
            └── templates
                ├── home.html
                ├── login.html
                ├── profile.html
                └── public.html
```

---

## 4. Créer une OAuth App GitHub

Avant de lancer le projet, il faut créer une application OAuth côté GitHub.

### Étapes

1. Aller dans GitHub.
2. Ouvrir :

```text
Settings → Developer settings → OAuth Apps → New OAuth App
```

3. Renseigner les champs :

```text
Application name:
Spring Security OAuth2 GitHub Demo

Homepage URL:
http://localhost:8080

Authorization callback URL:
http://localhost:8080/login/oauth2/code/github
```

4. Créer l'application.
5. Copier :

```text
Client ID
Client Secret
```

La callback URL est très importante. Spring Security utilise par défaut ce format :

```text
/login/oauth2/code/{registrationId}
```

Ici, le `registrationId` est `github`, donc l'URL complète est :

```text
http://localhost:8080/login/oauth2/code/github
```

---

## 5. Configuration de l'application

Le fichier `src/main/resources/application.yml` contient :

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: ${GITHUB_CLIENT_ID:}
            client-secret: ${GITHUB_CLIENT_SECRET:}
            scope:
              - read:user
              - user:email
```

Les valeurs sensibles ne sont pas écrites directement dans le projet. Elles sont lues depuis des variables d'environnement :

```text
GITHUB_CLIENT_ID
GITHUB_CLIENT_SECRET
```

---

## 6. Lancer le projet en ligne de commande

### Linux / macOS

```bash
export GITHUB_CLIENT_ID=votre_client_id
export GITHUB_CLIENT_SECRET=votre_client_secret
mvn spring-boot:run
```

### Windows PowerShell

```powershell
$env:GITHUB_CLIENT_ID="votre_client_id"
$env:GITHUB_CLIENT_SECRET="votre_client_secret"
mvn spring-boot:run
```

Puis ouvrir :

```text
http://localhost:8080
```

---

## 7. Importer dans Eclipse

1. Ouvrir Eclipse.
2. Aller dans :

```text
File → Import → Maven → Existing Maven Projects
```

3. Sélectionner le dossier du projet.
4. Cliquer sur **Finish**.
5. Attendre le téléchargement des dépendances Maven.
6. Configurer les variables d'environnement dans la configuration de lancement :

```text
Run Configurations → Java Application → Environment
```

Ajouter :

```text
GITHUB_CLIENT_ID=votre_client_id
GITHUB_CLIENT_SECRET=votre_client_secret
```

7. Lancer la classe :

```text
Oauth2GithubDemoApplication
```

---

## 8. Pages disponibles

### Page d'accueil

```text
http://localhost:8080/
```

Accessible sans authentification.

---

### Page publique

```text
http://localhost:8080/public
```

Accessible sans login.

Dans `SecurityConfig` :

```java
.requestMatchers("/", "/public", "/css/**", "/error").permitAll()
```

---

### Page de login

```text
http://localhost:8080/login
```

Cette page contient un lien vers :

```text
/oauth2/authorization/github
```

Cette URL est fournie par Spring Security. Elle déclenche le démarrage du flow OAuth2.

---

### Profil privé

```text
http://localhost:8080/profile
```

Cette page nécessite une authentification.

Si l'utilisateur n'est pas connecté, Spring Security le redirige vers GitHub.

---

### Endpoint JSON utilisateur

```text
http://localhost:8080/me
```

Retourne les attributs utilisateur reçus depuis GitHub.

Exemple :

```json
{
  "login": "octocat",
  "id": 123456,
  "avatar_url": "https://avatars.githubusercontent.com/...",
  "html_url": "https://github.com/octocat"
}
```

---

### Informations sur le token

```text
http://localhost:8080/token-info
```

Retourne quelques informations pédagogiques sur le token :

```json
{
  "clientRegistrationId": "github",
  "tokenType": "Bearer",
  "scopes": ["read:user", "user:email"],
  "expiresAt": "...",
  "note": "Par sécurité, la valeur de l'access token n'est pas affichée."
}
```

La valeur brute de l'access token n'est volontairement pas affichée.

---

## 9. Configuration Spring Security

Classe :

```text
src/main/java/com/example/oauth2github/config/SecurityConfig.java
```

Configuration principale :

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/public", "/css/**", "/error").permitAll()
                .requestMatchers("/profile", "/me", "/token-info").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/profile", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }
}
```

### Remarque sur `@EnableWebSecurity`

Avec Spring Boot, cette annotation est souvent facultative car l'auto-configuration active Spring Security automatiquement.

Elle est gardée ici pour une raison pédagogique

---

## 10. Déroulement du flow OAuth2

Quand l'utilisateur clique sur **Se connecter avec GitHub** :

```text
1. L'utilisateur ouvre /login
2. Il clique sur /oauth2/authorization/github
3. Spring Security redirige vers GitHub
4. GitHub demande l'autorisation
5. GitHub redirige vers /login/oauth2/code/github
6. Spring Security échange le code contre un access token
7. Spring Security appelle GitHub pour récupérer le profil utilisateur
8. Spring crée une Authentication locale
9. L'utilisateur accède à /profile
```

---

## 11. Notes

### GitHub est le serveur d'autorisation

GitHub est responsable de :

- authentifier l'utilisateur ;
- demander son consentement ;
- délivrer un access token ;
- exposer une API utilisateur.

---

### Spring Boot est le client OAuth2

L'application Spring est responsable de :

- rediriger l'utilisateur vers GitHub ;
- recevoir le callback ;
- échanger le code contre un token ;
- récupérer les informations utilisateur ;
- créer une session locale.

---

### Le login GitHub ne remplace pas la session Spring

Après le retour de GitHub, Spring Security crée sa propre session applicative.

```text
GitHub authentifie
        ↓
Spring Security crée une session locale
        ↓
L'utilisateur navigue dans l'application
```

---

### Logout Spring différent de logout GitHub

Quand l'utilisateur clique sur logout, il est déconnecté de l'application Spring.

Mais il reste généralement connecté à GitHub dans son navigateur.

Donc, s'il clique de nouveau sur login, GitHub peut le reconnecter rapidement sans redemander le mot de passe.

C'est normal.

---

## 12. Tests pédagogiques à faire

### Test 1 : Accéder à une page publique

Ouvrir :

```text
http://localhost:8080/public
```

Résultat attendu : accès direct.

---

### Test 2 : Accéder à une page protégée

Ouvrir :

```text
http://localhost:8080/profile
```

Résultat attendu : redirection vers `/login`, puis vers GitHub.

---

### Test 3 : Se connecter avec GitHub

Cliquer sur :

```text
Se connecter avec GitHub
```

Résultat attendu : GitHub demande l'autorisation, puis l'application affiche le profil.

---

### Test 4 : Voir les attributs utilisateur

Ouvrir :

```text
http://localhost:8080/me
```

Résultat attendu : JSON avec les informations du compte GitHub.

---

### Test 5 : Tester une mauvaise callback URL

Dans GitHub, changer temporairement la callback URL, par exemple :

```text
http://localhost:8080/mauvaise-url
```

Résultat attendu : erreur OAuth2.

Objectif : montrer que la `redirect_uri` doit correspondre exactement.

Remettre ensuite :

```text
http://localhost:8080/login/oauth2/code/github
```

---

### Test 6 : Tester le logout

1. Se connecter.
2. Cliquer sur logout.
3. Recliquer sur login.

Observation : GitHub peut ne pas redemander le mot de passe.

Conclusion pédagogique :

```text
Logout de l'application Spring ≠ logout global de GitHub
```

---

## 13. Différence avec la démo précédente Form Login

Dans la démo Form Login :

```text
Utilisateur
    ↓
Spring Boot
    ↓
Formulaire local
```

Spring Security vérifie directement le username/password.

Dans cette démo OAuth2 :

```text
Utilisateur
    ↓
Spring Boot
    ↓
GitHub
```

Spring Security délègue l'authentification à GitHub.

---

## 14. Limite importante de cette démo

GitHub est très pratique pour montrer OAuth2 Login, mais ce n'est pas le meilleur exemple pour expliquer OpenID Connect.

Avec GitHub, Spring récupère surtout :

```text
Access Token
+
UserInfo via API GitHub
```

Pour expliquer OpenID Connect, il vaut mieux faire ensuite une démo avec Google, car Google fournit un **ID Token** standard OIDC.

Formule pédagogique :

```text
GitHub : très bon pour comprendre OAuth2 Login
Google : meilleur pour comprendre OpenID Connect et ID Token
```

---

## 15. Problèmes fréquents

### Erreur : client-id vide

Vérifier que les variables d'environnement sont bien définies :

```text
GITHUB_CLIENT_ID
GITHUB_CLIENT_SECRET
```

---

### Erreur : redirect_uri mismatch

Vérifier dans GitHub :

```text
Authorization callback URL = http://localhost:8080/login/oauth2/code/github
```

---

### Erreur après login

Vérifier que l'application tourne bien sur le port 8080 :

```yaml
server:
  port: 8080
```

Si vous changez le port, il faut aussi changer la callback URL dans GitHub.

---

## 16. Commandes utiles

Compiler :

```bash
mvn clean package
```

Lancer :

```bash
mvn spring-boot:run
```

Nettoyer :

```bash
mvn clean
```

---

## 17. Message clé

OAuth2 Login permet à une application Spring de déléguer la connexion à un fournisseur externe comme GitHub.

L'application ne manipule pas le mot de passe GitHub de l'utilisateur. Elle reçoit un résultat d'authentification et crée ensuite sa propre session Spring Security.
