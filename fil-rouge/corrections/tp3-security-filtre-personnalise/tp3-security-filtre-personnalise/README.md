# TP 3 - Spring Security : filtre d'authentification personnalisé

## Objectif

Dans le TP précédent, HTTP Basic envoyait `login:motDePasse` dans le header `Authorization` à chaque requête.

Ici, on retire HTTP Basic. Le client va envoyer une clé dans un header personnalisé :

```http
X-API-KEY: alice-key-2026
```

Le but n'est pas de présenter l'API Key comme la solution à utiliser partout. Elle sert à comprendre le rôle d'un filtre d'authentification avant d'aborder JWT.

## Import Eclipse

`File > Import > Maven > Existing Maven Projects`, sélectionner le dossier du projet puis terminer l'import.

Lancer `Tp3Application` comme application Spring Boot.

## 1. Vérifier la route publique

```bash
curl -i http://localhost:8080/
```

Aucune authentification n'est nécessaire.

## 2. Appeler une route protégée sans clé

```bash
curl -i http://localhost:8080/api/client
```

La requête n'a pas d'identité authentifiée. Spring Security refuse donc l'accès.

## 3. S'authentifier comme Alice

```bash
curl -i -H "X-API-KEY: alice-key-2026" http://localhost:8080/api/client
```

Le filtre lit la clé, reconnaît Alice et construit un objet `Authentication` possédant `ROLE_USER`.

Chemin suivi :

```text
requête HTTP
    |
    | X-API-KEY: alice-key-2026
    v
ApiKeyAuthenticationFilter
    |
    | clé reconnue
    v
Authentication
    |
    v
SecurityContext
    |
    v
règles d'autorisation
    |
    v
Controller
```

## 4. Alice essaie d'accéder à l'administration

```bash
curl -i -H "X-API-KEY: alice-key-2026" http://localhost:8080/api/admin
```

Alice est authentifiée, mais elle n'a pas `ROLE_ADMIN`. L'accès doit donc être refusé.

C'est la différence entre authentification et autorisation.

## 5. Tester avec l'administrateur

```bash
curl -i -H "X-API-KEY: admin-key-2026" http://localhost:8080/api/admin
```

Puis :

```bash
curl -i -H "X-API-KEY: admin-key-2026" http://localhost:8080/api/client
```

L'administrateur possède `ROLE_USER` et `ROLE_ADMIN`.

## 6. Observer le filtre

La classe importante du TP est `ApiKeyAuthenticationFilter`.

Elle hérite de `OncePerRequestFilter`. Spring garantit ainsi l'exécution du filtre une fois par requête dans le traitement normal du filtre.

Le filtre commence par lire :

```java
String apiKey = request.getHeader("X-API-KEY");
```

Si la clé correspond à Alice ou Admin, il construit une authentification :

```java
var authentication =
    new UsernamePasswordAuthenticationToken(username, null, roles);
```

Puis il la place dans le contexte de sécurité :

```java
SecurityContextHolder.getContext()
    .setAuthentication(authentication);
```

Le contrôleur n'a pas besoin de relire `X-API-KEY`. Pour lui, l'utilisateur est déjà authentifié.

## 7. Pourquoi `filterChain.doFilter(...)` est indispensable ?

Le filtre termine par :

```java
filterChain.doFilter(request, response);
```

Cela signifie : continuer la chaîne.

Sans cet appel, la requête s'arrêterait dans notre filtre et n'arriverait pas au contrôleur.

## 8. Où placer notre filtre ?

Dans `SecurityConfig` :

```java
.addFilterBefore(
    apiKeyFilter,
    UsernamePasswordAuthenticationFilter.class
)
```

Nous indiquons explicitement où insérer notre filtre dans la chaîne Spring Security.

## 9. Pourquoi `STATELESS` ?

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

L'application ne conserve pas l'authentification d'une requête à l'autre dans une session HTTP.

Chaque requête doit donc apporter sa preuve :

```text
requête 1 + X-API-KEY -> Alice
requête 2 sans clé     -> anonyme
requête 3 + X-API-KEY -> Alice
```

Cette idée sera très importante pour le TP JWT.

## 10. Comparaison avec HTTP Basic

HTTP Basic :

```http
Authorization: Basic YWxpY2U6cGFzc3dvcmQ=
```

Filtre du TP :

```http
X-API-KEY: alice-key-2026
```

Dans les deux cas, une information arrive avec la requête. Spring doit la transformer en `Authentication` afin de remplir le `SecurityContext`.

Le TP rend simplement cette mécanique visible.

## 11. Points volontairement simplifiés

Les clés sont écrites en dur pour que le TP reste lisible. Ce n'est pas une bonne pratique de production.

Le filtre ignore également une clé inconnue et laisse Spring Security traiter ensuite la requête comme non authentifiée. Dans une application réelle, on pourrait retourner une réponse d'erreur plus précise.

Enfin, une API Key simple n'apporte pas les propriétés d'un JWT : claims, expiration portée par le jeton, signature, issuer, etc.

## Exercices

1. Ajouter un utilisateur `bob` avec une nouvelle clé et `ROLE_USER`.
2. Ajouter `/api/support` réservé à `ROLE_SUPPORT`.
3. Mettre un point d'arrêt dans `doFilterInternal` et suivre la requête.
4. Afficher `SecurityContextHolder.getContext().getAuthentication()` avant puis après authentification.
5. Envoyer une clé incorrecte et expliquer le résultat.
6. Remplacer le nom du header `X-API-KEY` par `X-FORMATION-KEY`.

## À retenir

Le filtre personnalisé n'est pas le contrôleur et ne contient pas le métier. Il intervient en amont pour établir l'identité de l'appelant.

Le fil conducteur pour la suite est :

```text
preuve reçue dans la requête
        -> filtre
        -> Authentication
        -> SecurityContext
        -> autorisation
        -> Controller
```

Dans le prochain TP, la preuve ne sera plus une simple chaîne `alice-key-2026`, mais un Bearer Token JWT.
