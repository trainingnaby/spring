# TP 15 - Sécurisation des endpoints REST avec JWT

## Objectif du TP

Dans le TP précédent, les endpoints REST étaient sécurisés avec HTTP Basic. Cela fonctionne très bien pour une démonstration, mais dans beaucoup d'applications modernes on préfère utiliser un token transmis dans l'en-tête HTTP.

Dans ce TP, on remplace donc l'authentification Basic de l'API REST par une authentification JWT simple.

Le front Thymeleaf reste sécurisé comme avant avec :

- formulaire de login ;
- session HTTP ;
- CSRF actif sur les formulaires HTML.

La partie REST utilise maintenant :

- `POST /api/auth/login` pour obtenir un token ;
- `Authorization: Bearer <token>` pour appeler les endpoints REST ;
- une session stateless ;
- des réponses d'erreur JSON avec `ProblemDetail`.

## Notions à connaitre


- ce qu'est un JWT ;
- la différence entre authentification par session et authentification par token ;
- pourquoi une API REST est généralement stateless ;
- où se place un filtre JWT dans la chaîne Spring Security ;
- pourquoi on désactive CSRF sur une API REST stateless ;
- pourquoi on garde CSRF actif sur les formulaires HTML ;
- comment protéger des routes REST par rôle.

## Rappel important

Un JWT n'est pas chiffré. Il est seulement encodé et signé.

Cela signifie que :

- le client peut lire le contenu du token ;
- le serveur peut vérifier que le token n'a pas été modifié ;
- il ne faut jamais mettre de mot de passe ou de données sensibles dans le payload.

Dans ce TP, l'implémentation JWT est volontairement simple et pédagogique. En production, on utilisera plutôt une bibliothèque spécialisée comme Nimbus JOSE + JWT ou jjwt.

## Comptes de test

Les comptes restent définis en mémoire dans `SecurityConfig`.

| Utilisateur | Mot de passe | Rôles |
|---|---|---|
| `user` | `user` | `ROLE_USER` |
| `admin` | `admin` | `ROLE_USER`, `ROLE_ADMIN` |

## Routes sécurisées

### Endpoint public d'authentification

| Méthode | URL | Sécurité |
|---|---|---|
| `POST` | `/api/auth/login` | Public |

### Endpoints REST duplicatas

| Méthode | URL | Rôle attendu |
|---|---|---|
| `GET` | `/duplicatas` | `USER` ou `ADMIN` |
| `GET` | `/duplicatas/{id}` | `USER` ou `ADMIN` |
| `GET` | `/duplicatas/by-user/{userId}` | `USER` ou `ADMIN` |
| `GET` | `/duplicatas/by-montant` | `USER` ou `ADMIN` |
| `GET` | `/duplicatas/search` | `USER` ou `ADMIN` |
| `GET` | `/duplicatas/jpql` | `USER` ou `ADMIN` |
| `GET` | `/duplicatas/projections` | `USER` ou `ADMIN` |
| `GET` | `/duplicatas/page` | `USER` ou `ADMIN` |
| `POST` | `/duplicatas` | `ADMIN` |
| `POST` | `/duplicatas/{userId}/{montant}` | `ADMIN` |
| `POST` | `/duplicatas_dto` | `ADMIN` |
| `DELETE` | `/duplicatas/{id}` | `ADMIN` |

### Endpoints cache

| Méthode | URL | Rôle attendu |
|---|---|---|
| toutes | `/api/cache/**` | `ADMIN` |

## Fichiers ajoutés ou modifiés

### `pom.xml`

Aucune dépendance JWT externe n'a été ajoutée pour garder le TP simple.

On conserve :

- `spring-boot-starter-security` ;
- `spring-boot-starter-web` ;
- `springdoc-openapi-starter-webmvc-ui` en version `2.6.0` pour rester compatible avec Spring Boot `3.3.5`.

### `application.properties`

Ajout des propriétés suivantes :

```properties
app.security.jwt.secret=formation-spring-jwt-secret-change-me-1234567890
app.security.jwt.expiration-seconds=3600
```

Explication :

- `secret` sert à signer le token ;
- `expiration-seconds` définit la durée de validité du token.

En production, le secret ne doit jamais être stocké en clair dans le dépôt Git.

### `JwtService`

Classe responsable de :

- générer un token JWT ;
- signer le token avec HMAC SHA-256 ;
- extraire le username ;
- vérifier la signature ;
- vérifier l'expiration.

Le token contient notamment :

```json
{
  "sub": "admin",
  "roles": ["ROLE_USER", "ROLE_ADMIN"],
  "iat": 1718000000,
  "exp": 1718003600
}
```

### `JwtAuthenticationFilter`

Filtre Spring Security qui :

1. lit l'en-tête `Authorization` ;
2. vérifie qu'il commence par `Bearer ` ;
3. extrait le token ;
4. valide le token ;
5. charge l'utilisateur ;
6. place l'authentification dans le `SecurityContext`.

### `AuthController`

Contrôleur REST ajouté :

```text
POST /api/auth/login
```

Il reçoit :

```json
{
  "username": "admin",
  "password": "admin"
}
```

Il retourne :

```json
{
  "tokenType": "Bearer",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600,
  "username": "admin",
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```

### `SecurityConfig`

La configuration contient toujours deux chaînes de sécurité.

#### Chaîne 1 : API REST

```java
@Bean
@Order(1)
SecurityFilterChain apiSecurityFilterChain(...)
```

Elle concerne :

```text
/api/auth/**
/duplicatas/**
/duplicatas_dto
/api/cache/**
```

Elle configure :

- CORS ;
- CSRF désactivé ;
- session stateless ;
- formulaire désactivé ;
- Basic Auth désactivé ;
- filtre JWT ajouté avant `UsernamePasswordAuthenticationFilter` ;
- réponses `401` et `403` en `ProblemDetail`.

#### Chaîne 2 : Front MVC

```java
@Bean
@Order(2)
SecurityFilterChain mvcSecurityFilterChain(...)
```

Elle conserve :

- login HTML ;
- logout ;
- session HTTP ;
- CSRF actif ;
- protection des pages `/ui/**`.

## Exercice 1 - Obtenir un token JWT

Avec `curl` :

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

Réponse attendue :

```json
{
  "tokenType": "Bearer",
  "accessToken": "...",
  "expiresIn": 3600,
  "username": "admin",
  "roles": ["ROLE_USER", "ROLE_ADMIN"]
}
```

## Exercice 2 - Appeler une API sans token

```bash
curl -i http://localhost:8080/duplicatas
```

Résultat attendu :

```text
HTTP/1.1 401
Content-Type: application/problem+json
```

La réponse indique qu'un token JWT est requis.

## Exercice 3 - Appeler une API avec un token utilisateur

Récupérer un token avec le compte `user/user` :

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user"}'
```

Puis appeler :

```bash
curl -i http://localhost:8080/duplicatas \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

Résultat attendu :

```text
HTTP/1.1 200
```

## Exercice 4 - Tenter une création avec le rôle USER

```bash
curl -i -X POST http://localhost:8080/duplicatas_dto \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_USER" \
  -d '{"user_id":"123456789","montant":2500}'
```

Résultat attendu :

```text
HTTP/1.1 403
Content-Type: application/problem+json
```

Explication :

- l'utilisateur est authentifié ;
- mais il n'a pas le rôle `ADMIN` ;
- Spring Security renvoie donc `403 Forbidden`.

## Exercice 5 - Créer un duplicata avec le rôle ADMIN

Récupérer un token `admin/admin`, puis appeler :

```bash
curl -i -X POST http://localhost:8080/duplicatas_dto \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ADMIN" \
  -d '{"user_id":"123456789","montant":2500}'
```

Résultat attendu :

```text
HTTP/1.1 200
```

La réponse contient le duplicata créé.

## Exercice 6 - Supprimer un duplicata avec ADMIN

```bash
curl -i -X DELETE http://localhost:8080/duplicatas/ID_DUPLICATA \
  -H "Authorization: Bearer TOKEN_ADMIN"
```

Résultat attendu :

```text
HTTP/1.1 204
```

## Exercice 7 - Tester avec Swagger UI

Accéder à :

```text
http://localhost:8080/swagger-ui.html
```

Étapes :

1. appeler `POST /api/auth/login` ;
2. copier la valeur `accessToken` ;
3. cliquer sur le bouton `Authorize` ;
4. coller uniquement le token ;
5. valider ;
6. appeler les endpoints REST protégés.

Swagger ajoute automatiquement :

```text
Authorization: Bearer <token>
```

## Exercice 8 - Observer la différence avec le front MVC

Le front reste accessible via :

```text
http://localhost:8080/ui/duplicatas
```

Il utilise toujours :

- formulaire de login ;
- cookie de session ;
- CSRF ;
- pages d'erreur HTML.

L'API REST utilise :

- token JWT ;
- pas de session ;
- pas de CSRF ;
- erreurs JSON `ProblemDetail`.

## CSRF et JWT

Dans ce TP, CSRF est désactivé uniquement pour l'API REST.

Pourquoi ?

Parce que le token JWT est envoyé explicitement par le client dans l'en-tête :

```text
Authorization: Bearer <token>
```

Le navigateur ne l'ajoute pas automatiquement comme il le ferait avec un cookie de session.

Pour les formulaires Thymeleaf, CSRF reste actif parce que l'authentification MVC repose sur une session et un cookie.

## CORS

La configuration CORS autorise les origines suivantes :

```text
http://localhost:3000
http://127.0.0.1:5500
```

Cela permet de simuler un front séparé, par exemple :

- React sur `localhost:3000` ;
- une page HTML lancée avec Live Server sur `127.0.0.1:5500`.

## À faire

1. Ajouter les propriétés JWT dans `application.properties`.
2. Créer `JwtService`.
3. Créer `JwtAuthenticationFilter`.
4. Créer les records `LoginRequest` et `LoginResponse`.
5. Créer `AuthController`.
6. Modifier la chaîne de sécurité REST.
7. Désactiver HTTP Basic pour l'API.
8. Ajouter le filtre JWT.
9. Whitelister seulement `/api/auth/login`.
10. Tester les cas `401`, `403`, `200`.
11. Configurer Swagger en Bearer JWT.

## Points de discussion

### Pourquoi deux `SecurityFilterChain` ?

Parce que les besoins sont différents.

Pour le front :

- login HTML ;
- session ;
- CSRF ;
- redirections ;
- pages d'erreur HTML.

Pour l'API REST :

- token ;
- stateless ;
- pas de CSRF ;
- pas de redirection ;
- erreurs JSON.

### Pourquoi `@Order(1)` sur la chaîne REST ?

Spring Security teste les chaînes dans l'ordre.

La chaîne REST doit être évaluée avant la chaîne MVC, sinon les appels REST risqueraient d'être traités comme des pages web classiques.

### Pourquoi `SessionCreationPolicy.STATELESS` ?

Parce que le serveur n'a pas besoin de stocker l'utilisateur en session.

À chaque requête, le client renvoie le token, et le serveur le valide.

## Lancer le projet

Depuis Eclipse :

```text
File > Import > Existing Maven Projects
```

Sélectionner le dossier du projet, puis lancer :

```text
DuplicataImpotsApplication
```

Depuis un terminal :

```bash
mvn spring-boot:run
```

## URLs utiles

```text
Front MVC              : http://localhost:8080/ui/duplicatas
Swagger UI             : http://localhost:8080/swagger-ui.html
Login JWT REST         : http://localhost:8080/api/auth/login
H2 Console             : http://localhost:8080/h2-console
Actuator health        : http://localhost:8080/actuator/health
```

Accès H2 :

```text
JDBC URL  : jdbc:h2:mem:duplicatasdb
User Name : sa
Password  : laisser vide
```
