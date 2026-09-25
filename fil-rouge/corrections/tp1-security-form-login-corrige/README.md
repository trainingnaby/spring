# TP 1 — Corrigé : Spring Security et Form Login

Ce projet est le corrigé du premier TP consacré à Spring Security. Il reste volontairement simple : utilisateurs en mémoire, formulaire de connexion généré par Spring Security et pages Thymeleaf.

## Comptes de démonstration

- `alice / alice123` — rôle `USER`
- `admin / admin123` — rôles `USER` et `ADMIN`

L'administrateur possède aussi `USER`. Il peut donc ouvrir `/espace-client` puis revenir sur `/admin` sans changer de compte.

## URLs à tester

- `/` : page publique
- `/espace-client` : utilisateur connecté avec le rôle `USER`
- `/admin` : utilisateur connecté avec le rôle `ADMIN`
- `/logout` : traité par Spring Security en POST

## Configuration de sécurité

`SecurityConfig` définit :

1. un `PasswordEncoder` ;
2. un `UserDetailsService` en mémoire ;
3. un `SecurityFilterChain` avec les règles d'autorisation ;
4. le formulaire de connexion ;
5. la déconnexion et la suppression de la session.

```text
/                         -> public
/espace-client/**         -> ROLE_USER
/admin/**                 -> ROLE_ADMIN
reste                     -> utilisateur authentifié
```

Après une connexion réussie, Spring conserve l'authentification dans la session HTTP. Le navigateur reçoit le cookie `JSESSIONID` et le renvoie lors des requêtes suivantes.

## Déconnexion et CSRF

La déconnexion est volontairement faite avec un formulaire `POST` :

```html
<form th:action="@{/logout}" method="post">
    <button type="submit">Se déconnecter</button>
</form>
```

Le `th:action` est important. Avec Thymeleaf et Spring Security, le formulaire reçoit le jeton CSRF attendu par Spring Security. Un formulaire écrit uniquement avec `action="/logout"` peut conduire à un `403 Forbidden`, car le POST ne contient pas ce jeton.

Après la déconnexion, Spring invalide la session, supprime le cookie `JSESSIONID` et redirige vers `/?logout`.

## Manipulation conseillée

1. Ouvrir `/espace-client` sans être connecté : Spring redirige vers le formulaire de connexion.
2. Se connecter avec `alice`. La page utilisateur est accessible.
3. Essayer `/admin` : Alice est authentifiée mais ne possède pas `ROLE_ADMIN`, l'accès est donc refusé.
4. Se déconnecter avec le bouton.
5. Se connecter avec `admin`.
6. Ouvrir `/admin`, puis `/espace-client`. Les deux pages sont accessibles car `admin` possède `USER` et `ADMIN`.
7. Se déconnecter depuis l'une ou l'autre page et vérifier le retour sur la page d'accueil.

## À retenir

Authentification et autorisation sont deux étapes différentes. `alice` peut être correctement authentifiée tout en n'étant pas autorisée à consulter `/admin`.

`hasRole("ADMIN")` recherche l'authority `ROLE_ADMIN`. Lorsque les utilisateurs sont créés avec `.roles("ADMIN")`, Spring ajoute le préfixe `ROLE_`.

La protection CSRF reste activée dans ce TP. On ne la désactive pas simplement pour faire fonctionner le bouton de déconnexion : on envoie correctement le jeton attendu.


## Redirection après connexion

Le TP force volontairement la redirection vers `/espace-client` après une authentification réussie :

```java
.defaultSuccessUrl("/espace-client", true)
```

Le second argument à `true` évite qu'une ancienne requête mémorisée (par exemple une ressource statique demandée par le navigateur) devienne la destination après le login. Les ressources statiques usuelles (`/css/**`, `/js/**`, `/images/**`, `/assets/**`) sont également autorisées sans authentification.
