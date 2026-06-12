# TP 14 — Sécurisation des endpoints REST avec Spring Security

## Objectif du TP

Ce TP part du corrigé précédent `13_fil_rouge_security_front`.

Dans le TP précédent, seule la partie **front MVC / Thymeleaf** était protégée. Les routes REST étaient volontairement laissées publiques.

Dans ce TP, on sécurise maintenant les endpoints REST de l'application de génération de duplicatas d'impôts.

L'objectif est de comprendre :

- comment protéger une API REST avec Spring Security ;
- comment séparer la sécurité MVC et la sécurité REST ;
- pourquoi une API REST ne doit pas rediriger vers une page de login HTML ;
- comment renvoyer des erreurs JSON propres en `401` et `403` ;
- comment gérer les rôles `USER` et `ADMIN` ;
- pourquoi on désactive généralement CSRF pour une API REST stateless ;
- comment utiliser CORS avec une API REST ;
- comment tester l'API avec `curl`, Postman ou Swagger UI.

Dans ce TP, on utilise volontairement **HTTP Basic** pour rester simple. Le prochain TP pourra remplacer Basic Auth par JWT.

---

## Règles de sécurité retenues

### Comptes disponibles

| Login | Mot de passe | Rôles |
|---|---|---|
| `user` | `user` | `ROLE_USER` |
| `admin` | `admin` | `ROLE_USER`, `ROLE_ADMIN` |

### Routes REST protégées

| Route | Méthode | Rôle attendu |
|---|---:|---|
| `/duplicatas` | `GET` | `USER` ou `ADMIN` |
| `/duplicatas/{id}` | `GET` | `USER` ou `ADMIN` |
| `/duplicatas/by-user/{userId}` | `GET` | `USER` ou `ADMIN` |
| `/duplicatas/by-montant` | `GET` | `USER` ou `ADMIN` |
| `/duplicatas/search` | `GET` | `USER` ou `ADMIN` |
| `/duplicatas/jpql` | `GET` | `USER` ou `ADMIN` |
| `/duplicatas/projections` | `GET` | `USER` ou `ADMIN` |
| `/duplicatas/page` | `GET` | `USER` ou `ADMIN` |
| `/duplicatas` | `POST` | `ADMIN` |
| `/duplicatas/{userId}/{montant}` | `POST` | `ADMIN` |
| `/duplicatas_dto` | `POST` | `ADMIN` |
| `/duplicatas/{id}` | `DELETE` | `ADMIN` |
| `/api/cache/**` | `GET`, `DELETE` | `ADMIN` |

### Routes front MVC

Les routes `/ui/**` restent protégées comme dans le TP précédent avec formulaire de connexion HTML.

---

## Pourquoi deux `SecurityFilterChain` ?

Le projet contient maintenant deux chaînes de sécurité dans `SecurityConfig`.

### 1. Chaîne REST

```java
@Bean
@Order(1)
SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, ...) throws Exception {
    return http
            .securityMatcher("/duplicatas", "/duplicatas/**", "/duplicatas_dto", "/duplicatas_dto/**", "/api/cache", "/api/cache/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(...)
            .build();
}
```

Cette chaîne ne s'applique qu'aux endpoints REST.

Elle utilise :

- `securityMatcher(...)` pour dire quelles routes sont concernées ;
- `httpBasic(...)` pour authentifier simplement les appels REST ;
- `SessionCreationPolicy.STATELESS` pour ne pas dépendre d'une session HTTP ;
- `csrf().disable()` car l'API REST est appelée avec un header `Authorization` ;
- des réponses JSON `ProblemDetail` en cas d'erreur.

### 2. Chaîne MVC

```java
@Bean
@Order(2)
SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
            .formLogin(...)
            .logout(...)
            .csrf(...)
            .authorizeHttpRequests(...)
            .build();
}
```

Cette chaîne concerne les pages HTML Thymeleaf.

Elle garde :

- le formulaire de login ;
- les sessions HTTP ;
- la protection CSRF des formulaires ;
- la page `/access-denied` en cas d'accès refusé.

### Point pédagogique important

Une application peut avoir plusieurs `SecurityFilterChain`.

C'est très pratique quand une même application expose à la fois :

- un front MVC HTML ;
- une API REST ;
- des endpoints techniques ;
- des endpoints Actuator.

---

## Différence entre authentification et autorisation

### Authentification

L'authentification répond à la question :

```text
Qui êtes-vous ?
```

Exemple :

```bash
curl -u user:user http://localhost:8080/duplicatas
```

Ici, l'utilisateur est authentifié.

### Autorisation

L'autorisation répond à la question :

```text
Avez-vous le droit de faire cette action ?
```

Exemple :

```bash
curl -u user:user -X DELETE http://localhost:8080/duplicatas/dup-demo-001
```

L'utilisateur `user` est authentifié, mais il n'a pas le rôle `ADMIN`.

Résultat attendu :

```text
403 Forbidden
```

---

## Réponses d'erreur REST avec ProblemDetail

Pour les endpoints REST, le projet ne redirige pas vers `/login`.

Il renvoie des erreurs JSON au format `application/problem+json`.

### Sans authentification

```bash
curl -i http://localhost:8080/duplicatas
```

Résultat attendu :

```text
HTTP/1.1 401 Unauthorized
Content-Type: application/problem+json
```

Exemple de corps JSON :

```json
{
  "type": "https://formation.spring/erreurs/securite",
  "title": "Authentification requise",
  "status": 401,
  "detail": "Vous devez fournir un identifiant et un mot de passe valides pour appeler cette API REST.",
  "instance": "/duplicatas",
  "method": "GET",
  "path": "/duplicatas"
}
```

### Authentifié mais non autorisé

```bash
curl -i -u user:user -X DELETE http://localhost:8080/duplicatas/dup-demo-001
```

Résultat attendu :

```text
HTTP/1.1 403 Forbidden
Content-Type: application/problem+json
```

Exemple de corps JSON :

```json
{
  "type": "https://formation.spring/erreurs/securite",
  "title": "Acces refuse",
  "status": 403,
  "detail": "Votre compte est authentifie, mais il ne possede pas les droits suffisants pour cette operation.",
  "instance": "/duplicatas/dup-demo-001",
  "method": "DELETE",
  "path": "/duplicatas/dup-demo-001"
}
```

---

## Exercice 1 — Ajouter la chaîne de sécurité REST

### À faire par les stagiaires

Dans `SecurityConfig`, ajouter une nouvelle méthode :

```java
@Bean
@Order(1)
SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, ...) throws Exception {
    // configuration REST
}
```

Puis limiter cette chaîne aux routes REST :

```java
.securityMatcher("/duplicatas", "/duplicatas/**", "/duplicatas_dto", "/duplicatas_dto/**", "/api/cache", "/api/cache/**")
```

### Pourquoi `@Order(1)` ?

Spring Security teste les chaînes dans l'ordre.

La chaîne REST doit passer avant la chaîne MVC, sinon une requête REST non authentifiée pourrait être traitée comme une requête HTML et redirigée vers `/login`.

---

## Exercice 2 — Activer HTTP Basic pour les endpoints REST

Dans la chaîne REST :

```java
.httpBasic(Customizer.withDefaults())
.formLogin(AbstractHttpConfigurer::disable)
.logout(AbstractHttpConfigurer::disable)
```

### Pourquoi désactiver `formLogin` ?

Une API REST ne doit pas renvoyer une page HTML de login.

Elle doit renvoyer :

```text
401 Unauthorized
```

Le client REST décide ensuite comment s'authentifier.

---

## Exercice 3 — Rendre l'API stateless

Dans la chaîne REST :

```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

### Explication

Une API REST est généralement stateless :

- chaque requête contient ses informations d'authentification ;
- le serveur ne dépend pas d'une session HTTP ;
- cela prépare le passage à JWT.

---

## Exercice 4 — Désactiver CSRF pour REST

Dans la chaîne REST :

```java
.csrf(AbstractHttpConfigurer::disable)
```

### Pourquoi ?

CSRF protège surtout les applications web utilisant des cookies de session automatiquement envoyés par le navigateur.

Dans une API REST appelée avec :

```text
Authorization: Basic ...
```

ou plus tard :

```text
Authorization: Bearer ...
```

le risque CSRF est différent.

On désactive donc CSRF pour les endpoints REST stateless.

### Attention pédagogique

On ne désactive pas CSRF globalement.

La chaîne MVC garde CSRF activé pour les formulaires Thymeleaf.

---

## Exercice 5 — Ajouter les autorisations REST

Dans le corrigé :

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.GET, "/duplicatas", "/duplicatas/**").hasAnyRole("USER", "ADMIN")
    .requestMatchers(HttpMethod.POST, "/duplicatas", "/duplicatas/**", "/duplicatas_dto").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/duplicatas/**").hasRole("ADMIN")
    .requestMatchers("/api/cache/**").hasRole("ADMIN")
    .anyRequest().authenticated())
```

### À tester

```bash
curl -i -u user:user http://localhost:8080/duplicatas
```

Résultat attendu : `200 OK`.

```bash
curl -i -u user:user -X POST "http://localhost:8080/duplicatas?user_id=123456789&montant=2500"
```

Résultat attendu : `403 Forbidden`.

```bash
curl -i -u admin:admin -X POST "http://localhost:8080/duplicatas?user_id=123456789&montant=2500"
```

Résultat attendu : `200 OK`.

---

## Exercice 6 — Tester la création avec JSON

```bash
curl -i -u admin:admin \
  -H "Content-Type: application/json" \
  -X POST http://localhost:8080/duplicatas_dto \
  -d '{"user_id":"123456789","montant":2500}'
```

Résultat attendu : `200 OK` avec le duplicata créé.

Avec un utilisateur non admin :

```bash
curl -i -u user:user \
  -H "Content-Type: application/json" \
  -X POST http://localhost:8080/duplicatas_dto \
  -d '{"user_id":"123456789","montant":2500}'
```

Résultat attendu : `403 Forbidden`.

---

## Exercice 7 — Sécuriser les endpoints cache

Les endpoints du TP cache sont maintenant protégés :

```text
/api/cache/** → ADMIN uniquement
```

Tester :

```bash
curl -i -u user:user http://localhost:8080/api/cache
```

Résultat attendu : `403 Forbidden`.

Puis :

```bash
curl -i -u admin:admin http://localhost:8080/api/cache
```

Résultat attendu : `200 OK`.

Vider tous les caches :

```bash
curl -i -u admin:admin -X DELETE http://localhost:8080/api/cache
```

Résultat attendu : `204 No Content`.

---

## Exercice 8 — Tester avec Swagger UI

Swagger UI reste accessible sans authentification :

```text
http://localhost:8080/swagger-ui.html
```

Mais les appels aux endpoints REST nécessitent maintenant une authentification.

Dans Swagger UI :

1. Cliquer sur le bouton `Authorize`.
2. Saisir :

```text
username : admin
password : admin
```

3. Valider.
4. Tester un endpoint REST.

Le fichier `OpenApiConfig` déclare un schéma de sécurité Basic Auth :

```java
.components(new Components()
    .addSecuritySchemes("basicAuth", new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("basic")))
.addSecurityItem(new SecurityRequirement().addList("basicAuth"))
```

---

## Exercice 9 — Comprendre CORS

Le projet contient une configuration CORS :

```java
@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:5500"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### Message pédagogique

CORS n'est pas une sécurité d'authentification.

CORS sert à contrôler quels sites web, exécutés dans un navigateur, ont le droit d'appeler l'API depuis une autre origine.

Exemple :

```text
Frontend : http://localhost:3000
Backend  : http://localhost:8080
```

Comme les deux origines sont différentes, le navigateur applique la politique CORS.

---

## Exercice 10 — Vérifier que le front MVC fonctionne toujours

Ouvrir :

```text
http://localhost:8080/ui/duplicatas
```

Le comportement attendu est identique au TP précédent :

- redirection vers `/login` si non connecté ;
- `user/user` peut consulter ;
- `admin/admin` peut créer et supprimer ;
- CSRF reste actif sur les formulaires HTML.

---

## Accès H2

La console H2 reste accessible pour le TP :

```text
http://localhost:8080/h2-console
```

Paramètres :

```text
JDBC URL : jdbc:h2:mem:duplicatasdb
User     : sa
Password : 
```

---

## Lancer le projet

Depuis Eclipse :

```text
Clic droit sur DuplicataImpotsApplication
Run As > Java Application
```

Avec Maven :

```bash
mvn spring-boot:run
```

---

## Résumé pédagogique

À la fin de ce TP, les stagiaires doivent avoir compris que :

- la sécurité d'un front MVC et celle d'une API REST n'ont pas les mêmes besoins ;
- un front MVC utilise souvent une session, un formulaire de login et CSRF ;
- une API REST utilise plutôt une authentification par header et un mode stateless ;
- une API REST doit renvoyer des statuts HTTP et du JSON, pas une page HTML ;
- les rôles permettent de distinguer lecture et administration ;
- CORS ne remplace jamais l'authentification ;
- cette configuration prépare naturellement le prochain TP sur JWT.
