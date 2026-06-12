# TP 17 - Intégration des WebSockets dans l'application de duplicatas d'impôts

## 1. Objectif du TP

Dans les TP précédents, l'application de génération de duplicatas d'impôts fonctionnait principalement selon un modèle classique :

```text
le navigateur envoie une requête HTTP
le serveur traite la demande
le serveur renvoie une réponse
la communication est terminée
```

Ce modèle convient très bien pour :

- afficher la liste des duplicatas ;
- créer un duplicata via un formulaire ;
- consulter un duplicata ;
- supprimer un duplicata ;
- appeler une API REST.

Mais il ne permet pas facilement au serveur de prévenir spontanément les navigateurs qu'un événement vient de se produire.

Dans ce TP, on ajoute donc une communication temps réel avec Spring WebSocket.

Cas métier retenu :

> Lorsqu'un duplicata est créé ou supprimé, tous les utilisateurs connectés à la page de liste reçoivent immédiatement une notification sans recharger la page.

---

## 2. Rappel : qu'est-ce qu'un WebSocket ?

HTTP classique est basé sur une logique de requête/réponse :

```text
Client -> Serveur : donne-moi la liste des duplicatas
Serveur -> Client : voici la liste
```

Une fois la réponse reçue, la connexion est terminée.

WebSocket permet d'ouvrir une connexion persistante entre le navigateur et le serveur :

```text
Client <==============================> Serveur
```

Une fois cette connexion ouverte :

- le client peut envoyer des messages au serveur ;
- le serveur peut envoyer des messages au client ;
- le serveur peut pousser une information sans attendre une nouvelle requête HTTP.

C'est utile pour :

- notifications temps réel ;
- messagerie instantanée ;
- suivi d'un traitement long ;
- tableau de bord dynamique ;
- collaboration en direct ;
- supervision.

Dans notre projet, on utilise WebSocket pour notifier les créations et suppressions de duplicatas.

---

## 3. WebSocket simple vs STOMP

Spring peut gérer des WebSockets bas niveau, mais cela oblige à manipuler directement les sessions et les messages.

Dans ce TP, on utilise **STOMP** au-dessus de WebSocket.

STOMP est un protocole de messagerie simple qui introduit des notions proches de la messagerie :

```text
subscribe  -> je m'abonne à un canal
send       -> j'envoie un message
message    -> je reçois un message
```

Dans notre application :

```text
/topic/duplicatas
```

est le canal sur lequel les navigateurs s'abonnent pour recevoir les notifications.

---

## 4. Ce que le TP ajoute au projet

Ce corrigé ajoute :

- la dépendance `spring-boot-starter-websocket` ;
- une configuration WebSocket/STOMP ;
- un endpoint de connexion WebSocket `/ws-duplicatas` ;
- un topic de diffusion `/topic/duplicatas` ;
- un objet `DuplicataNotification` ;
- un composant `DuplicataWebSocketNotifier` ;
- une notification lors de la création d'un duplicata ;
- une notification lors de la suppression d'un duplicata ;
- un client JavaScript dans la page Thymeleaf de liste ;
- une section visuelle `Notifications temps réel` dans `/ui/duplicatas`.

---

## 5. Dépendances Maven ajoutées

Dans `pom.xml`, on ajoute :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

Pour le navigateur, on utilise SockJS et STOMP via WebJars :

```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>sockjs-client</artifactId>
    <version>1.5.1</version>
</dependency>

<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>stomp-websocket</artifactId>
    <version>2.3.4</version>
</dependency>
```

SockJS permet d'avoir une couche de compatibilité côté navigateur.

STOMP permet de s'abonner à des topics et de recevoir des messages structurés.

---

## 6. Configuration WebSocket côté Spring

Classe ajoutée :

```text
src/main/java/com/formation/config/WebSocketConfig.java
```

Elle contient :

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-duplicatas")
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .withSockJS();
    }
}
```

Explications :

### `@EnableWebSocketMessageBroker`

Active le support WebSocket/STOMP dans Spring.

### `/ws-duplicatas`

C'est l'URL de connexion WebSocket.

Le navigateur se connecte à :

```text
/ws-duplicatas
```

### `/topic`

C'est le préfixe des destinations de diffusion.

Dans ce TP, on diffuse sur :

```text
/topic/duplicatas
```

### `/app`

C'est le préfixe des messages envoyés par le client vers le serveur.

Exemple pédagogique inclus :

```text
/app/duplicatas/ping
```

---

## 7. Objet envoyé aux navigateurs

Classe ajoutée :

```text
src/main/java/com/formation/websocket/DuplicataNotification.java
```

Elle représente le message envoyé en JSON aux navigateurs :

```json
{
  "type": "CREATION",
  "message": "Un nouveau duplicata a été généré pour l'utilisateur FR_123456789",
  "duplicataId": "dup-1710000000000",
  "userId": "FR_123456789",
  "montant": 2500,
  "dateNotification": "2026-06-10T10:30:00"
}
```

Deux types de notification sont utilisés :

```text
CREATION
SUPPRESSION
```

---

## 8. Envoi des notifications côté serveur

Classe ajoutée :

```text
src/main/java/com/formation/websocket/DuplicataWebSocketNotifier.java
```

Elle utilise :

```java
SimpMessagingTemplate
```

Ce composant Spring permet d'envoyer un message vers une destination STOMP.

Exemple :

```java
messagingTemplate.convertAndSend("/topic/duplicatas", notification);
```

Dans notre projet :

```java
public void notifierCreation(Duplicata duplicata) {
    messagingTemplate.convertAndSend(TOPIC_DUPLICATAS, DuplicataNotification.creation(duplicata));
}

public void notifierSuppression(Duplicata duplicata) {
    messagingTemplate.convertAndSend(TOPIC_DUPLICATAS, DuplicataNotification.suppression(duplicata));
}
```

---

## 9. Modification du service métier

La classe modifiée est :

```text
src/main/java/com/formation/service/DuplicataService.java
```

On injecte le notifier :

```java
private final DuplicataWebSocketNotifier webSocketNotifier;
```

Lorsqu'un duplicata est créé :

```java
Duplicata duplicataCree = duplicataRepository.save(duplicata);
webSocketNotifier.notifierCreation(duplicataCree);
return duplicataCree;
```

Lorsqu'un duplicata est supprimé :

```java
Duplicata duplicata = getById(id);
duplicataRepository.deleteById(id);
webSocketNotifier.notifierSuppression(duplicata);
```

Cela montre un point important :

> Le contrôleur ne s'occupe pas du WebSocket. Il appelle le service métier. Le service déclenche ensuite une notification métier.

---

## 10. Client JavaScript côté navigateur

Fichier ajouté :

```text
src/main/resources/static/js/duplicatas-websocket.js
```

Il fait trois choses :

1. connexion au endpoint WebSocket ;
2. abonnement au topic `/topic/duplicatas` ;
3. affichage dynamique des notifications reçues.

Extrait :

```javascript
const socket = new SockJS('/ws-duplicatas');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function () {
    stompClient.subscribe('/topic/duplicatas', function (message) {
        ajouterNotification(JSON.parse(message.body));
    });
});
```

---

## 11. Modification de la page Thymeleaf

Page modifiée :

```text
src/main/resources/templates/duplicatas/list.html
```

Une section a été ajoutée :

```html
<section class="card websocket-panel">
    <div class="websocket-header">
        <div>
            <h2>Notifications temps réel</h2>
            <p>Cette zone est alimentée par WebSocket/STOMP lorsqu'un duplicata est créé ou supprimé.</p>
        </div>
        <span id="ws-status" class="badge">Connexion en cours...</span>
    </div>
    <ul id="ws-notifications" class="ws-list">
        <li class="empty">Aucune notification reçue pour le moment.</li>
    </ul>
</section>
```

Et les scripts sont chargés en bas de page :

```html
<script src="/webjars/sockjs-client/1.5.1/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/2.3.4/stomp.min.js"></script>
<script th:src="@{/js/duplicatas-websocket.js}"></script>
```

---

## 12. Sécurité appliquée dans ce TP

Le projet contient déjà :

- sécurité front Thymeleaf ;
- OAuth2 GitHub ;
- JWT pour l'API REST.

Pour garder le TP WebSocket simple, le endpoint WebSocket est autorisé dans `SecurityConfig` :

```java
.requestMatchers("/ws-duplicatas/**").permitAll()
```

et exclu du CSRF :

```java
.ignoringRequestMatchers("/h2-console/**", "/ws-duplicatas/**")
```

Cela évite d'introduire immédiatement la sécurisation fine des messages STOMP.

Point important:

> En production, on sécurise généralement aussi la connexion WebSocket et les messages STOMP. Ici, on se concentre uniquement sur le mécanisme WebSocket.

---

## 13. Importer le projet dans Eclipse

### Étape 1

Ouvrir Eclipse.

### Étape 2

Choisir :

```text
File > Import...
```

### Étape 3

Choisir :

```text
Maven > Existing Maven Projects
```

### Étape 4

Sélectionner le dossier du projet :

```text
17_fil_rouge_websockets_corrige
```

### Étape 5

Cliquer sur :

```text
Finish
```

### Étape 6

Faire un update Maven :

```text
Clic droit projet
> Maven
> Update Project
> cocher Force Update of Snapshots/Releases
> OK
```

---

## 14. Lancer l'application

Depuis Eclipse, lancer la classe :

```text
com.formation.DuplicataImpotsApplication
```

ou en ligne de commande :

```bash
mvn spring-boot:run
```

Si vous testez GitHub OAuth2, définir le secret dans une variable d'environnement :

```bash
export GITHUB_CLIENT_SECRET="votre-secret-github"
```

Sous PowerShell :

```powershell
$env:GITHUB_CLIENT_SECRET="votre-secret-github"
```

---

## 15. Comptes de test locaux

Deux comptes locaux sont disponibles :

```text
user / user
admin / admin
```

Le compte `user` peut consulter les duplicatas.

Le compte `admin` peut créer et supprimer des duplicatas.

---

## 16. Tester le TP WebSocket

### Étape 1 : ouvrir une première fenêtre

Aller sur :

```text
http://localhost:8080/ui/duplicatas
```

Se connecter avec :

```text
admin / admin
```

Vérifier que la zone suivante apparaît :

```text
Notifications temps réel
```

Le badge doit afficher :

```text
Connecté aux notifications temps réel
```

### Étape 2 : ouvrir une deuxième fenêtre

Ouvrir une autre fenêtre ou un autre navigateur et aller aussi sur :

```text
http://localhost:8080/ui/duplicatas
```

Se connecter aussi.

### Étape 3 : créer un duplicata

Dans une fenêtre, cliquer sur :

```text
Générer un duplicata
```

Créer un duplicata avec par exemple :

```text
Identifiant fiscal : 123456789
Montant : 2500
```

### Résultat attendu

Dans l'autre fenêtre, sans recharger la page, une notification apparaît :

```text
CREATION - Un nouveau duplicata a été généré pour l'utilisateur FR_123456789
```

### Étape 4 : supprimer un duplicata

Supprimer un duplicata depuis une fenêtre.

### Résultat attendu

L'autre fenêtre reçoit :

```text
SUPPRESSION - Le duplicata dup-... a été supprimé.
```

---

## 17. Tester avec les outils navigateur

Dans Chrome ou Edge :

```text
F12 > Network > WS
```

ou :

```text
F12 > Network
```

Puis filtrer sur :

```text
ws-duplicatas
```

Avec SockJS, vous pouvez voir plusieurs requêtes techniques :

```text
/ws-duplicatas/info
/ws-duplicatas/.../websocket
```

C'est normal : SockJS négocie le meilleur transport disponible.

---

## 18. Exercice 1

Objectif : comprendre le chemin complet d'une notification.

Demander aux stagiaires de retrouver dans le code :

1. l'endroit où le duplicata est créé ;
2. l'endroit où la notification est envoyée ;
3. le topic utilisé ;
4. le JavaScript qui reçoit le message ;
5. l'endroit où le message est ajouté dans le HTML.

Réponse attendue :

```text
DuplicataService.createDuplicata
-> DuplicataWebSocketNotifier.notifierCreation
-> /topic/duplicatas
-> duplicatas-websocket.js
-> ul#ws-notifications
```

---

## 19. Exercice 2

Objectif : enrichir la notification.

Demander aux stagiaires d'ajouter le champ suivant dans `DuplicataNotification` :

```java
private String pdfUrl;
```

Puis modifier la notification de création pour envoyer aussi l'URL du PDF.

Enfin, modifier le JavaScript pour afficher un lien vers le PDF.

---

## 20. Exercice 3

Objectif : créer un nouveau topic.

Créer un topic spécifique pour les suppressions :

```text
/topic/duplicatas/suppressions
```

Puis :

- envoyer les suppressions sur ce topic ;
- abonner le navigateur à ce deuxième topic ;
- afficher les suppressions dans une couleur différente.

---

## 21. Exercice 4

Objectif : envoyer un message du client vers le serveur.

Le projet contient un contrôleur pédagogique :

```text
DuplicataWebSocketController
```

Il expose :

```text
/app/duplicatas/ping
```

Les stagiaires peuvent ajouter un bouton dans la page qui envoie :

```javascript
stompClient.send('/app/duplicatas/ping', {}, JSON.stringify({ source: 'navigateur' }));
```

Et écouter la réponse sur :

```text
/topic/duplicatas/debug
```

Cela permet d'illustrer la communication client -> serveur -> clients.

---

## 22. Points à retenir

### HTTP REST

REST est idéal pour demander une ressource ou exécuter une action ponctuelle.

Exemple :

```text
GET /duplicatas
POST /duplicatas_dto
DELETE /duplicatas/{id}
```

### WebSocket

WebSocket est utile lorsque le serveur doit pousser une information au client.

Exemple :

```text
Un duplicata vient d'être créé
Un traitement est terminé
Une alerte métier vient d'apparaître
```

### STOMP

STOMP apporte une abstraction de messagerie :

```text
/topic/duplicatas
```

Le navigateur s'abonne à ce topic et reçoit les messages publiés par le serveur.

---

## 23. URLs utiles

Application front :

```text
http://localhost:8080/ui/duplicatas
```

Login :

```text
http://localhost:8080/login
```

Swagger UI :

```text
http://localhost:8080/swagger-ui.html
```

Console H2 :

```text
http://localhost:8080/h2-console
```

Actuator health :

```text
http://localhost:8080/actuator/health
```

Endpoint WebSocket :

```text
/ws-duplicatas
```

Topic des notifications :

```text
/topic/duplicatas
```

---

## 24. Résumé

- expliquer la différence entre HTTP classique et WebSocket ;
- comprendre le rôle de STOMP ;
- configurer un endpoint WebSocket dans Spring ;
- publier un message avec `SimpMessagingTemplate` ;
- s'abonner à un topic côté JavaScript ;
- appliquer le temps réel à un cas métier simple.
