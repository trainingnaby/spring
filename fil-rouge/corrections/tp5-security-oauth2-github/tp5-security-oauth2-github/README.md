# TP 5 - Spring Security OAuth2 avec GitHub

## Objectifs

Ce TP termine la progression Form Login -> HTTP Basic -> filtre personnalisé -> JWT -> OAuth2.

À la fin, vous saurez expliquer :
- le rôle du client (notre application Spring), du fournisseur OAuth2 (GitHub) et de l'utilisateur ;
- le flux Authorization Code ;
- la différence entre les identifiants GitHub et le Client ID / Client Secret de l'application ;
- comment Spring Security crée un utilisateur authentifié à partir des informations retournées par GitHub ;
- pourquoi le Client Secret ne doit jamais être commité.

> Important : GitHub est utilisé ici comme fournisseur OAuth2 pour simplifier le TP. Ce TP illustre OAuth2 Login. GitHub OAuth Apps n'est pas présenté ici comme un fournisseur OpenID Connect : ne cherchez donc pas un `OidcUser` ou un ID Token comme dans le TP Keycloak. Le contrôleur utilise `OAuth2User`.

## 1. Prérequis

- Java 17+
- Maven
- Eclipse / Spring Tools
- un compte GitHub
- accès Internet

Aucun Keycloak et aucun Docker ne sont nécessaires.

## 2. Importer dans Eclipse

File -> Import -> Maven -> Existing Maven Projects, sélectionner ce dossier puis Finish.

## 3. Créer l'OAuth App dans GitHub

Dans GitHub : Settings -> Developer settings -> OAuth Apps -> New OAuth App.

Valeurs pour le TP :

- Application name : `TP5 Spring Security`
- Homepage URL : `http://localhost:8080`
- Authorization callback URL : `http://localhost:8080/login/oauth2/code/github`

Enregistrer l'application puis récupérer :
- Client ID
- Client Secret

Le callback est important : après l'autorisation, GitHub y renvoie le navigateur avec un code temporaire. Spring Security traite automatiquement cette URL.

## 4. Ne pas mettre le secret dans application.properties

Le projet attend deux variables d'environnement :

```text
GITHUB_CLIENT_ID
GITHUB_CLIENT_SECRET
```

Dans Eclipse : Run -> Run Configurations -> Spring Boot App (ou Java Application) -> Environment -> New.

Ajouter les deux variables avec les valeurs fournies par GitHub.

`application.properties` contient :

```properties
spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}
spring.security.oauth2.client.registration.github.scope=read:user,user:email
```

Spring Security connaît déjà les endpoints standards du provider GitHub lorsque l'identifiant d'enregistrement est `github`. Il n'y a donc pas d'`issuer-uri` Keycloak à configurer.

## 5. Démarrer

Lancer `Tp5Application`, puis ouvrir :

```text
http://localhost:8080
```

Cliquer sur **Se connecter avec GitHub**.

Le lien utilisé par Spring est :

```text
/oauth2/authorization/github
```

## 6. Observer le flux

Le navigateur suit approximativement ce chemin :

```text
Navigateur
    |
    | GET /oauth2/authorization/github
    v
Application Spring Security
    |
    | redirection vers GitHub
    v
GitHub
    |
    | authentification + autorisation
    | retourne un code temporaire
    v
/login/oauth2/code/github
    |
    | Spring échange le code contre un Access Token
    | puis récupère les informations utilisateur
    v
SecurityContext
    |
    v
/profil
```

Point pédagogique : le mot de passe GitHub n'est jamais donné à notre application Spring.

## 7. Observer l'utilisateur Spring Security

Le contrôleur reçoit :

```java
@AuthenticationPrincipal OAuth2User user
```

Essayez :

```text
http://localhost:8080/api/me
```

Vous verrez les attributs renvoyés pour le compte GitHub, par exemple `login`, `id`, `avatar_url`, `html_url` et éventuellement `name`.

## 8. OAuth2 et le TP JWT précédent

TP4 : notre application vérifiait elle-même le Bearer JWT utilisé pour appeler son API.

TP5 : l'authentification est déléguée à GitHub. Spring Security gère le flux OAuth2, l'échange du code et la récupération des informations utilisateur.

Ce sont deux usages différents qu'il ne faut pas confondre simplement parce qu'ils utilisent tous deux des tokens.

## 9. OAuth2 n'est pas automatiquement OpenID Connect

C'est une différence importante avec la version Keycloak du TP.

OAuth2 traite avant tout de l'autorisation déléguée. OpenID Connect ajoute une couche d'identité standardisée au-dessus d'OAuth2, notamment avec l'ID Token et les scopes OIDC tels que `openid`.

Dans cette variante GitHub OAuth App :

```java
OAuth2User
```

Dans un fournisseur OIDC tel que Keycloak/Google configuré en OIDC, on rencontre généralement :

```java
OidcUser
```

Pour un premier TP, GitHub permet donc d'apprendre le flux OAuth2 avec très peu d'infrastructure. Vous pouvez présenter OIDC conceptuellement à la fin.

## 10. 401, 403 et redirection

Avec `oauth2Login()`, lorsqu'un navigateur demande une page protégée, Spring Security peut rediriger vers le mécanisme de connexion au lieu de répondre comme une API REST stateless. C'est normal : ce TP représente une application web avec login OAuth2.

## 11. Déconnexion

Le bouton de la page `/profil` exécute un POST `/logout` avec le token CSRF Thymeleaf automatiquement intégré.

Cela termine la session de notre application Spring. Cela ne signifie pas que l'utilisateur est déconnecté de son compte GitHub dans le navigateur.

## 12. Exercices stagiaires

1. Protéger une nouvelle URL `/prive`.
2. Afficher l'avatar et le login GitHub.
3. Mettre un breakpoint dans `profil()` et inspecter `OAuth2User`.
4. Retirer le scope `user:email` et observer les différences.
5. Ouvrir les outils réseau du navigateur et repérer les redirections.
6. Expliquer pourquoi `GITHUB_CLIENT_SECRET` ne doit jamais apparaître dans Git.
7. Comparer `OAuth2User` de ce TP avec l'`Authentication` construite manuellement dans le TP3.
8. Comparer la déconnexion de ce TP avec le JWT stateless du TP4.

## 13. Questions de synthèse

- Qui authentifie réellement l'utilisateur ?
- Notre application voit-elle le mot de passe GitHub ?
- À quoi sert le code temporaire retourné au callback ?
- À quoi sert le Client Secret ?
- Quelle différence entre OAuth2 et OpenID Connect ?
- Pourquoi utilise-t-on `OAuth2User` ici plutôt que `OidcUser` ?

## Résumé formateur

```text
TP1 Form Login       -> login géré par notre application + session
TP2 HTTP Basic       -> credentials HTTP à chaque requête
TP3 Filtre custom    -> nous construisons Authentication
TP4 JWT              -> Bearer token signé + API stateless
TP5 GitHub OAuth2    -> authentification déléguée + Authorization Code
```

Le message principal du TP5 : **l'application ne demande plus directement le mot de passe de l'utilisateur ; elle délègue l'authentification à un fournisseur et Spring Security orchestre le flux OAuth2.**
