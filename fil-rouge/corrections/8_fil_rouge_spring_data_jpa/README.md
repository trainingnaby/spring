# TP 8 - Remplacer Spring JDBC par Spring Data JPA

## Objectif pédagogique

Dans le TP précédent, les duplicatas d'impôts étaient persistés avec `JdbcTemplate`.

Ce TP montre comment remplacer cette couche d'accès aux données par **Spring Data JPA** tout en restant sur une application Spring Boot simple.

Points abordés :

- la différence entre JDBC et JPA ;
- la transformation d'une classe métier en entité JPA ;
- la création d'un repository Spring Data ;
- les requêtes dérivées ;
- les requêtes JPQL ;
- les projections ;
- la pagination ;
- le tri des résultats ;
- l'accès à la console H2.

---

## Application fil rouge

L'application permet de générer des duplicatas d'impôts.

Fonctionnalités conservées :

- API REST ;
- interface web Thymeleaf ;
- validation des données ;
- suppression d'un duplicata ;
- visualisation d'un duplicata ;
- base H2 en mémoire ;
- aspect AOP de normalisation du `userId`.

---

## Import dans Eclipse

1. Dézipper le projet.
2. Dans Eclipse : `File > Import > Maven > Existing Maven Projects`.
3. Sélectionner le dossier du projet.
4. Cliquer sur `Finish`.
5. Attendre la fin du téléchargement Maven.

---

## Lancer l'application

Depuis Eclipse :

1. ouvrir `DuplicataImpotsApplication` ;
2. clic droit ;
3. `Run As > Java Application`.

Depuis un terminal, si Maven est installé :

```bash
mvn spring-boot:run
```

L'application démarre sur :

```text
http://localhost:8080
```

Interface web :

```text
http://localhost:8080/ui/duplicatas
```

API REST :

```text
http://localhost:8080/duplicatas
```

---

## Accès à la base H2

La console H2 est disponible ici :

```text
http://localhost:8080/h2-console
```

Paramètres de connexion :

```text
Driver Class : org.h2.Driver
JDBC URL     : jdbc:h2:mem:duplicatasdb
User Name    : sa
Password     : laisser vide
```

Une fois connecté, tester :

```sql
select * from duplicata;
```

---

## Étape 1 - Remplacer la dépendance JDBC par JPA

Dans `pom.xml`, on remplace :

```xml
<artifactId>spring-boot-starter-jdbc</artifactId>
```

par :

```xml
<artifactId>spring-boot-starter-data-jpa</artifactId>
```

Spring Boot ajoute alors automatiquement :

- Spring Data JPA ;
- Hibernate ;
- la gestion des transactions ;
- l'intégration avec le `DataSource` H2.

---

## Étape 2 - Transformer `Duplicata` en entité JPA

La classe `Duplicata` devient une entité persistante :

```java
@Entity
@Table(name = "duplicata")
public class Duplicata {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private int montant;

    @Column(name = "pdf_url", nullable = false)
    private String pdfUrl;
}
```

Points d'attention:

- `@Entity` indique que la classe est gérée par JPA ;
- `@Table` permet de choisir le nom de la table ;
- `@Id` indique la clé primaire ;
- `@Column` personnalise le mapping colonne/champ.

Dans ce corrigé, l'identifiant reste généré par le service :

```java
duplicata.setId("dup-" + System.currentTimeMillis());
```

C'est volontaire pour ne pas introduire trop tôt `@GeneratedValue`.

---

## Étape 3 - Supprimer le repository JdbcTemplate

Avant :

```java
@Repository
public class DuplicataRepository {
    private final JdbcTemplate jdbcTemplate;
}
```

Après :

```java
public interface DuplicataRepository extends JpaRepository<Duplicata, String> {
}
```

Spring Data JPA fournit automatiquement :

- `findAll()` ;
- `findById(id)` ;
- `save(entity)` ;
- `deleteById(id)` ;
- `existsById(id)` ;
- `findAll(Sort sort)` ;
- `findAll(Pageable pageable)`.

---

## Étape 4 - Requêtes dérivées

Les requêtes dérivées sont générées à partir du nom des méthodes.

Dans `DuplicataRepository` :

```java
List<Duplicata> findByUserId(String userId);
```

Test REST :

```text
GET http://localhost:8080/duplicatas/by-user/u1
```

Autre exemple :

```java
List<Duplicata> findByMontantBetweenOrderByMontantDesc(int montantMin, int montantMax);
```

Test REST :

```text
GET http://localhost:8080/duplicatas/by-montant?min=2000&max=7000
```

Autre exemple :

```java
List<Duplicata> findByUserIdContainingIgnoreCase(String morceauUserId);
```

Test REST :

```text
GET http://localhost:8080/duplicatas/search?q=FR
```

---

## Étape 5 - JPQL

JPQL ressemble au SQL, mais on interroge les entités Java, pas directement les tables.

Exemple dans `DuplicataRepository` :

```java
@Query("select d from Duplicata d where d.montant >= :montantMinimum order by d.createdAt desc")
List<Duplicata> rechercherParMontantMinimum(@Param("montantMinimum") int montantMinimum);
```

Test REST :

```text
GET http://localhost:8080/duplicatas/jpql?min=3000
```

---

## Étape 6 - Projections

Une projection permet de ne retourner qu'une partie d'une entité.

Interface ajoutée :

```java
public interface DuplicataResumeProjection {
    String getId();
    String getUserId();
    int getMontant();
}
```

Repository :

```java
List<DuplicataResumeProjection> findByMontantGreaterThanEqual(int montantMinimum);
```

Test REST :

```text
GET http://localhost:8080/duplicatas/projections?min=1000
```

Intérêt pédagogique :

- éviter d'exposer toutes les données ;
- réduire les données retournées ;
- montrer une alternative simple aux DTO pour la lecture.


---

## Étape 7 - Pagination et tri

La pagination est utile dès qu'une table peut contenir beaucoup de lignes.

Service :

```java
Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));
return duplicataRepository.findByUserIdContainingIgnoreCase(recherche, pageable);
```

Repository :

```java
Page<Duplicata> findByUserIdContainingIgnoreCase(String morceauUserId, Pageable pageable);
```

Test REST :

```text
GET http://localhost:8080/duplicatas/page?q=&page=0&size=2&sort=montant&direction=desc
```

Tester aussi :

```text
GET http://localhost:8080/duplicatas/page?q=u&page=0&size=1&sort=userId&direction=asc
```

À observer dans la réponse JSON :

- `content` ;
- `totalElements` ;
- `totalPages` ;
- `size` ;
- `number` ;
- `sort`.

---

## Étape 8 - Configuration JPA dans `application.properties`

```properties
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

Explications :

- `ddl-auto=create` : Hibernate crée les tables au démarrage ;
- `show-sql=true` : affiche le SQL généré ;
- `format_sql=true` : rend le SQL plus lisible ;
- `defer-datasource-initialization=true` : exécute `data.sql` après la création des tables par Hibernate ;
- `data.sql` insère les données de démonstration.

Remarque importante :

Pour un vrai projet, on évite généralement `ddl-auto=create` et on utilise plutôt :

- Flyway ;
- Liquibase ;
- des scripts SQL versionnés.


---

## Fichiers importants du corrigé

```text
pom.xml
src/main/java/com/formation/domain/Duplicata.java
src/main/java/com/formation/repository/DuplicataRepository.java
src/main/java/com/formation/repository/projection/DuplicataResumeProjection.java
src/main/java/com/formation/service/DuplicataService.java
src/main/java/com/formation/web/DuplicataControlleur.java
src/main/resources/application.properties
src/main/resources/data.sql
```

---

## Vérifications rapides

Lister les duplicatas :

```text
GET http://localhost:8080/duplicatas
```

Créer un duplicata :

```text
POST http://localhost:8080/duplicatas?user_id=u1&montant=3500
```

Interface web :

```text
http://localhost:8080/ui/duplicatas
```

Console H2 :

```text
http://localhost:8080/h2-console
```

---

## Message pédagogique à retenir

Avec `JdbcTemplate`, le développeur garde la main sur le SQL et le mapping.

Avec Spring Data JPA, le développeur travaille davantage avec des entités, des repositories et des conventions.

Spring Data JPA est très productif, mais il faut comprendre le SQL généré et surveiller les performances.
findByUserIdContainingIgnoreCase