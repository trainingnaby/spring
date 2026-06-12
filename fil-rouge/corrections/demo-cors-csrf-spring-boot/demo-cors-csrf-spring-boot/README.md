# Démo Spring Boot - CORS, Preflight et CSRF

## Objectif

Ce projet contient deux applications Spring Boot pour illustrer concrètement :

- CORS ;
- requête preflight `OPTIONS` ;
- CSRF ;
- cookie de session ;
- Spring Security ;
- différence entre “bloquer la lecture d'une réponse” et “empêcher une action”.

## Applications

```text
demo-cors-csrf-spring-boot
├── api-banque    -> http://localhost:8080
└── site-pirate   -> http://localhost:9090
```

## Identifiants banque

```text
username: alice
password: password
```

## Lancer les applications

Depuis le dossier racine :

```bash
mvn clean install
```

Puis lancer l'API banque :

```bash
cd api-banque
mvn spring-boot:run
```

Dans un second terminal :

```bash
cd site-pirate
mvn spring-boot:run
```

Ouvrir :

```text
http://localhost:8080
```

Se connecter, puis ouvrir :

```text
http://localhost:9090
```

## Modes de sécurité

Dans `api-banque/src/main/resources/application.properties` :

```properties
app.security.mode=csrf-only
```

Valeurs possibles :

```text
csrf-only
insecure
secure
```

Il faut redémarrer `api-banque` après modification.

---

# 1. Mode `csrf-only`

```properties
app.security.mode=csrf-only
```

Dans ce mode :

- CSRF est activé ;
- CORS n'est pas configuré ;
- le faux site ne peut pas lire l'API avec `fetch()` ;
- le formulaire pirate échoue avec `403 Forbidden`.

Tests :

1. Aller sur `http://localhost:8080` et se connecter.
2. Aller sur `http://localhost:9090`.
3. Cliquer sur `Lire le compte banque`.
4. Observer une erreur CORS.
5. Cliquer sur `Tenter virement JSON`.
6. Observer une erreur CORS ou un refus.
7. Cliquer sur `Envoyer un faux virement par formulaire`.
8. Observer `403 Forbidden`.

Conclusion :

```text
CORS bloque la lecture cross-origin.
CSRF bloque l'action POST sans token.
```

---

# 2. Mode `insecure`

```properties
app.security.mode=insecure
```

Dans ce mode :

- CSRF est désactivé ;
- CORS autorise `http://localhost:9090` ;
- les cookies sont autorisés ;
- c'est volontairement dangereux.

Tests :

1. Redémarrer `api-banque`.
2. Se connecter sur `http://localhost:8080`.
3. Aller sur `http://localhost:9090`.
4. Cliquer sur `Lire le compte banque`.
5. Le site pirate lit le JSON du compte.
6. Cliquer sur `Tenter virement JSON`.
7. Le virement peut passer.
8. Cliquer sur `Envoyer un faux virement par formulaire`.
9. Le virement peut passer.

Conclusion :

```text
C'est la configuration à ne pas faire en production.
```

---

# 3. Mode `secure`

```properties
app.security.mode=secure
```

Dans ce mode :

- CSRF est activé ;
- CORS est configuré seulement pour `http://localhost:4200` ;
- le site pirate `http://localhost:9090` n'est pas autorisé.

Tests depuis `http://localhost:9090` :

- lecture du compte : bloquée par CORS ;
- virement JSON : bloqué ;
- formulaire HTML : bloqué par CSRF.

---

# C'est quoi CORS ?

CORS signifie :

```text
Cross-Origin Resource Sharing
```

Une origine est :

```text
protocole + host + port
```

Donc :

```text
http://localhost:8080
http://localhost:9090
```

sont deux origines différentes.

CORS répond à la question :

```text
Ce JavaScript a-t-il le droit de lire la réponse de mon serveur ?
```

CORS est une protection appliquée par le navigateur.

---

# C'est quoi une requête preflight ?

Une preflight est une requête `OPTIONS` envoyée automatiquement par le navigateur avant certaines requêtes cross-origin.

Exemple :

```javascript
fetch("http://localhost:8080/api/virement-json", {
  method: "POST",
  headers: { "Content-Type": "application/json" }
})
```

Le navigateur envoie d'abord :

```http
OPTIONS /api/virement-json
Origin: http://localhost:9090
Access-Control-Request-Method: POST
Access-Control-Request-Headers: content-type
```

Si le serveur ne répond pas avec les bons headers CORS, le vrai POST n'est pas envoyé.

---

# C'est quoi CSRF ?

CSRF signifie :

```text
Cross-Site Request Forgery
```

Le pirate force le navigateur d'un utilisateur connecté à envoyer une requête vers un site où il possède déjà une session.

Exemple :

```html
<form action="http://localhost:8080/virement" method="post">
  <input type="hidden" name="beneficiaire" value="PIRATE">
  <input type="hidden" name="montant" value="100">
</form>
```

Le navigateur ajoute automatiquement le cookie de session de la banque.

Spring Security exige un token CSRF pour les POST.

Si le token est absent :

```text
403 Forbidden
```

---

# Pourquoi CORS ne suffit pas contre CSRF ?

CORS protège surtout la lecture de la réponse par JavaScript.

CSRF protège contre l'exécution d'une action non voulue.

Phrase à retenir :

```text
CORS : est-ce que ce JavaScript peut lire la réponse ?
CSRF : est-ce que cette action vient vraiment de mon application ?
```

Un formulaire HTML cross-origin peut envoyer une requête même si CORS est strict.

Le pirate n'a pas besoin de lire la réponse : il veut seulement déclencher l'action.

---

# Configuration Spring Security

La classe importante est :

```text
api-banque/src/main/java/fr/formation/corscsrf/api/config/SecurityConfig.java
```

Elle montre :

- login formulaire ;
- utilisateur en mémoire ;
- CSRF activé ou désactivé selon le mode ;
- CORS autorisé ou restreint selon le mode.

---

# Fil conducteur pour une formation

1. Expliquer la session et le cookie.
2. Montrer la banque légitime.
3. Montrer le faux site pirate.
4. Tester `csrf-only`.
5. Tester `insecure`.
6. Tester `secure`.
7. Résumer :

```text
CORS protège les lectures cross-origin.
CSRF protège les actions avec cookies de session.
```
