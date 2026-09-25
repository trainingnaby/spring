# TP 2 - Spring Security : authentification HTTP Basic

## Objectif

Ce TP reprend les utilisateurs et les règles d'autorisation du TP 1, mais remplace le formulaire par HTTP Basic.

À la fin du TP, vous devez savoir expliquer :

- le rôle de l'en-tête `Authorization` ;
- la différence entre `401 Unauthorized` et `403 Forbidden` ;
- pourquoi Base64 n'est pas du chiffrement ;
- la différence entre HTTP Basic et une session HTTP ;
- pourquoi le navigateur peut sembler rester connecté même après suppression des cookies.

## Import Eclipse

`File > Import > Maven > Existing Maven Projects`, puis sélectionner le dossier du projet.

Classe de démarrage : `com.formation.SecurityHttpBasicApplication`.

## Comptes

| Utilisateur | Mot de passe | Rôles |
|---|---|---|
| alice | alice123 | USER |
| admin | admin123 | USER, ADMIN |

## 1. Configuration

La partie essentielle de `SecurityConfig` est :

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/", "/error", "/favicon.ico",
                     "/assets/**", "/css/**", "/js/**", "/images/**")
        .permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/client/**", "/api/me").hasRole("USER")
    .anyRequest().authenticated()
)
.httpBasic(Customizer.withDefaults())
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
);
```

Les ressources statiques sont explicitement publiques. C'est important pour les tests au navigateur : une requête automatique vers un favicon ou un fichier statique ne doit pas provoquer un challenge HTTP Basic.

## 2. Tester d'abord avec curl

HTTP Basic est un mécanisme HTTP. Pour l'observer sans comportement automatique du navigateur, `curl` est le client le plus clair.

Endpoint public :

```bash
curl -i http://localhost:8080/
```

Résultat attendu : `200 OK`, sans authentification.

Ressource protégée sans credentials :

```bash
curl -i http://localhost:8080/api/client
```

Résultat attendu : `401 Unauthorized` avec un en-tête `WWW-Authenticate: Basic ...`.

## 3. Alice

```bash
curl -i -u alice:alice123 http://localhost:8080/api/client
```

Résultat attendu : `200 OK`.

Puis :

```bash
curl -i -u alice:alice123 http://localhost:8080/api/admin
```

Résultat attendu : `403 Forbidden`.

Alice est bien **authentifiée**, mais elle n'a pas le rôle `ADMIN`.

## 4. Admin

```bash
curl -i -u admin:admin123 http://localhost:8080/api/admin
curl -i -u admin:admin123 http://localhost:8080/api/client
```

Les deux appels doivent retourner `200`, car admin possède `ROLE_USER` et `ROLE_ADMIN`.

## 5. Voir l'authentification

```bash
curl -s -u alice:alice123 http://localhost:8080/api/me
```

Puis :

```bash
curl -s -u admin:admin123 http://localhost:8080/api/me
```

Comparer les authorities.

## 6. 401 et 403

À retenir :

```text
Pas de credentials / mauvais credentials
                |
                v
        401 Unauthorized

Credentials valides
mais rôle insuffisant
                |
                v
         403 Forbidden
```

Test mauvais mot de passe :

```bash
curl -i -u alice:mauvais http://localhost:8080/api/client
```

Résultat attendu : `401`.

## 7. Ce que contient HTTP Basic

Le client construit la chaîne :

```text
alice:alice123
```

puis l'encode en Base64 et envoie :

```text
Authorization: Basic <valeur Base64>
```

Base64 est un **encodage**, pas un chiffrement. HTTP Basic doit donc être transporté sur HTTPS dans un contexte réel.

Pour observer l'en-tête :

```bash
curl -v -u alice:alice123 http://localhost:8080/api/client
```

## 8. Pourquoi le navigateur reste-t-il "connecté" ?

C'est un point important du TP.

Après avoir saisi `alice / alice123` dans la fenêtre HTTP Basic, le navigateur peut mémoriser les credentials pour le serveur et les renvoyer automatiquement :

```text
Navigateur
   |
   | GET /api/client
   | Authorization: Basic ...
   v
Spring Security
```

Cela ne dépend pas d'un cookie `JSESSIONID`.

Le TP utilise :

```java
SessionCreationPolicy.STATELESS
```

Le serveur ne conserve donc pas l'authentification d'Alice dans une session entre les requêtes. C'est **le navigateur qui renvoie l'en-tête Authorization**.

Supprimer les cookies, `localStorage` ou `sessionStorage` ne supprime donc pas forcément le cache d'authentification HTTP Basic du navigateur.

Pour refaire un test propre :

- utiliser `curl` ; ou
- ouvrir une nouvelle fenêtre privée ; ou
- fermer complètement les fenêtres du navigateur concerné puis le relancer, selon son comportement.

Dans l'onglet Network, vérifier l'en-tête de requête :

```text
Authorization: Basic ...
```

S'il est présent, les credentials sont bien renvoyés par le client.

## 9. Pourquoi il n'y a pas de logout ?

Dans le TP 1 :

```text
Form Login -> session -> JSESSIONID -> logout détruit la session
```

Dans ce TP :

```text
HTTP Basic -> Authorization à chaque requête
             +
             STATELESS
```

Il n'y a donc pas de session d'authentification à détruire côté serveur.

Le bouton logout du TP 1 n'a pas de sens ici.

## 10. Parcours d'une requête

```text
GET /api/client
Authorization: Basic ...
          |
          v
Spring Security Filter Chain
          |
          v
extraction username/password
          |
          v
AuthenticationManager
          |
          v
UserDetailsService
          |
          v
PasswordEncoder
          |
          v
Authentication
          |
          v
SecurityContext
          |
          v
contrôle ROLE_USER
          |
          v
Controller
```

Le contrôleur ne lit jamais lui-même le header `Authorization`.

## 11. Comparaison avec le TP 1

| Form Login | HTTP Basic dans ce TP |
|---|---|
| formulaire de connexion | popup/client HTTP |
| session après login | credentials sur chaque requête |
| cookie JSESSIONID | header Authorization |
| logout côté serveur | pas de logout serveur utile ici |
| navigateur Web MVC | simple pour démonstrations et clients HTTP |

Important : **HTTP Basic et STATELESS sont deux notions distinctes**. Nous avons volontairement configuré `STATELESS` pour rendre le fonctionnement visible.

## Exercices

1. Ajouter `bob / bob123`, rôle `USER`, puis vérifier `/api/client` et `/api/admin`.
2. Ajouter `GET /api/public/info` et le rendre public.
3. Ajouter un rôle `SUPPORT` et protéger `GET /api/support`.
4. Retirer temporairement `SessionCreationPolicy.STATELESS` et observer la différence. Ne pas conclure que HTTP Basic implique automatiquement une politique stateless.

## À retenir

HTTP Basic transporte les credentials dans le header `Authorization`. Spring Security les vérifie puis construit une `Authentication` pour la requête. Les règles `permitAll()`, `hasRole(...)` et `authenticated()` déterminent ensuite l'autorisation.

Pour ce TP, préférez `curl` pour les démonstrations de `401`, `403` et des changements d'utilisateur : le navigateur possède son propre mécanisme de mémorisation HTTP Basic, indépendant des cookies.
