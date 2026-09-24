# TP 12 - Spring Boot Actuator : supervision de l'application de duplicatas

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
- gestion globale des exceptions avec `ProblemDetail` ;
- Spring Cache avec endpoints de vidage de cache.

L'objectif de ce TP est d'ajouter **Spring Boot Actuator** pour superviser l'application et montrer ce que Spring Boot fournit automatiquement pour observer une application en fonctionnement.

Les stagiaires vont apprendre a :

- ajouter `spring-boot-starter-actuator` ;
- exposer des endpoints de supervision ;
- consulter l'etat de l'application avec `/actuator/health` ;
- afficher des informations applicatives avec `/actuator/info` ;
- observer les metriques avec `/actuator/metrics` ;
- observer les caches Spring avec `/actuator/caches` ;
- creer un `HealthIndicator` metier personnalise pour le module de duplicatas ;
- comprendre les precautions de securite autour des endpoints Actuator.

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

## URLs utiles du projet

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

Endpoints de cache deja presents dans le TP precedent :

```text
GET    http://localhost:8080/api/cache
DELETE http://localhost:8080/api/cache
DELETE http://localhost:8080/api/cache/{cacheName}
DELETE http://localhost:8080/api/cache/duplicata-par-id/{id}
```

---

# Partie 1 - Ajouter Spring Boot Actuator

Dans `pom.xml`, on ajoute la dependance :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Cette dependance ajoute automatiquement plusieurs endpoints techniques.

Sans Spring Boot, il faudrait configurer soi-meme beaucoup de choses :

- les endpoints web ;
- les indicateurs de sante ;
- les metriques ;
- l'exposition HTTP ;
- l'integration avec le contexte Spring ;
- l'integration avec les composants deja presents comme la base de donnees, le cache, le serveur web, etc.

Avec Spring Boot, il suffit d'ajouter le starter et quelques proprietes.

---

# Partie 2 - Configurer l'exposition des endpoints

Dans `application.properties` :

```properties
management.endpoints.web.exposure.include=health,info,metrics,caches,beans,mappings,env,loggers
management.endpoint.health.show-details=always
management.endpoint.health.show-components=always
management.endpoint.health.probes.enabled=true
```

Explication :

```properties
management.endpoints.web.exposure.include=...
```

permet de choisir les endpoints accessibles en HTTP.

Important : en production, il ne faut pas exposer trop d'endpoints sans securite.

Pour un TP, on expose volontairement plusieurs endpoints pour les observer depuis le navigateur.

---

# Partie 3 - Tester les endpoints Actuator

## Endpoint racine

```text
GET http://localhost:8080/actuator
```

Il liste les endpoints Actuator exposes.

## Health global

```text
GET http://localhost:8080/actuator/health
```

Exemple de reponse attendue :

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    },
    "duplicata": {
      "status": "UP"
    }
  }
}
```

Spring Boot detecte automatiquement certains composants :

- la base de donnees H2 ;
- l'espace disque ;
- le ping applicatif ;
- les composants personnalises comme `DuplicataHealthIndicator`.

## Info

```text
GET http://localhost:8080/actuator/info
```

Les proprietes suivantes alimentent cet endpoint :

```properties
management.info.env.enabled=true
info.app.name=Duplicata Impots
info.app.description=Application fil rouge de generation de duplicatas d'impots
info.app.version=12-actuator
info.app.training.module=Spring Boot Actuator
```

## Metrics

```text
GET http://localhost:8080/actuator/metrics
```

Puis consulter une metrique precise :

```text
GET http://localhost:8080/actuator/metrics/http.server.requests
GET http://localhost:8080/actuator/metrics/jvm.memory.used
GET http://localhost:8080/actuator/metrics/process.uptime
```

## Caches

```text
GET http://localhost:8080/actuator/caches
```

Cet endpoint permet de voir les caches Spring declares dans l'application.

Dans ce projet, les caches sont :

```text
duplicatas
duplicataParId
duplicatasParUser
duplicatasParMontant
duplicatasRecherche
duplicatasJpql
duplicatasProjections
duplicatasPage
```

---

# Partie 4 - Ajouter un HealthIndicator metier

## Besoin metier

Dans le cadre de l'application de duplicatas d'impots, on veut que le endpoint `/actuator/health` indique si le module de generation de duplicatas est operationnel.

Regle choisie pour le TP :

- si la base contient au moins un duplicata : statut `UP` ;
- si la base ne contient aucun duplicata : statut `DOWN` ;
- si la base contient un volume anormalement eleve pour le TP : statut custom `DEGRADED` ;
- si la base est inaccessible : statut `DOWN`.

Le but n'est pas de definir une vraie regle de production, mais de montrer comment brancher un indicateur de sante sur une logique metier.

---

## Classe ajoutee

Fichier :

```text
src/main/java/com/formation/actuator/DuplicataHealthIndicator.java
```

Code principal :

```java
@Component
public class DuplicataHealthIndicator implements HealthIndicator {

    private static final long SEUIL_ALERTE_VOLUME = 1000;

    private final DuplicataRepository duplicataRepository;

    public DuplicataHealthIndicator(DuplicataRepository duplicataRepository) {
        this.duplicataRepository = duplicataRepository;
    }

    @Override
    public Health health() {
        try {
            long nombreDuplicatas = duplicataRepository.count();

            if (nombreDuplicatas == 0) {
                return Health.down()
                        .withDetail("message", "Aucun duplicata n'est present en base")
                        .withDetail("nombreDuplicatas", nombreDuplicatas)
                        .withDetail("action", "Verifier l'initialisation de data.sql ou generer un premier duplicata")
                        .build();
            }

            if (nombreDuplicatas > SEUIL_ALERTE_VOLUME) {
                return Health.status("DEGRADED")
                        .withDetail("message", "Le nombre de duplicatas est anormalement eleve pour le TP")
                        .withDetail("nombreDuplicatas", nombreDuplicatas)
                        .withDetail("seuilAlerteVolume", SEUIL_ALERTE_VOLUME)
                        .build();
            }

            return Health.up()
                    .withDetail("message", "Le module de generation de duplicatas est operationnel")
                    .withDetail("nombreDuplicatas", nombreDuplicatas)
                    .build();
        } catch (Exception exception) {
            return Health.down(exception)
                    .withDetail("message", "Impossible d'interroger la base des duplicatas")
                    .build();
        }
    }
}
```

---

## Pourquoi le composant s'appelle `duplicata` dans Actuator ?

Le bean s'appelle :

```text
duplicataHealthIndicator
```

Spring Boot retire automatiquement le suffixe `HealthIndicator`.

Dans Actuator, le composant apparait donc sous le nom :

```text
duplicata
```

Endpoint direct :

```text
GET http://localhost:8080/actuator/health/duplicata
```

---

# Partie 5 - Tester la customisation du health

## Cas 1 - Application operationnelle

Au demarrage, `data.sql` insere des duplicatas.

Appeler :

```text
GET http://localhost:8080/actuator/health/duplicata
```

Reponse attendue :

```json
{
  "status": "UP",
  "details": {
    "message": "Le module de generation de duplicatas est operationnel",
    "nombreDuplicatas": 3
  }
}
```

Le nombre exact peut varier selon les donnees presentes dans `data.sql`.

---

## Cas 2 - Plus aucun duplicata

Supprimer tous les duplicatas via l'interface web ou les endpoints REST.

Puis appeler :

```text
GET http://localhost:8080/actuator/health/duplicata
```

Reponse attendue :

```json
{
  "status": "DOWN",
  "details": {
    "message": "Aucun duplicata n'est present en base",
    "nombreDuplicatas": 0,
    "action": "Verifier l'initialisation de data.sql ou generer un premier duplicata"
  }
}
```

Creer ensuite un nouveau duplicata :

```text
POST http://localhost:8080/duplicatas
Content-Type: application/json

{
  "userId": "u001",
  "montant": 1200
}
```

Puis rappeler :

```text
GET http://localhost:8080/actuator/health/duplicata
```

Le statut repasse a `UP`.

---

# Partie 6 - Liveness et readiness

Les probes sont activees avec :

```properties
management.endpoint.health.probes.enabled=true
```

Endpoints disponibles :

```text
GET http://localhost:8080/actuator/health/liveness
GET http://localhost:8080/actuator/health/readiness
```

Explication simple :

- `liveness` : l'application est-elle vivante ?
- `readiness` : l'application est-elle prete a recevoir du trafic ?

Ces endpoints sont utiles dans des environnements comme Docker, Kubernetes ou des plateformes cloud.

---

# Partie 7 - Actuator et le cache

Comme le TP precedent a ajoute Spring Cache, Actuator permet maintenant de consulter les caches.

Lister les caches :

```text
GET http://localhost:8080/actuator/caches
```

Consulter un cache precis :

```text
GET http://localhost:8080/actuator/caches/duplicatas
GET http://localhost:8080/actuator/caches/duplicataParId
```

Vider les caches avec les endpoints pedagogiques du projet :

```text
DELETE http://localhost:8080/api/cache
DELETE http://localhost:8080/api/cache/duplicatas
DELETE http://localhost:8080/api/cache/duplicata-par-id/dup-001
```

Remarque importante : Actuator permet d'observer les caches, mais les endpoints de flush ont ete volontairement codes dans `CacheControlleur` pour montrer l'utilisation de `CacheManager`.

---

# Partie 8 - Securite des endpoints Actuator

Pendant le TP, on expose volontairement beaucoup d'endpoints :

```properties
management.endpoints.web.exposure.include=health,info,metrics,caches,beans,mappings,env,loggers
```

En production, c'est dangereux si ce n'est pas protege.

Certains endpoints peuvent donner des informations sensibles :

- variables d'environnement ;
- beans Spring ;
- mappings HTTP ;
- configuration ;
- logs ;
- metriques internes.

Bonne pratique de production :

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

Et proteger `/actuator/**` avec Spring Security.

Ce point sera reutilisable dans le TP sur la securite.

---

# Exercices proposes

## Exercice 1 - Ajouter Actuator

1. Ajouter `spring-boot-starter-actuator` dans `pom.xml`.
2. Redemarrer l'application.
3. Ouvrir `http://localhost:8080/actuator`.
4. Constater que certains endpoints apparaissent automatiquement.

## Exercice 2 - Exposer plus d'endpoints

1. Ajouter :

```properties
management.endpoints.web.exposure.include=health,info,metrics,caches,beans,mappings
```

2. Tester :

```text
/actuator/health
/actuator/info
/actuator/metrics
/actuator/caches
/actuator/mappings
```

## Exercice 3 - Enrichir `/actuator/info`

Ajouter dans `application.properties` :

```properties
management.info.env.enabled=true
info.app.name=Duplicata Impots
info.app.version=12-actuator
info.app.training.module=Spring Boot Actuator
```

Puis tester :

```text
GET http://localhost:8080/actuator/info
```

## Exercice 4 - Creer un HealthIndicator metier

1. Creer le package :

```text
com.formation.actuator
```

2. Creer la classe :

```text
DuplicataHealthIndicator
```

3. Injecter `DuplicataRepository`.
4. Implementer `HealthIndicator`.
5. Renvoyer `UP` si la base contient au moins un duplicata.
6. Renvoyer `DOWN` sinon.
7. Tester :

```text
GET http://localhost:8080/actuator/health/duplicata
```

## Exercice 5 - Ajouter un statut custom

Ajouter un statut `DEGRADED` si le nombre de duplicatas depasse un seuil.

Ajouter ensuite :

```properties
management.endpoint.health.status.order=down,out-of-service,degraded,unknown,up
management.endpoint.health.status.http-mapping.degraded=200
```

## Exercice 6 - Relier Actuator au TP cache

1. Appeler plusieurs fois :

```text
GET http://localhost:8080/duplicatas
GET http://localhost:8080/duplicatas/dup-001
```

2. Observer les caches :

```text
GET http://localhost:8080/actuator/caches
```

3. Vider un cache :

```text
DELETE http://localhost:8080/api/cache/duplicatas
```

4. Reconsulter Actuator.

---

# Ce que Spring Boot fait pour nous

Avec Actuator, Spring Boot :

- cree automatiquement les endpoints `/actuator/*` ;
- detecte la base de donnees et ajoute un health check `db` ;
- detecte l'espace disque et ajoute `diskSpace` ;
- expose des metriques JVM, HTTP, Tomcat et applicatives ;
- expose les caches Spring ;
- permet d'ajouter facilement des indicateurs metier via `HealthIndicator` ;
- permet de configurer l'exposition des endpoints avec quelques proprietes.

Sans Spring Boot, il faudrait coder ou assembler une grande partie de cette infrastructure a la main.

---

# Resultat attendu du TP

- expliquer le role de Spring Boot Actuator ;
- exposer les endpoints utiles ;
- consulter l'etat de sante global de l'application ;
- lire les metriques principales ;
- observer les caches ;
- creer un endpoint de health metier ;
- expliquer pourquoi les endpoints Actuator doivent etre securises en production.
