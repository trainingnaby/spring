# TP 11 - Caching avec Spring Cache

## Objectif du TP

Ce TP continue l'application fil rouge de generation de duplicatas d'impots.

La version de depart contient deja :

- Spring Boot ;
- Spring MVC REST ;
- Spring MVC + Thymeleaf ;
- Spring Data JPA ;
- H2 ;
- Bean Validation ;
- AOP ;
- OpenAPI / Swagger UI ;
- DevTools ;
- gestion globale des exceptions avec `ProblemDetail`.

L'objectif est maintenant d'ajouter du cache pour eviter de relancer inutilement certaines requetes de lecture vers la base H2.

Notions abordées :

- activer le cache Spring avec `@EnableCaching` ;
- ajouter la dependance `spring-boot-starter-cache` ;
- configurer un cache simple en memoire ;
- utiliser `@Cacheable` sur les methodes de lecture ;
- utiliser `@CacheEvict` et `@Caching` lors des creations/suppressions ;
- comprendre le probleme d'invalidation du cache ;
- proposer des endpoints REST pour vider un ou plusieurs caches ;
- observer concretement l'effet du cache grace aux logs SQL Hibernate.

---

## Importer le projet dans Eclipse

1. Dezipper le projet.
2. Ouvrir Eclipse.
3. Aller dans `File > Import...`.
4. Choisir `Maven > Existing Maven Projects`.
5. Selectionner le dossier du projet.
6. Cliquer sur `Finish`.
7. Attendre la fin du telechargement Maven.
8. Si necessaire : clic droit sur le projet > `Maven > Update Project`.

Le projet est un projet Maven Spring Boot classique et contient les fichiers Eclipse `.project`, `.classpath` et `.settings`.

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
JDBC URL : jdbc:h2:mem:duplicatasdb
User     : sa
Password : laisser vide
```

Endpoints cache :

```text
GET    http://localhost:8080/api/cache
DELETE http://localhost:8080/api/cache
DELETE http://localhost:8080/api/cache/{cacheName}
DELETE http://localhost:8080/api/cache/duplicata-par-id/{id}
```

---

## Partie 1 - Ajouter la dependance cache

Dans `pom.xml`, on ajoute :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

Cette dependance donne acces a l'abstraction cache de Spring :

- `@Cacheable` ;
- `@CacheEvict` ;
- `@Caching` ;
- `CacheManager`.

Spring Cache est une abstraction. Le code applicatif ne depend pas directement d'un fournisseur technique comme Caffeine, Ehcache, Redis ou Hazelcast.

Dans ce TP, on reste volontairement simple : le cache est en memoire dans la JVM.

---

## Partie 2 - Activer le cache

Dans la classe principale :

```java
@SpringBootApplication
@EnableCaching
public class DuplicataImpotsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DuplicataImpotsApplication.class, args);
    }
}
```

`@EnableCaching` demande a Spring de creer les proxys necessaires pour intercepter les appels aux methodes annotees avec `@Cacheable`, `@CacheEvict`, etc.

Point important :

```text
Comme pour @Transactional, le caching Spring fonctionne par proxy.
Un appel interne d'une methode vers une autre methode de la meme classe ne passe pas par le proxy.
```

---

## Partie 3 - Configurer les caches

Dans `application.properties` :

```properties
spring.cache.type=simple
spring.cache.cache-names=duplicatas,duplicataParId,duplicatasParUser,duplicatasParMontant,duplicatasRecherche,duplicatasJpql,duplicatasProjections,duplicatasPage
```

Ici, `simple` signifie que Spring Boot utilise un cache en memoire base sur des maps Java.

C'est tres pratique en formation, mais ce n'est pas adapte a tous les cas de production, car :

- le cache disparait au redemarrage ;
- chaque instance de l'application a son propre cache ;
- il n'y a pas de TTL automatique ;
- il n'y a pas de limite de taille fine.

Pour une vraie application, on pourrait ensuite remplacer ce cache par Caffeine ou Redis sans changer les annotations metier.

---

## Partie 4 - Cacher les lectures simples

Dans `DuplicataService`, la liste complete des duplicatas est cachee :

```java
@Transactional(readOnly = true)
@Cacheable(cacheNames = CACHE_DUPLICATAS)
public List<Duplicata> getDuplicatas() {
    return duplicataRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
}
```

Principe :

1. premier appel : Spring ne trouve rien dans le cache ;
2. la methode est executee ;
3. Hibernate execute la requete SQL ;
4. le resultat est place en cache ;
5. deuxieme appel identique : Spring retourne directement le resultat du cache.

Exercice  :

1. lancer l'application ;
2. appeler `GET /duplicatas` ;
3. observer les logs SQL ;
4. rappeler `GET /duplicatas` ;
5. constater que la requete SQL n'apparait plus.

---

## Partie 5 - Cacher une lecture par identifiant

Toujours dans `DuplicataService` :

```java
@Transactional(readOnly = true)
@Cacheable(cacheNames = CACHE_DUPLICATA_PAR_ID, key = "#id")
public Duplicata getById(String id) {
    return findById(id)
            .orElseThrow(() -> new DuplicataNotFoundException(id));
}
```

Ici, la cle de cache est l'identifiant du duplicata.

Exemple :

```text
GET /duplicatas/dup-demo-001
```

sera stocke dans le cache `duplicataParId` avec la cle :

```text
dup-demo-001
```

Exercice :

1. appeler deux fois `GET /duplicatas/dup-demo-001` ;
2. constater que le SQL disparait au deuxieme appel ;
3. appeler `GET /duplicatas/dup-demo-002` ;
4. constater qu'une nouvelle requete SQL apparait car la cle est differente.

---

## Partie 6 - Cacher les recherches

Le TP ajoute aussi du cache sur plusieurs recherches :

```java
@Cacheable(cacheNames = CACHE_DUPLICATAS_PAR_USER, key = "#userId")
public List<Duplicata> rechercherParUserId(String userId)
```

```java
@Cacheable(cacheNames = CACHE_DUPLICATAS_PAR_MONTANT, key = "#min + '-' + #max")
public List<Duplicata> rechercherParMontantEntre(int min, int max)
```

```java
@Cacheable(cacheNames = CACHE_DUPLICATAS_PAGE,
           key = "#recherche + '-' + #page + '-' + #size + '-' + #sortProperty + '-' + #direction")
public Page<Duplicata> rechercherPagee(...)
```

Cela permet d'illustrer que la cle de cache doit representer tous les parametres qui influencent le resultat.

Mauvais exemple :

```java
@Cacheable(cacheNames = "duplicatasPage", key = "#page")
```

Pourquoi c'est mauvais ?

Parce que deux appels differents pourraient utiliser la meme cle :

```text
/duplicatas/page?page=0&size=5&sort=createdAt
/duplicatas/page?page=0&size=10&sort=montant
```

Ils ont la meme page, mais pas le meme resultat.

---

## Partie 7 - Invalidation automatique lors des modifications

Quand on cree ou supprime un duplicata, les caches de lecture peuvent devenir obsoletes.

Exemple :

1. `GET /duplicatas` met la liste en cache ;
2. `POST /duplicatas_dto` cree un nouveau duplicata ;
3. si on ne vide pas le cache, `GET /duplicatas` risque de retourner l'ancienne liste.

Pour eviter cela, la correction utilise `@CacheEvict`.

Sur la creation :

```java
@Caching(evict = {
    @CacheEvict(cacheNames = CACHE_DUPLICATAS, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_PAR_USER, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_PAR_MONTANT, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_RECHERCHE, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_JPQL, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_PROJECTIONS, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_PAGE, allEntries = true)
})
public Duplicata createDuplicata(String userId, int montant)
```

Sur la suppression :

```java
@Caching(evict = {
    @CacheEvict(cacheNames = CACHE_DUPLICATAS, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATA_PAR_ID, key = "#id"),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_PAR_USER, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_PAR_MONTANT, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_RECHERCHE, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_JPQL, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_PROJECTIONS, allEntries = true),
    @CacheEvict(cacheNames = CACHE_DUPLICATAS_PAGE, allEntries = true)
})
public void deleteById(String id)
```

Points importants :

- vider largement les caches est simple et fiable ;
- vider seulement les entrees impactees est plus performant mais plus complexe ;
- en production, la strategie depend du volume, de la criticite et de la fraicheur attendue.

---

## Partie 8 - Ajouter des endpoints pour vider le cache

La correction contient le controleur :

```text
com.formation.web.CacheControlleur
```

Il expose des endpoints pedagogiques.

### Lister les caches

```http
GET /api/cache
```

Exemple de reponse :

```json
{
  "cacheManager": "ConcurrentMapCacheManager",
  "caches": [
    "duplicatas",
    "duplicataParId",
    "duplicatasParUser",
    "duplicatasParMontant",
    "duplicatasRecherche",
    "duplicatasJpql",
    "duplicatasProjections",
    "duplicatasPage"
  ],
  "message": "Appelez plusieurs fois un endpoint de lecture, puis observez les logs SQL : le SQL disparait quand le cache est utilise."
}
```

### Vider tous les caches

```http
DELETE /api/cache
```

Retour :

```text
204 No Content
```

### Vider un cache precis

```http
DELETE /api/cache/duplicatas
```

Retour :

```text
204 No Content
```

### Vider un duplicata precis dans le cache par identifiant

```http
DELETE /api/cache/duplicata-par-id/dup-demo-001
```

Retour :

```text
204 No Content
```

Attention : ces endpoints sont publics dans le TP pour faciliter les manipulations. Dans une vraie application, il faudrait les proteger avec Spring Security, voire ne pas les exposer publiquement.

---

## Partie 9 - Scenario complet de test

### Scenario 1 : cache de liste

1. Demarrer l'application.
2. Appeler :

```http
GET /duplicatas
```

3. Observer une requete SQL dans la console.
4. Rappeler :

```http
GET /duplicatas
```

5. Observer que le SQL n'est plus execute.
6. Vider le cache :

```http
DELETE /api/cache/duplicatas
```

7. Rappeler :

```http
GET /duplicatas
```

8. Observer que le SQL reapparait.

### Scenario 2 : cache par identifiant

1. Appeler :

```http
GET /duplicatas/dup-demo-001
```

2. Rappeler le meme endpoint.
3. Constater que le SQL disparait au deuxieme appel.
4. Vider uniquement cette entree :

```http
DELETE /api/cache/duplicata-par-id/dup-demo-001
```

5. Rappeler :

```http
GET /duplicatas/dup-demo-001
```

6. Le SQL reapparait.

### Scenario 3 : invalidation apres creation

1. Appeler :

```http
GET /duplicatas
```

2. Creer un duplicata :

```http
POST /duplicatas_dto
Content-Type: application/json

{
  "user_id": "123456789",
  "montant": 2500
}
```

3. Rappeler :

```http
GET /duplicatas
```

4. La liste est correcte car la creation vide automatiquement les caches de lecture.

### Scenario 4 : invalidation apres suppression

1. Appeler :

```http
GET /duplicatas/dup-demo-001
```

2. Supprimer :

```http
DELETE /duplicatas/dup-demo-001
```

3. Rappeler :

```http
GET /duplicatas/dup-demo-001
```

4. L'API retourne une erreur `404` avec `ProblemDetail`.

---

## Partie 10 - Points pedagogiques importants

### `@Cacheable`

`@Cacheable` est utilise sur les methodes de lecture.

Si la cle existe deja dans le cache, la methode n'est pas executee.

### `@CacheEvict`

`@CacheEvict` supprime une entree ou tout un cache.

Il est souvent utilise sur les methodes d'ecriture : creation, modification, suppression.

### `@Caching`

`@Caching` permet de grouper plusieurs annotations de cache sur une meme methode.

Dans ce TP, une creation de duplicata vide plusieurs caches, car plusieurs endpoints de lecture peuvent etre impactes.

### Cle de cache

La cle doit identifier de maniere unique le resultat.

Exemples :

```java
key = "#id"
key = "#min + '-' + #max"
key = "#recherche + '-' + #page + '-' + #size + '-' + #sortProperty + '-' + #direction"
```

### Limite du cache simple

Le cache simple de Spring Boot est tres utile pour apprendre, mais il est volontairement basique.

Pour aller plus loin, on peut remplacer le cache simple par :

- Caffeine pour un cache local plus robuste avec TTL et taille maximale ;
- Redis pour un cache partage entre plusieurs instances ;
- Ehcache pour des configurations plus avancees.

