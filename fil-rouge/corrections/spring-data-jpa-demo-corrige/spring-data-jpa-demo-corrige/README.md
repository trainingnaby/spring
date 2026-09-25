# Démonstration Spring Data JPA

Projet support pour la partie **Spring Data** de la formation *Spring - développer des applications d'entreprise*.

L'objectif n'est pas de construire une application complète. Le projet sert à observer ce que Spring Data JPA apporte par rapport à une couche DAO écrite à la main : implémentation des repositories, requêtes dérivées, `@Query`, projections, pagination et tri.

## Prérequis

- JDK 17
- Maven 3.9 ou Maven intégré à Eclipse
- Eclipse avec le support Maven (m2e)

La base utilisée est H2 en mémoire. Il n'y a donc rien à installer.

## Import dans Eclipse

1. Décompresser le projet.
2. Dans Eclipse : **File > Import > Maven > Existing Maven Projects**.
3. Sélectionner le dossier `spring-data-jpa-demo`.
4. Terminer l'import Maven.
5. Lancer `SpringDataJpaDemoApplication` avec **Run As > Java Application** ou **Spring Boot App** si Spring Tools est installé.

L'application écoute sur `http://localhost:8080`.


## Pourquoi les contrôleurs ne retournent pas les entités JPA

Dans cette version, les classes `Employee` et `Department` restent dans la couche de persistance. Les endpoints REST retournent des projections (`EmployeeSummary`) ou un DTO (`EmployeeSalaryDto`).

Cela évite de faire dépendre le contrat HTTP des détails Hibernate (`LAZY`, proxies, `hibernateLazyInitializer`) et permet de choisir explicitement les données exposées. Le `ManyToOne` vers `Department` reste donc `LAZY` : on ne le passe pas en `EAGER` uniquement pour faciliter la sérialisation JSON.

La projection principale expose `departmentName` directement :

```java
public interface EmployeeSummary {
    Long getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    BigDecimal getSalary();
    String getDepartmentName();
}
```

Le premier test à faire après le démarrage est :

```text
GET http://localhost:8080/api/employees
```

La réponse JSON ne contient ni objet proxy Hibernate ni propriété technique de JPA.

## 1. Ce que Spring Boot configure pour nous

Le `pom.xml` contient `spring-boot-starter-data-jpa` et le driver H2. À partir de ces dépendances et des propriétés de `application.properties`, Spring Boot configure notamment le `DataSource`, Hibernate/JPA, l'`EntityManagerFactory`, le gestionnaire de transactions et l'infrastructure Spring Data JPA.

Le projet ne contient donc pas de configuration manuelle de `DataSource`, d'`EntityManagerFactory` ou de `PlatformTransactionManager`.

Au démarrage, Spring Data recherche les interfaces de repository. Il crée une implémentation à l'exécution et enregistre le résultat comme bean Spring. C'est la raison pour laquelle ceci suffit :

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
```

Il n'y a pas de `EmployeeRepositoryImpl` à écrire pour les opérations standards.

Pour observer l'auto-configuration Boot, ajouter temporairement :

```properties
debug=true
```

puis relancer l'application et rechercher dans la console le **CONDITIONS EVALUATION REPORT**.

## 2. CrudRepository, PagingAndSortingRepository ou JpaRepository ?

### CrudRepository

`DepartmentRepository` étend `CrudRepository` :

```java
public interface DepartmentRepository
        extends CrudRepository<Department, Long> {
}
```

Il fournit les opérations CRUD essentielles : `save`, `findById`, `findAll`, `existsById`, `count`, `deleteById`, etc.

C'est un bon choix quand le repository a seulement besoin des opérations de base et que l'on veut exprimer une dépendance minimale.

### PagingAndSortingRepository

Il ajoute les opérations liées au tri et à la pagination. Dans les versions actuelles de Spring Data, il ne faut pas partir du principe que cette interface hérite de `CrudRepository`. Si un repository a besoin explicitement des deux contrats, il peut étendre les deux interfaces.

Dans une application JPA, on utilise cependant très souvent `JpaRepository`, qui rassemble les opérations courantes dont on a besoin.

### JpaRepository

`EmployeeRepository` étend :

```java
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {
}
```

`JpaRepository` est spécifique à JPA. Il fournit CRUD, pagination, tri et des opérations JPA supplémentaires comme `flush()`.

Pour une application Spring Data JPA classique, c'est généralement l'interface la plus pratique. `CrudRepository` reste intéressant quand on souhaite volontairement limiter le contrat exposé.

## 3. Méthodes fournies automatiquement

Sans écrire de requête, `EmployeeRepository` possède déjà par exemple :

```java
repository.save(employee);
repository.findById(1L);
repository.findAll();
repository.deleteById(1L);
repository.count();
```

Spring Data fournit l'implémentation. Le SQL reste exécuté par le fournisseur JPA, Hibernate dans ce projet.

Test :

```text
GET /api/employees
```

Regarder en même temps le SQL affiché dans la console.

## 4. Requêtes dérivées du nom de méthode

Spring Data sait analyser certains noms de méthodes :

```java
List<EmployeeSummary> findByLastNameIgnoreCase(String lastName);
List<EmployeeSummary> findByActiveTrueOrderByLastNameAsc();
List<EmployeeSummary> findBySalaryGreaterThanEqual(BigDecimal minimumSalary);
List<EmployeeSummary> findByDepartmentName(String departmentName);
```

Par exemple :

```text
findByDepartmentName
       |          |
      critère   propriété imbriquée
```

Spring Data construit la requête à partir du nom. Il faut garder des noms lisibles : lorsqu'une méthode dérivée devient trop longue ou difficile à comprendre, `@Query` est souvent plus clair.

Essai :

```text
GET /api/employees/by-last-name?name=Martin
```

## 5. @Query en JPQL

Exemple dans `EmployeeRepository` :

```java
@Query("""
    select e.id as id, e.firstName as firstName, e.lastName as lastName,
           e.email as email, e.salary as salary,
           e.department.name as departmentName
    from Employee e
    where e.department.name = :department
      and e.salary >= :minimumSalary
    order by e.lastName, e.firstName
    """)
List<EmployeeSummary> findWellPaidEmployees(
    @Param("department") String department,
    @Param("minimumSalary") BigDecimal minimumSalary);
```

Le JPQL travaille avec les **entités et leurs propriétés** : `Employee`, `department.name`, `salary`. On ne manipule pas directement les noms physiques des tables.

Essai :

```text
GET /api/employees/jpql?department=Informatique&min=3500
```

## 6. @Query en SQL natif

Pour écrire directement du SQL :

```java
@Query(value = """
    select e.*
    from employee e
    where lower(e.email) like lower(concat('%', :domain))
    order by e.last_name
    """, nativeQuery = true)
List<EmployeeSummary> findByEmailDomainNative(@Param("domain") String domain);
```

Ici on utilise les noms de la base : `employee`, `email`, `last_name`.

Le SQL natif est utile pour une requête spécifique au SGBD ou difficile à exprimer en JPQL. En contrepartie, il couple davantage la requête au schéma SQL et parfois au moteur de base utilisé.

Essai :

```text
GET /api/employees/native?domain=entreprise.fr
```

## 7. Projection par interface

Pour une liste, on n'a pas toujours besoin de charger toutes les informations de l'entité.

```java
public interface EmployeeSummary {
    String getFirstName();
    String getLastName();
    String getEmail();
}
```

Le repository expose :

```java
List<EmployeeSummary> findAllProjectedBy();
```

Spring Data crée à l'exécution un objet qui implémente cette interface. Pour une projection fermée simple, Spring Data JPA peut limiter la sélection aux propriétés nécessaires.

Essai :

```text
GET /api/employees
```

Observer le `select` Hibernate dans la console : le contrôleur ne renvoie jamais directement une entité JPA.

## 8. Projection DTO

Une projection peut aussi retourner une vraie classe ou un `record` Java :

```java
public record EmployeeSalaryDto(
    String firstName,
    String lastName,
    BigDecimal salary) {
}
```

La démonstration utilise une expression constructeur JPQL :

```java
@Query("""
    select new fr.formation.springdata.dto.EmployeeSalaryDto(
        e.firstName, e.lastName, e.salary)
    from Employee e
    where e.active = true
    order by e.salary desc
    """)
List<EmployeeSalaryDto> findActiveEmployeeSalaries();
```

Essai :

```text
GET /api/employees/salaries
```

À retenir : une projection interface est très pratique pour définir une vue de lecture Spring Data. Un DTO est un véritable objet Java que l'on peut également utiliser en dehors du repository. Les deux approches peuvent éviter de récupérer inutilement toutes les colonnes d'une entité, selon la requête utilisée.

## 9. Pagination

Une méthode peut accepter un `Pageable` :

```java
Page<EmployeeSummary> findByActive(boolean active, Pageable pageable);
```

Le contrôleur construit ici :

```java
PageRequest.of(page, size, Sort.by(sort).ascending())
```

Essais :

```text
GET /api/employees/page?page=0&size=2
GET /api/employees/page?page=1&size=2
GET /api/employees/page?page=0&size=3&sort=salary
```

Le résultat `Page` contient les éléments mais aussi des métadonnées : numéro de page, taille, nombre total d'éléments et nombre total de pages.

Observer les requêtes SQL : une pagination `Page` nécessite généralement aussi une requête de comptage pour connaître le total.

## 10. Tri

`JpaRepository` accepte directement un `Sort` :

```java
repository.findAll(
    Sort.by(
        Sort.Order.asc("department.name"),
        Sort.Order.desc("salary")
    )
);
```

Essai :

```text
GET /api/employees/sorted
```

Le tri peut également être transporté par un `Pageable`.

## 11. Base H2

La console H2 est activée :

```text
http://localhost:8080/h2-console
```

Paramètres :

```text
JDBC URL : jdbc:h2:mem:formationdb
User     : sa
Password : (vide)
```

Cela permet de comparer les données présentes dans la base avec les objets retournés par les repositories.

## Etapes lecture

1. Montrer `Employee` et `Department`.
2. Ouvrir `DepartmentRepository` et constater qu'il n'y a aucune implémentation.
3. Montrer les méthodes héritées de `CrudRepository` / `JpaRepository`.
4. Ajouter une méthode dérivée et relancer.
5. Montrer une requête JPQL avec `@Query`.
6. Comparer avec la requête native.
7. Appeler `GET /api/employees` et vérifier que la réponse est une projection et non une entité JPA.
8. Montrer le DTO `EmployeeSalaryDto`.
9. Terminer par pagination et tri.
10. Activer `debug=true` pour revenir sur l'auto-configuration Spring Boot.

## Point important

Spring Data JPA ne supprime pas JPA et Hibernate. Il réduit surtout le code répétitif de la couche d'accès aux données.

On peut résumer le chemin ainsi :

```text
Notre interface Repository
        ↓
Spring Data JPA
        ↓
implémentation créée à l'exécution
        ↓
JPA / EntityManager
        ↓
Hibernate
        ↓
SQL
        ↓
Base de données
```

Pendant la formation, garder `spring.jpa.show-sql=true` est utile : les stagiaires voient immédiatement le lien entre une méthode de repository et le SQL réellement exécuté.
