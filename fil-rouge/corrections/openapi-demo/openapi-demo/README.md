# Démonstration OpenAPI avec Spring Boot

Ce projet sert de support à la partie **OpenAPI** de la formation *Spring - développer des applications d'entreprise*. Il reste volontairement petit : l'objectif n'est pas de refaire l'application Spring Data, mais de voir comment décrire proprement une API REST existante et comment rendre cette description consultable et testable.

## 1. OpenAPI, Swagger et springdoc : ne pas les confondre

**OpenAPI** est une spécification : elle définit un format standard pour décrire une API HTTP (routes, paramètres, corps de requête, réponses, erreurs, schémas de données, sécurité...). La description peut être représentée en JSON ou en YAML.

**Swagger** désigne aujourd'hui surtout un ensemble d'outils autour de cette spécification. Dans ce projet, l'outil visible est **Swagger UI** : il lit le document OpenAPI et construit une page web permettant de consulter et d'essayer l'API.

**springdoc-openapi** est la bibliothèque qui fait le lien avec Spring Boot. Elle analyse les contrôleurs Spring MVC, les DTO, la validation et les annotations OpenAPI pour produire le document OpenAPI à l'exécution.

En résumé :

```text
Contrôleurs Spring MVC
        +
annotations OpenAPI
        |
        v
 springdoc-openapi
        |
        +----> /v3/api-docs       document OpenAPI JSON
        +----> /v3/api-docs.yaml  document OpenAPI YAML
        |
        v
    Swagger UI
```

OpenAPI n'est donc pas Swagger UI. Le document OpenAPI peut être utilisé sans interface graphique : génération de clients, tests, documentation, API Gateway, outils de gouvernance, etc.

## 2. Import dans Eclipse

Prérequis : Java 17 et Maven.

1. Décompresser le projet.
2. Dans Eclipse : **File > Import > Maven > Existing Maven Projects**.
3. Sélectionner le dossier `openapi-demo`.
4. Vérifier que le JDK du projet est Java 17 ou supérieur.
5. Lancer `OpenApiDemoApplication` avec **Run As > Java Application** ou **Spring Boot App** si Spring Tools est installé.

L'application écoute sur le port 8080.

## 3. Les URL à connaître

Après démarrage :

- Swagger UI : `http://localhost:8080/swagger-ui.html`
- description OpenAPI JSON : `http://localhost:8080/v3/api-docs`
- description OpenAPI YAML : `http://localhost:8080/v3/api-docs.yaml`

Commencer par ouvrir `/v3/api-docs`. C'est **le contrat OpenAPI**. Swagger UI n'est qu'une représentation de ce contrat.

## 4. La dépendance Maven

Le projet utilise :

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.9.1</version>
</dependency>
```

Cette version est destinée à Spring Boot 3. La dépendance apporte la génération du document OpenAPI ainsi que Swagger UI.

Une première manipulation consiste à lancer le projet **sans ajouter aucune annotation OpenAPI** dans un nouveau contrôleur. springdoc découvre déjà les `@RestController`, `@GetMapping`, `@PostMapping`, `@PathVariable`, `@RequestParam`, etc. Les annotations OpenAPI servent ensuite à enrichir ce qui ne peut pas être déduit correctement du code.

## 5. Le domaine de la démonstration

L'API manipule des employés en mémoire afin de rester concentrée sur OpenAPI. Il n'y a volontairement ni JPA ni base de données.

Les principales routes sont :

```text
GET    /api/employees
GET    /api/employees/{id}
POST   /api/employees
PUT    /api/employees/{id}
DELETE /api/employees/{id}
```

Dans Swagger UI, chaque méthode HTTP devient une **operation** OpenAPI.

## 6. `@Tag` : regrouper les opérations

Dans `EmployeeController` :

```java
@Tag(
    name = "Employés",
    description = "Consultation et gestion des employés"
)
```

Un tag sert à organiser la documentation. Dans une application plus importante, on pourrait avoir les tags `Employés`, `Commandes`, `Factures`, `Clients`, etc.

## 7. `@Operation` : documenter une opération

Exemple :

```java
@GetMapping
@Operation(
    summary = "Lister les employés",
    description = "Le département est un filtre facultatif."
)
```

`summary` doit rester court. `description` apporte les précisions utiles au consommateur de l'API. Il ne faut pas recopier le nom de la méthode Java : la documentation décrit le contrat HTTP, pas l'implémentation interne.

## 8. `@Parameter` : documenter les paramètres

La liste accepte un filtre facultatif :

```java
@Parameter(
    description = "Département à filtrer",
    example = "Informatique"
)
@RequestParam(required = false) String department
```

Tester dans Swagger UI :

```text
GET /api/employees?department=Informatique
```

Le même principe s'applique aux `@PathVariable`, headers et autres paramètres HTTP.

## 9. Les schémas : `@Schema`

`EmployeeRequest`, `EmployeeResponse` et `ApiError` sont des DTO. springdoc sait déjà en déduire leur structure. `@Schema` permet d'améliorer le contrat : description, exemple, format, contraintes particulières, etc.

```java
@Schema(description = "Employé retourné par l'API")
public record EmployeeResponse(...)
```

Dans Swagger UI, regarder la section **Schemas**. OpenAPI décrit les données indépendamment des classes Java qui ont servi à produire la description.

## 10. Validation Bean Validation et documentation

`EmployeeRequest` utilise :

```java
@NotBlank
@Email
```

springdoc exploite aussi les contraintes de Bean Validation pour enrichir le schéma. C'est intéressant car une contrainte utile à l'exécution contribue également à décrire le contrat.

Tester un POST valide :

```json
{
  "firstName": "Claire",
  "lastName": "Durand",
  "email": "claire.durand@entreprise.fr",
  "department": "RH"
}
```

Puis refaire le test avec un email incorrect ou un nom vide.

## 11. `@ApiResponse` et `@ApiResponses`

Une API n'a pas seulement une réponse `200`.

Pour `GET /api/employees/{id}`, le contrôleur documente notamment :

```java
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Employé trouvé"),
    @ApiResponse(
        responseCode = "404",
        description = "Employé introuvable",
        content = @Content(
            schema = @Schema(implementation = ApiError.class)
        )
    )
})
```

Tester `/api/employees/99`. L'intérêt est de documenter non seulement le code HTTP mais aussi **la forme du corps retourné en erreur**.

## 12. Configuration générale de l'API

`OpenApiConfig` déclare un bean `OpenAPI` :

```java
@Bean
OpenAPI formationOpenAPI() {
    return new OpenAPI().info(
        new Info()
            .title("API Gestion des employés")
            .version("1.0")
    );
}
```

Cette configuration concerne l'API dans son ensemble : titre, version, description, contact, licence, serveurs, sécurité globale, etc. Les annotations du contrôleur décrivent plutôt les opérations particulières.

## 13. Ce qui est automatique et ce qui mérite d'être explicite

springdoc peut déduire beaucoup d'informations à partir de Spring MVC. Il n'est donc pas utile de couvrir chaque ligne de code avec des annotations.

Une bonne règle de travail est : laisser springdoc déduire les informations évidentes et documenter explicitement ce qui fait partie du contrat métier : but de l'opération, paramètres ambigus, exemples utiles, codes d'erreur, règles particulières et sécurité.

Trop d'annotations rendent la documentation difficile à maintenir. Pas assez d'informations donne un contrat techniquement valide mais peu utile.

## 14. Approche code-first et design-first

Ce projet utilise une approche **code-first** :

```text
code Spring -> génération du document OpenAPI
```

Une autre approche est **design-first** : l'équipe écrit d'abord `openapi.yaml`, le fait relire et valider, puis développe l'API à partir de ce contrat. Cette approche est fréquente lorsque plusieurs équipes doivent se mettre d'accord sur une API avant son implémentation.

OpenAPI fonctionne avec les deux approches. springdoc est particulièrement pratique pour l'approche code-first avec Spring Boot.

## 15. Manipulations

### Manipulation A - voir ce que Springdoc découvre seul

Retirer temporairement `@Operation` d'une méthode, redémarrer et comparer Swagger UI. La route reste documentée : Spring MVC fournit déjà beaucoup d'informations.

### Manipulation B - observer le vrai document OpenAPI

Ouvrir `/v3/api-docs`, rechercher `/api/employees/{id}` et retrouver `parameters`, `responses` et les références vers les `schemas`.

### Manipulation C - documenter une erreur

Retirer temporairement le `@ApiResponse` 404 de `findById`. L'exception fonctionne toujours à l'exécution, mais le contrat ne décrit plus correctement ce cas. Cela montre la différence entre **faire fonctionner une API** et **documenter son contrat**.

### Manipulation D - ajouter une opération

Ajouter :

```text
GET /api/employees/count
```

Puis documenter son résumé et sa réponse. Vérifier qu'elle apparaît dans `/v3/api-docs` et Swagger UI.

## 16. À retenir

OpenAPI est le **contrat standardisé** de l'API. Swagger UI est un **outil qui sait afficher et exploiter ce contrat**. springdoc-openapi observe l'application Spring Boot et produit le document OpenAPI.

Dans un projet d'entreprise, la documentation doit rester synchronisée avec l'API. Le but n'est pas d'avoir une jolie page Swagger, mais un contrat suffisamment précis pour qu'une autre équipe puisse comprendre et consommer l'API sans lire son code source.
