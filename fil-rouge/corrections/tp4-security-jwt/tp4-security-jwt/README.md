# TP 4 - Spring Security : authentification par JWT

## Objectif

Dans le TP 3, notre filtre recevait une valeur très simple :

```http
X-API-KEY: alice-key-2026
```

Cette fois, le client commence par s'authentifier avec un login et un mot de passe. Si les credentials sont corrects, l'application lui remet un **JWT**. Les appels suivants envoient ce jeton dans le header HTTP `Authorization`.

```text
POST /auth/login
alice + alice123
        |
        v
authentification
        |
        v
création du JWT
        |
        v
client reçoit le token

GET /api/client
Authorization: Bearer eyJ...
        |
        v
JwtAuthenticationFilter
        |
        v
validation du JWT
        |
        v
Authentication -> SecurityContext
        |
        v
Controller
```

L'objectif est de comprendre le mécanisme. On ne cherche pas encore à mettre en place OAuth2 ou OpenID Connect : ce sera l'étape suivante.

---

## Import dans Eclipse

`File > Import > Maven > Existing Maven Projects`

Sélectionner le dossier `tp4-security-jwt`, terminer l'import puis lancer `Tp4Application` comme application Spring Boot.

Le projet utilise Java 17 et Spring Boot 3.5.6.

---

## 1. Les utilisateurs du TP

Deux utilisateurs sont déclarés en mémoire :

| Utilisateur | Mot de passe | Rôles |
|---|---|---|
| `alice` | `alice123` | `USER` |
| `admin` | `admin123` | `USER`, `ADMIN` |

Ils servent uniquement au TP.

---

## 2. Vérifier la route publique

```bash
curl -i http://localhost:8080/
```

Cette route est accessible sans token.

Essayez maintenant :

```bash
curl -i http://localhost:8080/api/client
```

Résultat attendu :

```text
HTTP/1.1 401
```

La ressource exige une identité authentifiée.

---

## 3. Obtenir un JWT pour Alice

Le login se fait avec une requête POST :

```bash
curl -i -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"alice123"}'
```

La réponse ressemble à :

```json
{
  "tokenType": "Bearer",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 900
}
```

Copiez la valeur de `accessToken`.

Le mot de passe n'est pas placé dans le JWT. Il sert à l'authentification initiale.

---

## 4. Utiliser le JWT

Remplacez `<TOKEN_ALICE>` par le jeton obtenu :

```bash
curl -i http://localhost:8080/api/client \
  -H "Authorization: Bearer <TOKEN_ALICE>"
```

Cette fois, la requête doit passer.

Le header suit le format :

```http
Authorization: Bearer eyJ...
```

`Bearer` indique que celui qui possède le jeton peut le présenter au serveur. Il faut donc traiter un access token comme une donnée sensible.

---

## 5. Authentification et autorisation restent deux choses différentes

Avec le token d'Alice :

```bash
curl -i http://localhost:8080/api/admin \
  -H "Authorization: Bearer <TOKEN_ALICE>"
```

Alice est bien authentifiée, mais elle ne possède pas `ROLE_ADMIN`.

Résultat attendu :

```text
HTTP/1.1 403
```

Rappel :

```text
401 : je ne dispose pas d'une authentification valide
403 : je suis authentifié, mais je n'ai pas le droit
```

---

## 6. Se connecter comme administrateur

```bash
curl -i -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Copiez le nouveau token, puis testez :

```bash
curl -i http://localhost:8080/api/admin \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

et :

```bash
curl -i http://localhost:8080/api/client \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

Admin possède les deux rôles, il peut donc accéder aux deux ressources.

---

## 7. À quoi ressemble un JWT ?

Un JWT signé est composé de trois parties séparées par des points :

```text
xxxxx.yyyyy.zzzzz

HEADER.PAYLOAD.SIGNATURE
```

Le payload de notre token contient notamment des informations de ce type :

```json
{
  "sub": "alice",
  "roles": ["ROLE_USER"],
  "iat": 1780000000,
  "exp": 1780000900
}
```

`sub` identifie le sujet, `iat` correspond à la date de création et `exp` à la date d'expiration.

**Important : le payload d'un JWT signé n'est pas chiffré.** Il est encodé et peut être lu. Il ne faut donc pas y mettre un mot de passe ou une donnée secrète simplement parce qu'il s'agit d'un JWT.

La signature sert à vérifier que le jeton n'a pas été modifié et qu'il a été signé avec la clé attendue.

---

## 8. Le login : `AuthenticationManager`

Regardez `AuthController`.

Il reçoit :

```json
{
  "username": "alice",
  "password": "alice123"
}
```

Puis il demande à Spring Security d'authentifier ces credentials :

```java
var authentication = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(
        request.username(),
        request.password()
    )
);
```

À ce stade, le JWT n'existe pas encore.

Le mécanisme est :

```text
username/password
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
utilisateur authentifié
       |
       v
JwtService.generateToken(...)
```

Le JWT est donc une conséquence d'une authentification réussie.

---

## 9. `JwtService`

La classe `JwtService` a deux responsabilités principales dans ce TP :

- créer un token signé ;
- vérifier et lire un token reçu.

À la création :

```java
Jwts.builder()
    .subject(user.getUsername())
    .claim("roles", roles)
    .issuedAt(...)
    .expiration(...)
    .signWith(key)
    .compact();
```

Le token possède une durée de vie de 15 minutes (`900` secondes), définie dans `application.properties`.

---

## 10. Le filtre JWT

Le point central du TP est `JwtAuthenticationFilter`.

Il hérite, comme dans le TP précédent, de :

```java
OncePerRequestFilter
```

Le filtre cherche :

```java
String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
```

Puis vérifie le préfixe :

```java
Bearer 
```

Il retire ce préfixe pour récupérer uniquement le JWT :

```java
String token = authorization.substring(7);
```

Si le token est valide, le filtre construit ensuite une `Authentication` et la place dans le `SecurityContext`.

C'est exactement le fil conducteur du TP 3 :

```text
preuve reçue dans la requête
        |
        v
filtre
        |
        v
Authentication
        |
        v
SecurityContext
        |
        v
autorisation
        |
        v
Controller
```

Seule la preuve est devenue plus riche : nous sommes passés d'une API Key à un JWT signé et expirant.

---

## 11. Pourquoi `STATELESS` ?

Dans `SecurityConfig` :

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

Le serveur ne conserve pas une session d'authentification entre deux requêtes.

```text
requête 1 + JWT valide -> Alice
requête 2 sans JWT     -> anonyme
requête 3 + JWT valide -> Alice
```

Le client doit donc envoyer son Bearer Token à chaque appel protégé.

Contrairement au Form Login du TP 1, on ne s'appuie pas sur `JSESSIONID`.

---

## 12. Que signifie "se déconnecter" avec ce JWT ?

Dans ce TP, le serveur ne stocke pas les tokens émis. Il n'existe donc pas de session serveur à supprimer.

Pour le client, se déconnecter revient principalement à **ne plus conserver et ne plus envoyer le token**.

Un token déjà émis reste techniquement valable jusqu'à son expiration, sauf si l'on ajoute un mécanisme de révocation. Ce sujet est volontairement laissé de côté ici.

C'est une différence importante avec le Form Login.

---

## 13. Tester un token incorrect

Modifiez volontairement un caractère du JWT puis appelez :

```bash
curl -i http://localhost:8080/api/client \
  -H "Authorization: Bearer <TOKEN_MODIFIE>"
```

La signature ne correspond plus. Le filtre ne crée pas d'authentification et Spring Security retourne `401`.

---

## 14. Tester l'expiration

Pour éviter d'attendre 15 minutes, modifiez temporairement :

```properties
security.jwt.expiration-seconds=10
```

Redémarrez l'application, obtenez un nouveau token et appelez immédiatement `/api/client`.

Attendez plus de 10 secondes et réessayez avec exactement le même token.

Vous devez constater qu'il n'est plus accepté.

Remettez ensuite la valeur à `900`.

---

## 15. Comparaison des quatre premiers TP

```text
TP 1 - Form Login
credentials -> login -> session -> cookie JSESSIONID

TP 2 - HTTP Basic
Authorization: Basic login:password -> chaque requête

TP 3 - Filtre personnalisé
X-API-KEY -> filtre -> Authentication

TP 4 - JWT
login/password -> création JWT
Bearer JWT -> filtre -> validation -> Authentication
```

Dans tous les cas, Spring Security finit par travailler avec le même concept central :

```text
Authentication
      |
      v
SecurityContext
      |
      v
autorisation
```

---

## 16. Points volontairement simplifiés

Ce projet est un TP. La clé HMAC est volontairement dans `application.properties` pour qu'elle soit visible et facile à expliquer. En production, un secret de signature ne doit pas être commité dans le dépôt : il doit venir d'une variable d'environnement, d'un gestionnaire de secrets ou d'une infrastructure équivalente.

Nous utilisons également notre propre endpoint `/auth/login` et un filtre JWT afin de rendre la mécanique visible. Pour des architectures OAuth2/OIDC, Spring Security fournit des composants dédiés de Resource Server et de Client : ce sera précisément l'objet du TP suivant.

---

## Exercices

1. Ajouter l'utilisateur `bob` avec `ROLE_USER` et vérifier qu'il peut obtenir son propre JWT.
2. Passer l'expiration à 30 secondes et observer le comportement avant et après expiration.
3. Mettre un point d'arrêt dans `JwtAuthenticationFilter#doFilterInternal`.
4. Observer `SecurityContextHolder.getContext().getAuthentication()` avant et après la validation du token.
5. Modifier un caractère du payload et constater que la signature n'est plus valide.
6. Ajouter une route `/api/me` qui retourne le nom et les authorities de l'utilisateur courant.
7. Expliquer pourquoi le mot de passe ne doit jamais être placé dans le payload JWT.

---

## À retenir

Un JWT ne remplace pas à lui seul toute la sécurité d'une application. Dans ce TP, il sert de preuve présentée après une authentification initiale.

Pour lire une route protégée, le chemin est :

```text
Authorization: Bearer <JWT>
          |
          v
JwtAuthenticationFilter
          |
          v
signature + expiration + utilisateur
          |
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

Le prochain TP permettra de replacer ces notions dans un protocole standard : OAuth2 et OpenID Connect.
