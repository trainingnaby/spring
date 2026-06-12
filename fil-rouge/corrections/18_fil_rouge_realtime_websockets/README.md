# TP 18 — Application temps réel avec Spring WebSocket, STOMP et monitoring

## 1. Objectif du TP

Ce TP enrichit l'application fil rouge de génération de duplicatas d'impôts avec une vraie **console temps réel**.

Jusqu'ici, l'application permettait de créer, consulter et supprimer des duplicatas. Dans ce TP, on ajoute un **centre de supervision** qui affiche sans rafraîchissement de page :

- les duplicatas créés ou supprimés ;
- les connexions et déconnexions WebSocket ;
- le nombre d'utilisateurs connectés ;
- les messages envoyés par le serveur ;
- des notifications privées destinées à un utilisateur précis ;
- un monitoring REST et Actuator des connexions temps réel.

Le TP permet de couvrir les notions suivantes :

- architecture d'une application temps réel ;
- WebSocket, SockJS et STOMP ;
- gestion des sessions WebSocket ;
- broadcasting ;
- messages ciblés ;
- reconnexion automatique côté navigateur ;
- interface utilisateur réactive ;
- monitoring et supervision des connexions.

---

## 2. Import du projet dans Eclipse

1. Ouvrir Eclipse.
2. Menu `File` > `Import`.
3. Choisir `Maven` > `Existing Maven Projects`.
4. Sélectionner le dossier du projet.
5. Vérifier que le fichier `pom.xml` est détecté.
6. Cliquer sur `Finish`.
7. Faire ensuite : clic droit sur le projet > `Maven` > `Update Project`.

Le projet utilise Java 17.

---

## 3. Lancer l'application

Depuis Eclipse :

1. Ouvrir la classe :

```text
com.formation.DuplicataImpotsApplication
```

2. Clic droit > `Run As` > `Java Application`.

L'application démarre sur :

```text
http://localhost:8080
```

Comptes locaux disponibles :

```text
admin / admin
user  / user
```

Le compte `admin` permet de créer et supprimer des duplicatas.
Le compte `user` permet seulement de consulter.

---

## 4. Pages utiles

### Page principale des duplicatas

```text
http://localhost:8080/ui/duplicatas
```

Cette page affiche la liste des duplicatas et reçoit déjà les notifications simples de création/suppression.

### Dashboard temps réel

```text
http://localhost:8080/ui/dashboard
```

C'est la nouvelle page principale du TP.

Elle affiche :

- l'état de la connexion WebSocket ;
- les statistiques temps réel ;
- les événements métier ;
- les événements système ;
- les notifications privées ;
- les sessions WebSocket actives.

### Console H2

```text
http://localhost:8080/h2-console
```

Paramètres :

```text
JDBC URL : jdbc:h2:mem:duplicatasdb
User     : sa
Password :
```

### Actuator Health WebSocket

```text
http://localhost:8080/actuator/health/websocket
```

### Monitoring REST WebSocket

```text
http://localhost:8080/api/ws-monitoring/stats
http://localhost:8080/api/ws-monitoring/sessions
```

---

## 5. Rappel conceptuel : WebSocket

HTTP classique fonctionne principalement en mode **requête/réponse** :

```text
Navigateur -> Serveur : donne-moi la liste des duplicatas
Serveur    -> Navigateur : voici la liste
```

Si une autre personne crée un duplicata, le navigateur ne le sait pas automatiquement. Il doit refaire une requête ou rafraîchir la page.

WebSocket ouvre une connexion durable entre le navigateur et le serveur :

```text
Navigateur <==============================> Serveur
```

Une fois la connexion ouverte, le serveur peut pousser des messages vers le navigateur sans attendre une nouvelle requête HTTP.

Dans ce TP, cela permet d'afficher immédiatement :

```text
Nouveau duplicata créé
Utilisateur admin connecté
Utilisateur user déconnecté
Nombre d'utilisateurs connectés : 3
```

---

## 6. Pourquoi STOMP et SockJS ?

### WebSocket natif

WebSocket seul fournit un canal de communication, mais il ne définit pas de modèle applicatif haut niveau.

### STOMP

STOMP ajoute un modèle de messages avec des destinations :

```text
/topic/duplicatas
/topic/system
/topic/statistiques
/user/queue/notifications
```

On peut donc s'abonner à un canal, envoyer un message vers une destination et organiser proprement l'application.

### SockJS

SockJS facilite les tests côté navigateur et fournit des mécanismes de fallback si WebSocket natif n'est pas disponible.

Dans le projet, les bibliothèques JavaScript sont fournies par WebJars :

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

Et les pages les chargent ainsi :

```html
<script src="/webjars/sockjs-client/1.5.1/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/2.3.4/stomp.min.js"></script>
```

---

## 7. Architecture mise en place

### Configuration WebSocket

Classe :

```text
com.formation.config.WebSocketConfig
```

Elle déclare :

```java
registry.enableSimpleBroker("/topic", "/queue");
registry.setApplicationDestinationPrefixes("/app");
registry.setUserDestinationPrefix("/user");
```

Signification :

| Préfixe | Rôle |
|---|---|
| `/topic` | diffusion à tous les abonnés |
| `/queue` | messages point-à-point |
| `/app` | messages envoyés du client vers un contrôleur WebSocket |
| `/user` | messages ciblés vers un utilisateur précis |

Endpoint de connexion :

```java
registry.addEndpoint("/ws-duplicatas")
        .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
        .withSockJS();
```

Le navigateur se connecte donc à :

```javascript
new SockJS('/ws-duplicatas')
```

---

## 8. Exercice 1 — Configurer WebSocket/STOMP

Objectif : comprendre le rôle de `WebSocketConfig`.

À réaliser par les stagiaires :

1. Ajouter la dépendance Maven :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

2. Créer la classe `WebSocketConfig`.
3. Activer le broker simple avec `/topic` et `/queue`.
4. Déclarer le endpoint `/ws-duplicatas`.
5. Vérifier dans le navigateur que l'appel réseau vers `/ws-duplicatas` ne retourne pas 404.

Corrigé : voir `com.formation.config.WebSocketConfig`.

---

## 9. Exercice 2 — Créer un dashboard temps réel

Objectif : créer une interface utilisateur réactive.

Page créée :

```text
src/main/resources/templates/dashboard.html
```

Contrôleur MVC :

```text
com.formation.mvc.DashboardController
```

Route :

```text
GET /ui/dashboard
```

Le dashboard contient quatre zones principales :

1. statistiques ;
2. événements duplicatas ;
3. événements système ;
4. notifications privées.

Le JavaScript associé se trouve ici :

```text
src/main/resources/static/js/realtime-dashboard.js
```

---

## 10. Exercice 3 — Broadcasting des événements métier

Le broadcasting consiste à envoyer un message à tous les clients abonnés.

Dans le projet, lorsqu'un duplicata est créé ou supprimé, le service métier appelle :

```text
com.formation.websocket.DuplicataWebSocketNotifier
```

Exemple :

```java
messagingTemplate.convertAndSend("/topic/duplicatas", notification);
```

Tous les navigateurs abonnés à `/topic/duplicatas` reçoivent l'événement.

Côté JavaScript :

```javascript
stompClient.subscribe('/topic/duplicatas', function (message) {
    const event = JSON.parse(message.body);
    // mise à jour de la page
});
```

Test :

1. Ouvrir deux navigateurs sur `/ui/dashboard`.
2. Dans un autre onglet, aller sur `/ui/duplicatas`.
3. Créer un duplicata avec le compte `admin`.
4. Observer que les dashboards reçoivent l'événement sans refresh.

---

## 11. Exercice 4 — Messages ciblés

Un message ciblé est envoyé à un seul utilisateur.

Exemple de destination côté client :

```javascript
stompClient.subscribe('/user/queue/notifications', function (message) {
    const notification = JSON.parse(message.body);
});
```

Côté serveur :

```java
messagingTemplate.convertAndSendToUser(
    username,
    "/queue/notifications",
    notification
);
```

Dans le projet, le service :

```text
com.formation.websocket.realtime.RealtimeNotificationService
```

expose la méthode :

```java
sendToUser(String username, UserNotification notification)
```

Important : avec Spring Security, le `username` correspond au nom de l'utilisateur authentifié dans la session.

Dans notre application, les utilisateurs locaux sont :

```text
admin
user
```

Les duplicatas de démonstration utilisent aussi des `userId`. Pour voir facilement une notification privée, créez un duplicata avec un `userId` correspondant à un utilisateur connecté, par exemple :

```text
user
```

si vous êtes connecté avec le compte `user` dans un autre navigateur.

---

## 12. Exercice 5 — Gestion des sessions WebSocket

Objectif : suivre les connexions actives.

Classes importantes :

```text
com.formation.websocket.realtime.SessionInfo
com.formation.websocket.realtime.SessionRegistryService
com.formation.websocket.realtime.WebSocketEventListener
```

`SessionRegistryService` conserve les sessions dans une map :

```java
Map<String, SessionInfo>
```

À chaque connexion WebSocket, Spring publie un événement :

```java
SessionConnectEvent
```

À chaque déconnexion :

```java
SessionDisconnectEvent
```

Le listener met à jour la liste des sessions et diffuse un message système.

Test :

1. Ouvrir `/ui/dashboard` dans deux navigateurs.
2. Observer le nombre d'utilisateurs connectés.
3. Fermer un onglet.
4. Observer la déconnexion dans le flux système.

---

## 13. Exercice 6 — Gestion des déconnexions et reconnexions

Le fichier :

```text
src/main/resources/static/js/realtime-dashboard.js
```

contient une logique simple de reconnexion :

```javascript
function scheduleReconnect() {
    reconnectAttempts++;
    const delay = Math.min(5000 * reconnectAttempts, 30000);
    setStatus(`Connexion perdue. Reconnexion dans ${delay / 1000}s...`, 'error');
    reconnectTimer = setTimeout(connect, delay);
}
```

Principe :

- première tentative après 5 secondes ;
- puis 10 secondes ;
- puis 15 secondes ;
- maximum 30 secondes.

Test :

1. Ouvrir `/ui/dashboard`.
2. Arrêter l'application Spring Boot.
3. Observer le message : `Connexion perdue`.
4. Relancer l'application.
5. Observer la reconnexion automatique.

---

## 14. Exercice 7 — Interface utilisateur réactive

Une interface réactive ne recharge pas toute la page. Elle met à jour seulement les parties nécessaires.

Dans ce TP :

- les compteurs sont mis à jour par `/topic/statistiques` ;
- les événements duplicatas sont ajoutés dans une liste ;
- les événements système apparaissent en temps réel ;
- les notifications privées s'ajoutent uniquement pour l'utilisateur concerné.

Côté JavaScript, la fonction suivante ajoute un événement sans refresh :

```javascript
function prependItem(listId, html) {
    const list = document.getElementById(listId);
    const li = document.createElement('li');
    li.innerHTML = html;
    list.prepend(li);
}
```

---

## 15. Exercice 8 — Monitoring REST

Contrôleur :

```text
com.formation.websocket.realtime.WebSocketMonitoringController
```

Endpoints :

```text
GET /api/ws-monitoring/stats
GET /api/ws-monitoring/sessions
```

Exemple de réponse :

```json
{
  "connectedUsers": 2,
  "totalConnections": 5,
  "totalDisconnections": 3,
  "messagesSent": 12
}
```

Ces endpoints permettent de comprendre qu'une application temps réel doit aussi être observable par des outils externes.

---

## 16. Exercice 9 — Supervision avec Actuator

Classe :

```text
com.formation.actuator.WebSocketHealthIndicator
```

Endpoint :

```text
http://localhost:8080/actuator/health/websocket
```

Exemple :

```json
{
  "status": "UP",
  "details": {
    "connectedUsers": 2,
    "totalConnections": 8,
    "totalDisconnections": 6,
    "messagesSent": 20
  }
}
```

Cela montre comment intégrer un composant applicatif dans la supervision Spring Boot Actuator.

---

## 17. Organisation des packages ajoutés

```text
com.formation.config
 └── WebSocketConfig

com.formation.mvc
 └── DashboardController

com.formation.websocket
 ├── DuplicataNotification
 ├── DuplicataWebSocketController
 └── DuplicataWebSocketNotifier

com.formation.websocket.realtime
 ├── DashboardStats
 ├── DashboardWebSocketController
 ├── RealtimeNotificationService
 ├── SessionInfo
 ├── SessionRegistryService
 ├── SystemMessage
 ├── UserNotification
 ├── WebSocketEventListener
 ├── WebSocketMonitoringController
 └── WebSocketMonitoringService

com.formation.actuator
 └── WebSocketHealthIndicator
```

---

## 18. Scénario de demo

1. Lancer l'application.
2. Se connecter avec `admin/admin`.
3. Ouvrir `/ui/dashboard`.
4. Ouvrir un autre navigateur ou une fenêtre privée avec `user/user`.
5. Aller aussi sur `/ui/dashboard`.
6. Observer le compteur d'utilisateurs connectés.
7. Avec `admin`, créer un duplicata pour `user`.
8. Observer :
   - un broadcast dans le flux métier ;
   - un événement système ;
   - une notification privée côté utilisateur `user`.
9. Fermer un navigateur.
10. Observer la déconnexion et la mise à jour des statistiques.
11. Consulter `/actuator/health/websocket`.

---

## 19. Points importants

- WebSocket ne remplace pas REST : il complète REST pour les événements temps réel.
- REST reste adapté aux opérations métier classiques : créer, consulter, supprimer.
- WebSocket est adapté aux notifications, tableaux de bord, chats, supervision, alertes.
- STOMP structure les échanges avec des destinations.
- `/topic` sert au broadcast.
- `/user/queue` sert au message ciblé.
- Une application temps réel doit gérer les déconnexions.
- Une application temps réel doit être monitorée.

---

## 20. Problèmes fréquents

### 404 sur SockJS ou STOMP

Vérifier que les dépendances WebJars existent dans `pom.xml` :

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

Et que les scripts utilisent les URLs versionnées :

```html
/webjars/sockjs-client/1.5.1/sockjs.min.js
/webjars/stomp-websocket/2.3.4/stomp.min.js
```

### Le dashboard reste sur "Connexion en cours"

Vérifier :

- que l'application est bien démarrée ;
- que `/ws-duplicatas` n'est pas bloqué par Spring Security ;
- que `/webjars/**` est autorisé dans `SecurityConfig` ;
- que la console navigateur ne contient pas de 404 sur les scripts.

### Les messages privés ne s'affichent pas

Vérifier que le `userId` du duplicata correspond au nom de l'utilisateur connecté.

Exemple :

- utilisateur connecté : `user` ;
- duplicata créé pour : `user`.

---

## 21. Ce que Spring Boot configure encore pour nous

Même si ce TP montre beaucoup de code applicatif, Spring Boot continue à simplifier :

- le serveur Tomcat embarqué ;
- la configuration MVC ;
- Jackson JSON ;
- Thymeleaf ;
- Spring Security ;
- Actuator ;
- les WebJars statiques ;
- les propriétés applicatives.

L'objectif pédagogique est de montrer que Boot configure l'infrastructure, mais que l'architecture temps réel reste une responsabilité applicative.
