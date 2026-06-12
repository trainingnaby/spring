# TP 19 - Tests unitaires et tests d'intégration avec Spring Boot

Ce TP complète l'application fil rouge de génération de duplicatas d'impôts avec un jeu de tests volontairement limité, mais représentatif.

L'objectif n'est pas de tout tester. L'objectif est de montrer **où placer les tests**, **quel niveau de Spring charger**, et **comment tester une application sécurisée**.

## Objectifs pédagogiques

A apprendre :

- écrire un test unitaire de service avec JUnit 5 et Mockito ;
- tester un repository Spring Data JPA avec `@DataJpaTest` ;
- tester un contrôleur REST avec `@WebMvcTest` et `MockMvc` ;
- écrire un test d'intégration REST avec `@SpringBootTest` ;
- tester des endpoints protégés par Spring Security ;
- utiliser `@WithMockUser` pour tester la sécurité MVC ;
- tester une API REST sécurisée avec un vrai token JWT.

## Rappel : les types de tests

### Test unitaire

Un test unitaire teste une classe seule, sans démarrer Spring.

Dans ce projet :

```java
DuplicataServiceTest
```

On teste la logique du service avec des mocks :

- `DuplicataRepository` est mocké ;
- `UserService` est mocké ;
- `DuplicataWebSocketNotifier` est mocké.

Ce test est rapide et permet de vérifier le comportement métier.

---

### Test de repository / DAO

Un test DAO vérifie l'accès aux données.

Dans ce projet :

```java
DuplicataRepositoryTest
```

Il utilise :

```java
@DataJpaTest
```

Spring Boot démarre uniquement la partie JPA :

- repository ;
- entity manager ;
- base H2 de test ;
- transaction de test.

Ce test permet de vérifier :

- les requêtes dérivées ;
- les projections ;
- la persistance JPA.

---

### Test de contrôleur

Un test de contrôleur vérifie uniquement la couche web.

Dans ce projet :

```java
DuplicataControlleurTest
```

Il utilise :

```java
@WebMvcTest(DuplicataControlleur.class)
@AutoConfigureMockMvc(addFilters = false)
```

Le service est mocké avec :

```java
@MockBean
private DuplicataService duplicataService;
```

Ici, on ne teste pas la base de données ni le service. On vérifie seulement :

- l'URL appelée ;
- le statut HTTP ;
- le JSON retourné.

---

### Test d'intégration REST

Un test d'intégration démarre presque toute l'application.

Dans ce projet :

```java
DuplicataRestIntegrationTest
```

Il utilise :

```java
@SpringBootTest
@AutoConfigureMockMvc
```

On teste réellement :

- Spring MVC ;
- Spring Security ;
- JWT ;
- service ;
- repository ;
- H2 ;
- gestion d'erreurs REST.

## Nouveautés ajoutées au projet

### Dépendance Spring Security Test

Dans `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

Cette dépendance apporte notamment :

- `@WithMockUser` ;
- `csrf()` ;
- des utilitaires MockMvc pour Spring Security.

## Structure des tests

```text
src/test/java/com/formation
 ├── service
 │   └── DuplicataServiceTest.java
 ├── repository
 │   └── DuplicataRepositoryTest.java
 ├── web
 │   └── DuplicataControlleurTest.java
 └── integration
     ├── DuplicataRestIntegrationTest.java
     └── DuplicataMvcSecurityTest.java
```

## Configuration de test

Le fichier suivant a été ajouté :

```text
src/test/resources/application-test.properties
```

Il configure une base H2 dédiée aux tests :

```properties
spring.datasource.url=jdbc:h2:mem:duplicatasdb-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
```

Les tests utilisent le profil `test` avec :

```java
@ActiveProfiles("test")
```

## Exercice 1 - Tester la couche service

Fichier corrigé :

```text
src/test/java/com/formation/service/DuplicataServiceTest.java
```

Le test vérifie que `createDuplicata` :

1. recherche l'utilisateur ;
2. crée un duplicata ;
3. génère l'URL du PDF ;
4. sauvegarde en repository ;
5. publie une notification WebSocket.

Extrait :

```java
when(userService.findById("u1")).thenReturn(user);
when(duplicataRepository.save(any(Duplicata.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
```

Puis :

```java
verify(duplicataRepository).save(any(Duplicata.class));
verify(webSocketNotifier).notifierCreation(resultat);
```

## Exercice 2 - Tester la couche DAO

Fichier corrigé :

```text
src/test/java/com/formation/repository/DuplicataRepositoryTest.java
```

Le test utilise :

```java
@DataJpaTest
```

Il vérifie une requête dérivée :

```java
findByUserId("dao-user")
```

et une projection :

```java
findByMontantGreaterThanEqual(1000)
```

`@DataJpaTest` ne démarre pas tout le serveur web. Il démarre seulement ce qui est utile pour JPA.

## Exercice 3 - Tester la couche contrôleur

Fichier corrigé :

```text
src/test/java/com/formation/web/DuplicataControlleurTest.java
```

Le test utilise :

```java
@WebMvcTest(controllers = DuplicataControlleur.class)
```

Le service est simulé :

```java
@MockBean
private DuplicataService duplicataService;
```

On vérifie la réponse JSON :

```java
mockMvc.perform(get("/duplicatas"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$[0].id").value("dup-test-controller-001"));
```

Dans ce test, les filtres Spring Security sont désactivés avec :

```java
@AutoConfigureMockMvc(addFilters = false)
```

C'est volontaire : on veut tester uniquement le contrôleur.

## Exercice 4 - Tester une API REST sécurisée par JWT

Fichier corrigé :

```text
src/test/java/com/formation/integration/DuplicataRestIntegrationTest.java
```

Ce test montre quatre cas :

### 1. Appel sans token

```java
GET /duplicatas
```

Résultat attendu :

```text
401 Unauthorized
```

### 2. Appel avec token USER

```java
GET /duplicatas
Authorization: Bearer <token>
```

Résultat attendu :

```text
200 OK
```

### 3. Création avec token USER

```java
POST /duplicatas_dto
```

Résultat attendu :

```text
403 Forbidden
```

L'utilisateur est authentifié, mais pas autorisé à créer.

### 4. Création avec token ADMIN

```java
POST /duplicatas_dto
```

Résultat attendu :

```text
200 OK
```

## Obtenir un token dans un test

Le test appelle directement :

```text
POST /api/auth/login
```

Avec :

```json
{
  "username": "admin",
  "password": "admin"
}
```

Puis il extrait le token JSON :

```java
JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
return json.get("token").asText();
```

Cela permet de tester la sécurité dans des conditions proches du réel.

## Exercice 5 - Tester la sécurité MVC avec `@WithMockUser`

Fichier corrigé :

```text
src/test/java/com/formation/integration/DuplicataMvcSecurityTest.java
```

Ce test montre trois cas :

### 1. Utilisateur non connecté

```java
GET /ui/duplicatas
```

Résultat attendu : redirection vers `/login`.

### 2. Utilisateur USER sur une page ADMIN

```java
@WithMockUser(username = "user", roles = "USER")
```

Puis :

```java
GET /ui/duplicatas/new
```

Résultat attendu :

```text
403 Forbidden
```

### 3. Utilisateur ADMIN avec CSRF

```java
@WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
```

Puis :

```java
post("/ui/duplicatas")
    .with(csrf())
```

Le `.with(csrf())` est important, car les formulaires MVC sont protégés par CSRF.

Sans CSRF, Spring Security retourne 403.

## Commandes utiles

Depuis un terminal :

```bash
mvn test
```

Pour lancer un seul test :

```bash
mvn -Dtest=DuplicataServiceTest test
```

```bash
mvn -Dtest=DuplicataRestIntegrationTest test
```

## Dans Eclipse

1. Importer le projet :

```text
File > Import > Maven > Existing Maven Projects
```

2. Sélectionner le dossier du projet.

3. Mettre à jour Maven :

```text
Clic droit sur le projet > Maven > Update Project
```

4. Lancer les tests :

```text
Clic droit sur src/test/java > Run As > JUnit Test
```

## Ordre

1. Lancer tous les tests pour voir le résultat global.
2. Ouvrir `DuplicataServiceTest`.
3. Expliquer Mockito : `when`, `verify`, `ArgumentCaptor`.
4. Ouvrir `DuplicataRepositoryTest`.
5. Expliquer `@DataJpaTest`.
6. Ouvrir `DuplicataControlleurTest`.
7. Expliquer `@WebMvcTest` et `MockMvc`.
8. Ouvrir `DuplicataRestIntegrationTest`.
9. Expliquer `@SpringBootTest`, JWT et tests REST sécurisés.
10. Ouvrir `DuplicataMvcSecurityTest`.
11. Expliquer `@WithMockUser` et `csrf()`.

## Points importants à retenir

| Besoin | Annotation conseillée |
|---|---|
| Tester une classe seule | `@ExtendWith(MockitoExtension.class)` |
| Tester un repository JPA | `@DataJpaTest` |
| Tester un contrôleur MVC/REST isolé | `@WebMvcTest` |
| Tester toute l'application | `@SpringBootTest` |
| Tester avec MockMvc | `@AutoConfigureMockMvc` |
| Simuler un utilisateur connecté | `@WithMockUser` |
| Ajouter un token CSRF dans un test | `.with(csrf())` |

## Limite volontaire du TP

On ne teste pas encore :

- tous les endpoints ;
- tous les cas d'erreur ;
- les WebSockets ;
- les endpoints Actuator ;
- le cache ;
- OAuth2 GitHub.

C'est volontaire. Pour une formation, il vaut mieux montrer peu de tests, mais bien choisis.
