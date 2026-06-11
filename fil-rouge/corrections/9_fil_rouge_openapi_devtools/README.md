# TP 9 - Documenter les services REST avec OpenAPI/Swagger et utiliser Spring Boot DevTools

## Objectif pédagogique

Dans le TP précédent, l'application fil rouge de génération de duplicatas d'impôts utilisait :

- Spring Boot ;
- Spring MVC REST ;
- Thymeleaf ;
- Spring Data JPA ;
- H2 ;
- Bean Validation ;
- AOP.

Ce TP ajoute deux sujets utiles au quotidien :

1. **OpenAPI / Swagger UI** pour documenter et tester les services REST ;
2. **Spring Boot DevTools** pour améliorer le confort de développement grâce au redémarrage automatique.

L'objectif est aussi de montrer aux stagiaires que Spring Boot automatise beaucoup de choses, mais qu'il reste important de comprendre ce qui est ajouté dans le projet.

---

## Fonctionnalités disponibles dans le corrigé

L'application contient :

- API REST de création, consultation, recherche et suppression de duplicatas ;
- documentation Swagger UI ;
- génération automatique du contrat OpenAPI JSON ;
- personnalisation du titre, de la description et de la version de l'API ;
- exemples de réponses `200`, `204`, `400`, `404` ;
- documentation du DTO d'entrée ;
- documentation du modèle de sortie ;
- gestion d'erreurs REST simple avec `@RestControllerAdvice` ;
- Spring Boot DevTools activé ;
- H2 Console ;
- interface Thymeleaf conservée.

---

## Import du projet dans Eclipse

1. Dézipper le projet.
2. Ouvrir Eclipse.
3. Aller dans `File > Import...`.
4. Choisir `Maven > Existing Maven Projects`.
5. Sélectionner le dossier du projet.
6. Cliquer sur `Finish`.
7. Attendre la fin du téléchargement des dépendances Maven.

---

## Lancer l'application

Depuis Eclipse :

1. ouvrir la classe `DuplicataImpotsApplication` ;
2. clic droit ;
3. `Run As > Java Application`.

Depuis un terminal :

```bash
mvn spring-boot:run
```

L'application démarre sur :

```text
http://localhost:8080
```

---

## URLs utiles

Interface web Thymeleaf :

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

Contrat OpenAPI JSON :

```text
http://localhost:8080/v3/api-docs
```

Console H2 :

```text
http://localhost:8080/h2-console
```

Paramètres H2 :

```text
Driver Class : org.h2.Driver
JDBC URL     : jdbc:h2:mem:duplicatasdb
User Name    : sa
Password     : laisser vide
```

Requête SQL de test :

```sql
select * from duplicata;
```

---

# Partie 1 - Ajouter OpenAPI / Swagger

## Exercice 1 - Ajouter la dépendance Maven

Dans `pom.xml`, ajouter :

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.17</version>
</dependency>
```

Cette dépendance ajoute :

- la génération du document OpenAPI ;
- Swagger UI ;
- les annotations `@Operation`, `@ApiResponse`, `@Schema`, `@Tag`, etc.

Après ajout de la dépendance, relancer l'application et ouvrir :

```text
http://localhost:8080/swagger-ui.html
```

À ce stade, Swagger affiche déjà les endpoints, même sans annotation.

---

## Exercice 2 - Ajouter une configuration OpenAPI

Créer la classe :

```text
src/main/java/com/formation/config/OpenApiConfig.java
```

Objectif : personnaliser les informations générales de la documentation.

Le corrigé contient :

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI duplicataOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Serveur local de formation"))
                .info(new Info()
                        .title("API Duplicatas d'impôts")
                        .version("1.0.0")
                        .description("Documentation OpenAPI de l'application fil rouge de génération de duplicatas d'impôts."));
    }
}
```

À vérifier dans Swagger UI :

- le titre de l'API ;
- la version ;
- la description ;
- le serveur local.

---

## Exercice 3 - Documenter le contrôleur REST

Dans `DuplicataControlleur`, ajouter :

```java
@Tag(name = "Duplicatas", description = "Services REST de génération et consultation des duplicatas d'impôts")
```

Puis documenter les méthodes avec :

```java
@Operation(summary = "Lister tous les duplicatas")
```

Exemple :

```java
@Operation(
    summary = "Lister tous les duplicatas",
    description = "Retourne tous les duplicatas triés du plus récent au plus ancien."
)
@GetMapping("/duplicatas")
public List<Duplicata> duplicatas() {
    return duplicataService.getDuplicatas();
}
```

À faire par les stagiaires :

- documenter `GET /duplicatas` ;
- documenter `GET /duplicatas/{id}` ;
- documenter `POST /duplicatas_dto` ;
- documenter `DELETE /duplicatas/{id}`.

---

## Exercice 4 - Documenter les paramètres

Ajouter `@Parameter` sur les paramètres importants.

Exemple :

```java
@GetMapping("/duplicatas/{id}")
public Duplicata getDuplicata(
        @Parameter(description = "Identifiant du duplicata", example = "dup-demo-001")
        @PathVariable String id) {
    return duplicataService.getById(id);
}
```

À observer dans Swagger :

- le nom du paramètre ;
- sa description ;
- l'exemple ;
- le champ de saisie généré automatiquement.

---

## Exercice 5 - Documenter les réponses HTTP

Ajouter `@ApiResponses` pour préciser les réponses possibles.

Exemple :

```java
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Duplicata trouvé"),
    @ApiResponse(responseCode = "404", description = "Duplicata introuvable")
})
```

Dans le corrigé, les réponses suivantes sont documentées :

- `200` : succès avec corps JSON ;
- `204` : suppression réussie sans corps ;
- `400` : erreur de validation ;
- `404` : duplicata ou utilisateur introuvable.

---

## Exercice 6 - Documenter les modèles JSON

Dans `DuplicataDto`, ajouter `@Schema` :

```java
@Schema(description = "Données nécessaires pour générer un duplicata d'impôts")
public class DuplicataDto {

    @Schema(description = "Identifiant fiscal de l'utilisateur", example = "123456789")
    private String userId;

    @Schema(description = "Montant déclaré", example = "2500", minimum = "1000", maximum = "7000")
    private Integer montant;
}
```

Dans `Duplicata`, ajouter aussi `@Schema` sur les champs de sortie.

À vérifier dans Swagger :

- la section `Schemas` ;
- les exemples ;
- les contraintes visibles sur les champs.

---

## Exercice 7 - Ajouter un exemple de corps JSON

Sur `POST /duplicatas_dto`, ajouter un exemple :

```java
@Operation(
    summary = "Créer un duplicata avec un corps JSON",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            schema = @Schema(implementation = DuplicataDto.class),
            examples = @ExampleObject(value = "{\n  \"user_id\": \"123456789\",\n  \"montant\": 2500\n}")
        )
    )
)
```

Tester ensuite directement depuis Swagger UI avec le bouton `Try it out`.

---

## Exercice 8 - Tester l'API depuis Swagger UI

Dans Swagger UI :

1. ouvrir `POST /duplicatas_dto` ;
2. cliquer sur `Try it out` ;
3. utiliser ce JSON :

```json
{
  "user_id": "123456789",
  "montant": 2500
}
```

4. cliquer sur `Execute` ;
5. vérifier la réponse JSON ;
6. aller sur `GET /duplicatas` ;
7. exécuter la requête ;
8. vérifier que le nouveau duplicata est présent.

Tester ensuite une erreur de validation :

```json
{
  "user_id": "",
  "montant": 999
}
```

Résultat attendu : réponse `400`.

---

# Partie 2 - Ajouter Spring Boot DevTools

## Exercice 9 - Ajouter la dépendance DevTools

Dans `pom.xml`, ajouter :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

`optional=true` évite de propager DevTools à d'autres projets si celui-ci est utilisé comme dépendance.

---

## Exercice 10 - Comprendre ce que fait DevTools

DevTools apporte principalement :

- redémarrage automatique de l'application quand les classes changent ;
- désactivation de certains caches en développement ;
- meilleur confort avec les templates Thymeleaf ;
- support LiveReload si le navigateur est compatible ou si une extension est installée.

Important : DevTools ne remplace pas la compilation. Dans Eclipse, il faut que le projet soit recompilé pour déclencher le redémarrage.

---

## Exercice 11 - Activer la compilation automatique dans Eclipse

Dans Eclipse, vérifier :

```text
Project > Build Automatically
```

Cette option doit être cochée.

Ensuite :

1. lancer l'application ;
2. modifier un texte dans `DuplicataControlleur` ou dans une classe Java ;
3. sauvegarder ;
4. observer dans la console que l'application redémarre.

Dans la console, on voit généralement que le contexte Spring est relancé.

---

## Exercice 12 - Tester avec Thymeleaf

Dans `src/main/resources/templates/duplicatas/list.html`, modifier le titre :

```html
<h1>Duplicatas d'impôts</h1>
```

par exemple :

```html
<h1>Duplicatas d'impôts - mode formation</h1>
```

Sauvegarder puis rafraîchir la page :

```text
http://localhost:8080/ui/duplicatas
```

Avec :

```properties
spring.thymeleaf.cache=false
```

la modification du template est visible immédiatement après rafraîchissement du navigateur.

---

## Exercice 13 - Configurer DevTools dans `application-dev.properties`

Le projet contient aussi un fichier :

```text
src/main/resources/application-dev.properties
```

On peut y placer des réglages spécifiques au développement.

Exemple :

```properties
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
spring.thymeleaf.cache=false
```

Pour lancer avec le profil `dev` :

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Ou dans Eclipse :

1. `Run Configurations...` ;
2. onglet `Arguments` ;
3. ajouter dans `VM arguments` :

```text
-Dspring.profiles.active=dev
```

---

# Tests rapides avec curl

Lister les duplicatas :

```bash
curl http://localhost:8080/duplicatas
```

Créer un duplicata avec JSON :

```bash
curl -X POST http://localhost:8080/duplicatas_dto \
  -H "Content-Type: application/json" \
  -d '{"user_id":"123456789","montant":2500}'
```

Créer une erreur de validation :

```bash
curl -X POST http://localhost:8080/duplicatas_dto \
  -H "Content-Type: application/json" \
  -d '{"user_id":"","montant":999}'
```

Supprimer un duplicata :

```bash
curl -X DELETE http://localhost:8080/duplicatas/dup-demo-001
```

---

# Ce que les stagiaires doivent retenir

## OpenAPI / Swagger

- Swagger UI est une interface de test et de documentation.
- OpenAPI est le contrat JSON de l'API.
- Springdoc génère automatiquement une base documentaire à partir des contrôleurs Spring MVC.
- Les annotations permettent d'améliorer fortement la documentation.
- Une bonne documentation indique les cas d'erreur, pas seulement les cas de succès.

## DevTools

- DevTools est une dépendance de développement.
- Elle ne doit pas être considérée comme une fonctionnalité métier.
- Elle facilite les cycles modifier / sauvegarder / tester.
- Avec Spring Boot, beaucoup de caches sont adaptés automatiquement en mode développement.

---

# Proposition de déroulé pédagogique

Durée conseillée : 2h à 2h30.

## Séquence 1 - Découverte sans annotation, 20 min

- Ajouter uniquement la dépendance Springdoc.
- Lancer l'application.
- Ouvrir Swagger UI.
- Constater que la documentation existe déjà.

## Séquence 2 - Enrichissement de la documentation, 50 min

- Ajouter `@Tag`.
- Ajouter `@Operation`.
- Ajouter `@Parameter`.
- Ajouter `@ApiResponse`.
- Ajouter `@Schema`.

## Séquence 3 - Test de l'API depuis Swagger, 25 min

- Tester une création valide.
- Tester une création invalide.
- Tester la suppression.
- Vérifier les données dans H2.

## Séquence 4 - DevTools, 30 min

- Ajouter la dépendance.
- Modifier une classe Java.
- Observer le restart.
- Modifier une page Thymeleaf.
- Observer le rafraîchissement.

## Séquence 5 - Synthèse, 15 min

Questions à poser aux stagiaires :

- Quelle différence entre Swagger UI et OpenAPI ?
- Pourquoi documenter aussi les erreurs ?
- Pourquoi DevTools ne doit-il pas être embarqué en production ?
- Qu'est-ce que Spring Boot configure automatiquement ici ?

---

# Fichiers importants du corrigé

```text
pom.xml
src/main/java/com/formation/config/OpenApiConfig.java
src/main/java/com/formation/web/DuplicataControlleur.java
src/main/java/com/formation/web/ApiError.java
src/main/java/com/formation/web/RestExceptionHandler.java
src/main/java/com/formation/dto/DuplicataDto.java
src/main/java/com/formation/domain/Duplicata.java
src/main/resources/application.properties
src/main/resources/application-dev.properties
```
