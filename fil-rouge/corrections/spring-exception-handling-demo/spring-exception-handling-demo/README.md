# TP - Gestion globale des exceptions avec Spring Boot

Ce projet sert de support à la partie consacrée à la gestion des erreurs dans une API REST Spring Boot. Le domaine est volontairement simple : une petite API de gestion de clients. L'objectif n'est pas de travailler la persistance mais de voir où placer les exceptions et comment construire des réponses HTTP cohérentes.

## Prérequis

- Java 17
- Maven 3.9 ou Maven intégré à Eclipse
- Eclipse avec m2e / Spring Tools

## Import dans Eclipse

1. Décompresser le projet.
2. `File > Import > Maven > Existing Maven Projects`.
3. Sélectionner le dossier `spring-exception-handling-demo`.
4. Lancer `ExceptionHandlingApplication` avec `Run As > Java Application` ou `Spring Boot App` si Spring Tools est installé.

L'application démarre sur `http://localhost:8080`.

## 1. Le problème que l'on veut résoudre

Sans traitement global, chaque contrôleur finit souvent par contenir des `try/catch`, avec des formats de réponse différents selon les méthodes. Dans une API d'entreprise, on préfère que le contrôleur reste centré sur HTTP et délègue le traitement des erreurs à un composant commun.

Le projet utilise :

- des exceptions métier (`CustomerNotFoundException`, `EmailAlreadyUsedException`) ;
- `@RestControllerAdvice` pour centraliser leur traitement ;
- `@ExceptionHandler` pour associer une exception à une réponse HTTP ;
- Bean Validation et `@Valid` pour les erreurs de saisie ;
- un objet `ApiError` pour conserver le même format d'erreur partout.

## 2. Tester un appel qui fonctionne

```bash
curl http://localhost:8080/api/customers/1
```

Réponse attendue : HTTP 200 avec le client Sophie Martin.

La liste complète est disponible avec :

```bash
curl http://localhost:8080/api/customers
```

## 3. Ressource inexistante : 404

```bash
curl -i http://localhost:8080/api/customers/99
```

`CustomerService` ne retourne pas `null`. Il lève une exception métier :

```java
.orElseThrow(() -> new CustomerNotFoundException(id));
```

Le contrôleur ne contient aucun `try/catch`. L'exception remonte jusqu'au `GlobalExceptionHandler` :

```java
@ExceptionHandler(CustomerNotFoundException.class)
public ResponseEntity<ApiError> handleCustomerNotFound(...) {
    ...
}
```

La réponse est un HTTP 404 avec un corps JSON de la forme :

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "Aucun client trouvé avec l'identifiant 99",
  "path": "/api/customers/99",
  "validationErrors": {}
}
```

## 4. Règle métier : 409 Conflict

Essayons de créer un client avec une adresse déjà utilisée :

```bash
curl -i -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Autre Sophie","email":"sophie.martin@entreprise.fr"}'
```

Le service lève `EmailAlreadyUsedException`. Le handler transforme cette exception en HTTP 409 `Conflict`.

C'est un point important : une exception Java ne dicte pas à elle seule le statut HTTP. C'est la couche Web qui décide de la traduction adaptée à l'API.

## 5. Validation des données : 400

`CreateCustomerRequest` utilise Bean Validation :

```java
public record CreateCustomerRequest(
    @NotBlank(message = "Le nom est obligatoire") String name,
    @NotBlank(message = "L'adresse e-mail est obligatoire")
    @Email(message = "L'adresse e-mail n'est pas valide") String email) {}
```

Le contrôleur active la validation avec `@Valid` :

```java
public ResponseEntity<Customer> create(
        @Valid @RequestBody CreateCustomerRequest request)
```

Test :

```bash
curl -i -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"","email":"adresse-invalide"}'
```

Spring lève `MethodArgumentNotValidException`. Le handler récupère les erreurs de champs et retourne HTTP 400 avec un résultat lisible :

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Les données envoyées ne sont pas valides",
  "path": "/api/customers",
  "validationErrors": {
    "name": "Le nom est obligatoire",
    "email": "L'adresse e-mail n'est pas valide"
  }
}
```

## 6. Pourquoi `@RestControllerAdvice` ?

`@RestControllerAdvice` s'applique aux contrôleurs REST de l'application. Il combine le principe de `@ControllerAdvice` avec une réponse sérialisée dans le corps HTTP.

Le chemin à retenir est :

```text
requête HTTP
    |
Controller
    |
Service
    |
exception
    |
@RestControllerAdvice
    |
@ExceptionHandler adapté
    |
ResponseEntity<ApiError>
    |
réponse HTTP
```

Le contrôleur reste donc simple :

```java
@GetMapping("/{id}")
public Customer findById(@PathVariable Long id) {
    return customerService.findById(id);
}
```

## 7. Le handler générique

Le dernier handler intercepte les erreurs non prévues :

```java
@ExceptionHandler(Exception.class)
```

Il retourne un HTTP 500 sans exposer au client une stack trace ou des détails techniques internes. Les erreurs connues doivent avoir des handlers plus précis ; le handler générique sert de filet de sécurité.

Dans une application réelle, c'est également à cet endroit que l'on journaliserait l'erreur technique avec SLF4J/Logback, avec un identifiant de corrélation si l'application en utilise un.

## 8. Exercices proposés

1. Ajouter `InvalidCustomerStatusException` et la traduire en HTTP 400.
2. Ajouter un endpoint `DELETE /api/customers/{id}` et retourner 404 si le client n'existe pas.
3. Ajouter une contrainte `@Size(min = 3)` sur le nom et vérifier le JSON retourné.
4. Ajouter un champ `code` dans `ApiError` pour fournir un code stable au front, par exemple `CUSTOMER_NOT_FOUND`.
5. Remplacer progressivement `ApiError` par `ProblemDetail`, le format d'erreur HTTP pris en charge par les versions modernes de Spring Framework.

## À retenir

- Le service lève les exceptions correspondant aux situations métier.
- Le contrôleur n'a normalement pas à répéter des `try/catch` pour chaque endpoint.
- `@RestControllerAdvice` centralise la traduction des exceptions vers HTTP.
- `@ExceptionHandler` permet de choisir le traitement selon le type d'exception.
- `@Valid` et Bean Validation traitent les erreurs de saisie avant l'exécution normale du traitement.
- Le format des erreurs doit rester stable pour les consommateurs de l'API.
