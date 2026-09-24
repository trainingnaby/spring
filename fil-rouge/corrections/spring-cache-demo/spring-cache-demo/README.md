# TP - Mettre en cache des données avec Spring Boot

Ce projet sert de support à la partie **cache Spring** de la formation *Spring - développer des applications d'entreprise*.

Le cas choisi est volontairement simple : un catalogue de produits est consulté par une API REST. L'accès à la source de données prend artificiellement deux secondes. Le but est de constater ce qui se passe avant et après la mise en cache, puis de traiter la mise à jour et la suppression des données.

## 1. Ce que montre le projet

Le TP permet de manipuler :

- `@EnableCaching` pour activer l'infrastructure de cache ;
- `@Cacheable` pour mémoriser le résultat d'une lecture ;
- `@CachePut` pour mettre à jour la donnée et le cache ;
- `@CacheEvict` pour retirer une entrée du cache ;
- `allEntries = true` pour vider un cache ;
- Caffeine comme implémentation locale du cache ;
- les clés de cache et la durée de vie des entrées.

L'application n'utilise volontairement pas de base de données. `ProductRepository` simule une source lente avec une attente de deux secondes. Cela permet de voir immédiatement l'effet du cache sans mélanger le sujet avec JPA.

## 2. Import dans Eclipse

Prérequis : Java 17 et Maven.

Dans Eclipse :

1. **File > Import...**
2. **Maven > Existing Maven Projects**
3. sélectionner le dossier `spring-cache-demo`
4. terminer l'import puis attendre la résolution des dépendances Maven
5. lancer `CacheDemoApplication` avec **Run As > Java Application** ou **Spring Boot App** si Spring Tools est installé.

L'application démarre sur le port `8080`.

## 3. Le principe du cache Spring

Spring fournit une abstraction de cache. Le code métier utilise les annotations Spring et ne manipule pas directement Caffeine.

```text
Controller
    |
    v
ProductService
    |
    | @Cacheable("products")
    v
Cache Spring ------> Caffeine
    |
    | cache absent
    v
ProductRepository
```

Lors d'un premier appel, Spring ne trouve pas la clé dans le cache : la méthode est exécutée et son résultat est mémorisé. Lors d'un appel suivant avec la même clé, Spring renvoie directement la valeur du cache.

## 4. Activer le cache

Dans la classe principale :

```java
@SpringBootApplication
@EnableCaching
public class CacheDemoApplication {
    // ...
}
```

`@EnableCaching` demande à Spring d'activer le traitement des annotations de cache.

Le `pom.xml` contient :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

Spring Boot détecte Caffeine sur le classpath et configure le gestionnaire de cache correspondant.

## 5. Premier test : `@Cacheable`

La méthode de lecture est :

```java
@Cacheable(cacheNames = "products", key = "#id")
public Product findById(Long id) {
    return repository.findById(id);
}
```

Appelez deux fois :

```text
GET http://localhost:8080/api/products/1
```

Au premier appel, la réponse arrive après environ deux secondes et la console affiche :

```text
ProductService.findById(1) exécuté
--> Accès à la source de données
```

Au deuxième appel, la réponse est quasiment immédiate et ces messages ne réapparaissent pas. Spring a trouvé la clé `1` dans le cache `products` et n'a pas exécuté la méthode.

Essayez ensuite `/api/products/2`. La clé est différente : la méthode est de nouveau exécutée au premier accès.

## 6. Une clé de cache

Ici :

```java
key = "#id"
```

signifie que la valeur du paramètre `id` sert de clé.

On obtient par exemple :

```text
products
  1 -> Product(1, ...)
  2 -> Product(2, ...)
```

Spring accepte des expressions SpEL pour construire des clés plus élaborées lorsque plusieurs paramètres interviennent.

## 7. Mettre à jour une donnée : `@CachePut`

Une difficulté apparaît dès qu'une donnée déjà en cache est modifiée. Le cache ne doit pas continuer à retourner l'ancienne valeur.

```java
@CachePut(cacheNames = "products", key = "#id")
public Product updatePrice(Long id, BigDecimal price) {
    return repository.updatePrice(id, price);
}
```

Contrairement à `@Cacheable`, `@CachePut` **exécute toujours la méthode**, puis place son résultat dans le cache.

Test :

```text
PATCH http://localhost:8080/api/products/1/price
Content-Type: application/json

{
  "price": 99.90
}
```

Puis :

```text
GET http://localhost:8080/api/products/1
```

Le GET doit retourner le nouveau prix directement depuis le cache.

## 8. Supprimer une entrée : `@CacheEvict`

```java
@CacheEvict(cacheNames = "products", key = "#id")
public void delete(Long id) {
    repository.deleteById(id);
}
```

Lorsqu'un produit est supprimé, Spring retire également la clé correspondante du cache.

```text
DELETE http://localhost:8080/api/products/3
```

`@CacheEvict` est également utile lorsqu'une opération modifie une donnée sans pouvoir facilement calculer la nouvelle valeur à placer dans le cache.

## 9. Vider tout le cache

Le projet contient aussi :

```java
@CacheEvict(cacheNames = "products", allEntries = true)
public void clearCache() {
}
```

Test :

```text
POST http://localhost:8080/api/products/cache/clear
```

Les prochaines lectures repasseront par `ProductRepository`.

## 10. Configuration de Caffeine

Dans `application.properties` :

```properties
spring.cache.type=caffeine
spring.cache.cache-names=products
spring.cache.caffeine.spec=maximumSize=100,expireAfterWrite=60s
```

Le cache contient au maximum 100 entrées. Une entrée expire 60 secondes après son écriture. Ces valeurs sont courtes pour faciliter les tests pendant le TP ; elles seraient choisies selon les besoins réels de l'application en production.

## 11. `@Cacheable`, `@CachePut` ou `@CacheEvict` ?

| Annotation | Comportement principal |
|---|---|
| `@Cacheable` | retourne le cache s'il existe, sinon exécute la méthode et mémorise le résultat |
| `@CachePut` | exécute toujours la méthode puis met son résultat en cache |
| `@CacheEvict` | supprime une ou plusieurs entrées du cache |

## 12. Point important : le proxy Spring

Les annotations de cache sont prises en charge par l'infrastructure Spring, généralement au moyen d'un proxy autour du bean.

Cela entraîne une conséquence classique : un appel interne comme `this.findById(id)` depuis une autre méthode de `ProductService` ne traverse pas le proxy. Il ne faut donc pas utiliser ce type d'appel pour voir l'effet de `@Cacheable`.

Le contrôleur du projet appelle le service depuis un autre bean, ce qui permet au mécanisme de fonctionner normalement.

## 13. Déroulé

1. Lancer l'application et appeler deux fois `/api/products/1`.
2. Observer le temps de réponse et la console.
3. Commenter `@Cacheable`, redémarrer et refaire le test.
4. Réactiver `@Cacheable` et tester deux identifiants différents.
5. Modifier le prix avec le `PATCH` et expliquer `@CachePut`.
6. Vider le cache avec `/api/products/cache/clear`.
7. Modifier `expireAfterWrite=60s` en `expireAfterWrite=10s`, redémarrer et constater l'expiration.
8. Terminer par la question : quelles données de notre application sont réellement de bonnes candidates au cache ?

## 14. Quand mettre en cache ?

Le cache est surtout intéressant lorsque la donnée est lue souvent, coûteuse à récupérer ou à calculer, et ne change pas en permanence.

Il faut en revanche réfléchir à l'invalidation. Une donnée très facile à lire mais difficile à maintenir cohérente en cache peut apporter plus de complexité que de gain.

Dans une application distribuée, Caffeine reste un cache local à chaque instance. Si plusieurs instances de l'application doivent partager le même cache, on envisagera plutôt une solution distribuée, par exemple Redis. L'abstraction de cache Spring permet de conserver une programmation proche tout en changeant de fournisseur.
