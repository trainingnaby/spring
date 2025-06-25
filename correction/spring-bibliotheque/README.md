
# Application Spring Boot - Gestion Bibliothèque

## Fonctionnalités

- Gestion des **Livres**, **Auteurs**, **Catégories** et **Critiques**
- Base de données **H2** pour développement
- **Elasticsearch** via Docker pour la recherche plein texte
- **API REST** et **Interface Web Thymeleaf**
- **Validation**, **Messages Flash**, **Suppression avec confirmation**
- **AOP** pour règles métier
- **Scheduler**, **Cache**, **Profiles**, **Configuration dynamique**
- Exemples de requêtes JPQL, SQL natif et Elasticsearch

---

## Lancer l'application

### 1 - Prérequis

- Java 17
- Maven
- Docker

### 2 - Lancer Elasticsearch et Kibana

```
docker-compose up -d
```

### 3 - Lancer l'application Spring Boot

```
./mvnw spring-boot:run
```

L'interface Web sera accessible sur [http://localhost:8080](http://localhost:8080)

### 4 - Accès à H2

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

- JDBC URL : `jdbc:h2:mem:testdb`
- User : `sa`

### 5 - Accès à Kibana

[http://localhost:5601](http://localhost:5601)

---

## Navigation Web

- **Accueil :** `/`
- **Livres :** `/web/livres`
- **Critiques :** `/web/critiques`
- **Auteurs :** `/web/auteurs`

Avec création, modification, suppression et validation.

---

## API REST (exemples)

- `GET /api/livres`
- `POST /api/livres`
- `POST /api/livres/{id}/archiver`
- `GET /api/livres/search?keyword=motclef`

---

## Recherche plein texte

Elasticsearch indexe les livres pour recherche rapide.
Configurer l'accès dans `application-dev.properties`.

---

## Profils disponibles

- **dev** : H2 + cache simple
- **prod** : PostgreSQL + cache avancé + Elasticsearch réel

---

## Postman

Une collection Postman est incluse pour tester tous les endpoints REST.

---

## Structure

- `entity/` : Entités JPA
- `repository/` : Repos JPA & ES
- `service/` : Règles métier + AOP
- `controller/` : REST & Web MVC
- `templates/` : Thymeleaf
- `config/` : Config Cache, Scheduler, AOP, Propriétés dynamiques

---

## Auteur

Naby GUEYE

