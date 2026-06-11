# TP 10 - Gestion globale des exceptions avec `@ControllerAdvice`, `@RestControllerAdvice` et `ProblemDetail`

## Objectif du TP

Ce TP continue l'application fil rouge de generation de duplicatas d'impots.

Le projet contient deja :

- Spring Boot ;
- Spring MVC REST ;
- Spring MVC + Thymeleaf ;
- Spring Data JPA ;
- H2 ;
- Bean Validation ;
- AOP ;
- OpenAPI / Swagger UI ;
- DevTools.

L'objectif est maintenant de centraliser la gestion des erreurs pour eviter les `try/catch` dans chaque controleur.

Les stagiaires vont apprendre a :

- creer des exceptions fonctionnelles ;
- utiliser `@RestControllerAdvice` pour les API REST ;
- utiliser `@ControllerAdvice` pour les pages MVC Thymeleaf ;
- comprendre la difference entre les deux ;
- retourner une reponse JSON standardisee avec `ProblemDetail` ;
- enrichir un `ProblemDetail` avec des proprietes metier ;
- gerer les erreurs de validation Bean Validation ;
- documenter les erreurs dans Swagger/OpenAPI.

---

## Correction importante Springdoc / Spring Boot 3.3.x

Le projet utilise Spring Boot `3.3.5`.

Avec cette version de Spring Boot, il faut utiliser Springdoc `2.6.0`.

Dans `pom.xml` :

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

Eviter `2.8.x` avec Spring Boot `3.3.x`, car cela peut provoquer au demarrage :

```text
ClassNotFoundException: org.springframework.web.servlet.resource.LiteWebJarsResourceResolver
```

---

## Importer le projet dans Eclipse

1. Dezipper le projet.
2. Ouvrir Eclipse.
3. Aller dans `File > Import...`.
4. Choisir `Maven > Existing Maven Projects`.
5. Selectionner le dossier du projet.
6. Cliquer sur `Finish`.
7. Attendre la fin du telechargement Maven.
8. Faire si necessaire : clic droit sur le projet > `Maven > Update Project`.

---

## Lancer l'application

Depuis Eclipse :

1. ouvrir `com.formation.DuplicataImpotsApplication` ;
2. clic droit ;
3. `Run As > Java Application`.

Depuis un terminal :

```bash
mvn spring-boot:run
```

Application :

```text
http://localhost:8080
```

---

## URLs utiles

Interface Thymeleaf :

```text
http://localhost:8080/ui/duplicatas
```

API REST :

```text
http://localhost:8080/duplicatas
```

Swagger UI :

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON :

```text
http://localhost:8080/v3/api-docs
```

Console H2 :

```text
http://localhost:8080/h2-console
```

Parametres H2 :

```text
Driver Class : org.h2.Driver
JDBC URL     : jdbc:h2:mem:duplicatasdb
User Name    : sa
Password     : laisser vide
```

Requete SQL :

```sql
select * from duplicata;
```

---

# Partie 1 - Constater le probleme avant correction

## Situation de depart

Sans gestion globale, les controleurs ont tendance a contenir ce type de code :

```java
try {
    return service.getById(id);
} catch (IllegalArgumentException exception) {
    // construire une reponse 404 ici
}
```

Problemes :

- duplication du code ;
- melange entre logique web et logique metier ;
- format d'erreur different selon les endpoints ;
- controleurs plus difficiles a lire ;
- gestion MVC HTML et REST JSON confondue.

Dans le corrige, les controleurs ne gerent plus directement les exceptions fonctionnelles.

---

# Partie 2 - Creer des exceptions fonctionnelles

## Exercice

Creer le package :

```text
com.formation.exception
```

Ajouter les exceptions suivantes :

```text
DuplicataNotFoundException
UserNotFoundException
InvalidSearchCriteriaException
```

## Objectif

Remplacer les exceptions trop generiques comme :

```java
throw new IllegalArgumentException(...);
```

par des exceptions metier explicites :

```java
throw new DuplicataNotFoundException(id);
throw new UserNotFoundException(userId);
throw new InvalidSearchCriteriaException("Tri non autorise");
```

## Corrige attendu

Dans `DuplicataService` :

```java
@Transactional(readOnly = true)
public Duplicata getById(String id) {
    return findById(id)
            .orElseThrow(() -> new DuplicataNotFoundException(id));
}
```

Pour la suppression :

```java
public void deleteById(String id) {
    if (!duplicataRepository.existsById(id)) {
        throw new DuplicataNotFoundException(id);
    }
    duplicataRepository.deleteById(id);
}
```

Pour la creation :

```java
User user = userService.findById(userId);
if (user == null) {
    throw new UserNotFoundException(userId);
}
```

---

# Partie 3 - Ajouter `@RestControllerAdvice`

## Role

`@RestControllerAdvice` sert a intercepter les exceptions des controleurs REST et a retourner une reponse HTTP serialisee en JSON.

Il est equivalent a :

```java
@ControllerAdvice
@ResponseBody
```

Dans ce projet, il est limite au package REST :

```java
@RestControllerAdvice(basePackages = "com.formation.web")
public class RestExceptionHandler {
}
```

Cela evite que les pages Thymeleaf soient transformees en JSON en cas d'erreur.

---

## Exercice

Creer :

```text
src/main/java/com/formation/web/RestExceptionHandler.java
```

Ajouter une methode pour gerer un duplicata introuvable :

```java
@ExceptionHandler(DuplicataNotFoundException.class)
public ProblemDetail handleDuplicataNotFound(DuplicataNotFoundException exception,
        HttpServletRequest request) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            exception.getMessage());
    problem.setTitle("Duplicata introuvable");
    problem.setInstance(URI.create(request.getRequestURI()));
    return problem;
}
```

---

# Partie 4 - Utiliser `ProblemDetail`

## Qu'est-ce que `ProblemDetail` ?

`ProblemDetail` est un objet fourni par Spring 6 pour representer une erreur HTTP standardisee.

Il contient notamment :

```text
type
status
title
detail
instance
```

Exemple de reponse REST :

```json
{
  "type": "https://formation.example/problems/duplicata-not-found",
  "title": "Duplicata introuvable",
  "status": 404,
  "detail": "Duplicata introuvable avec l'identifiant : xxx",
  "instance": "/duplicatas/xxx",
  "timestamp": "2026-06-10T10:30:00",
  "duplicataId": "xxx"
}
```

## Pourquoi l'utiliser ?

Avantages :

- format plus standard qu'une classe maison `ApiError` ;
- comprehensible par les clients REST ;
- extensible avec `setProperty` ;
- bien integre a Spring 6 / Spring Boot 3.

Dans le projet, les erreurs REST sont donc retournees avec :

```java
ProblemDetail.forStatusAndDetail(status, detail)
```

puis enrichies avec :

```java
problem.setProperty("timestamp", LocalDateTime.now());
problem.setProperty("errors", errors);
```

---

# Partie 5 - Gerer les erreurs de validation REST

## Exercice

Tester cet appel REST avec un montant invalide :

```bash
curl -X POST http://localhost:8080/duplicatas_dto \
  -H "Content-Type: application/json" \
  -d '{"user_id":"123456789", "montant": 50}'
```

La validation du DTO echoue car `montant` doit etre entre `1000` et `7000`.

## Corrige attendu

Dans `RestExceptionHandler` :

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ProblemDetail handleBodyValidation(MethodArgumentNotValidException exception,
        HttpServletRequest request) {
    List<String> errors = exception.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
            .toList();

    ProblemDetail problem = creerProblemDetail(HttpStatus.BAD_REQUEST,
            "Erreur de validation du corps JSON",
            "Le corps de la requete contient des donnees invalides.",
            request,
            "body-validation-error");
    problem.setProperty("errors", errors);
    return problem;
}
```

---

# Partie 6 - Gerer les erreurs de validation de parametres

## Exemple

Tester :

```text
http://localhost:8080/duplicatas?user_id=&montant=50
```

ou :

```bash
curl -X POST "http://localhost:8080/duplicatas?user_id=&montant=50"
```

Les annotations suivantes declenchent une erreur :

```java
@RequestParam("user_id") @NotBlank String userId,
@RequestParam @Min(1000) @Max(7000) Integer montant
```

Le corrige gere :

```java
ConstraintViolationException
HandlerMethodValidationException
```

---

# Partie 7 - Ajouter `@ControllerAdvice` pour Thymeleaf

## Difference avec `@RestControllerAdvice`

| Annotation | Usage | Type de reponse |
|---|---|---|
| `@ControllerAdvice` | MVC avec vues Thymeleaf/JSP | page HTML |
| `@RestControllerAdvice` | API REST | JSON/XML |

Dans le projet :

```java
@ControllerAdvice(basePackages = "com.formation.mvc")
public class MvcExceptionHandler {
}
```

Ce handler retourne une vue :

```java
return new ModelAndView("error/functional-error");
```

La page est :

```text
src/main/resources/templates/error/functional-error.html
```

---

## Exercice

Tester une erreur MVC :

```text
http://localhost:8080/ui/duplicatas/inconnu
```

Resultat attendu : une page HTML d'erreur fonctionnelle, pas un JSON.

Tester une erreur REST :

```text
http://localhost:8080/duplicatas/inconnu
```

Resultat attendu : une reponse JSON `ProblemDetail`.

Cette comparaison est le coeur du TP.

---

# Partie 8 - Ajouter un cas d'erreur sur pagination/tri

## Objectif

Montrer qu'une exception globale n'est pas reservee aux erreurs 404.

Dans `DuplicataService`, le corrige controle les criteres de pagination :

```java
private static final Set<String> SORT_PROPERTIES_AUTORISEES = Set.of("createdAt", "montant", "userId", "id");
```

Si le tri est invalide :

```java
throw new InvalidSearchCriteriaException("Tri non autorise : " + sortProperty);
```

## Test

```text
http://localhost:8080/duplicatas/page?sort=champInexistant
```

Resultat attendu : `400 Bad Request` avec un `ProblemDetail`.

---

# Partie 9 - Mettre a jour Swagger/OpenAPI

Les annotations REST utilisent maintenant `ProblemDetail` comme schema d'erreur :

```java
@ApiResponse(
    responseCode = "404",
    description = "Duplicata introuvable",
    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
)
```

Dans Swagger UI, ouvrir :

```text
http://localhost:8080/swagger-ui.html
```

Puis verifier les reponses documentees sur :

```text
GET /duplicatas/{id}
POST /duplicatas_dto
DELETE /duplicatas/{id}
GET /duplicatas/page
```

---

# Tests manuels recommandes

## 1. REST - Duplicata introuvable

```bash
curl http://localhost:8080/duplicatas/inconnu
```

Attendu : `404` avec JSON `ProblemDetail`.

## 2. REST - Utilisateur introuvable

```bash
curl -X POST "http://localhost:8080/duplicatas?user_id=999999999&montant=2500"
```

Attendu : `404` avec JSON `ProblemDetail` contenant `userId`.

## 3. REST - Validation JSON

```bash
curl -X POST http://localhost:8080/duplicatas_dto \
  -H "Content-Type: application/json" \
  -d '{"user_id":"", "montant": 50}'
```

Attendu : `400` avec une propriete `errors`.

## 4. REST - Tri invalide

```bash
curl "http://localhost:8080/duplicatas/page?sort=champInexistant"
```

Attendu : `400`.

## 5. MVC - Page d'erreur HTML

```text
http://localhost:8080/ui/duplicatas/inconnu
```

Attendu : page Thymeleaf `error/functional-error.html`.

---

# Ce que Spring Boot fait pour nous ici

Spring Boot configure automatiquement :

- le serveur Tomcat embarque ;
- Jackson pour serialiser `ProblemDetail` en JSON ;
- la validation Bean Validation ;
- le mapping des controleurs ;
- la resolution des vues Thymeleaf ;
- la gestion des erreurs par defaut ;
- H2 Console ;
- JPA/Hibernate ;
- Swagger UI via Springdoc quand la dependance est presente.

Sans Spring Boot, il faudrait configurer manuellement beaucoup de ces elements.

---

# Bilan pedagogique

A la fin du TP, les stagiaires doivent savoir expliquer :

- pourquoi il faut eviter les `try/catch` dans les controleurs ;
- quand utiliser `@ControllerAdvice` ;
- quand utiliser `@RestControllerAdvice` ;
- comment fonctionne `@ExceptionHandler` ;
- pourquoi `ProblemDetail` est preferable a une classe d'erreur maison ;
- comment gerer les erreurs de validation ;
- comment tester une erreur REST et une erreur MVC.
