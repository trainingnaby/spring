# TP 16 - Spring Security OAuth2 Login avec GitHub

## Objectif du TP

Ce TP repart du corrigé précédent basé sur Spring Security JWT.

L'objectif est d'ajouter une authentification OAuth2 avec GitHub pour la partie front MVC/Thymeleaf, tout en conservant la sécurisation JWT existante pour l'API REST.

On veut volontairement rester simple :

- le front `/ui/**` accepte maintenant deux modes de connexion : formulaire local ou GitHub ;
- l'API REST continue à utiliser JWT via `/api/auth/login` ;
- un utilisateur GitHub connecté reçoit le rôle `ROLE_USER` ;
- les opérations d'administration restent accessibles avec le compte local `admin/admin` ;
- le secret GitHub n'est pas écrit en dur dans le code source.

---

## Notions à apprendre

À la fin du TP, on doit pouvoir :

- expliquer la différence entre authentification locale, JWT et OAuth2 Login ;
- configurer un client OAuth2 dans Spring Boot ;
- déclarer une application OAuth GitHub ;
- comprendre l'URL de callback OAuth2 ;
- ajouter `spring-boot-starter-oauth2-client` ;
- utiliser `/oauth2/authorization/github` ;
- mapper les rôles d'un utilisateur OAuth2 ;
- comprendre pourquoi il ne faut pas versionner un `client-secret`.

---

## Rappel de l'état du projet avant ce TP

Avant ce TP, le projet contient déjà :

- Spring Boot ;
- Spring MVC + Thymeleaf ;
- Spring Data JPA ;
- H2 ;
- validation ;
- gestion globale d'exceptions ;
- cache ;
- actuator ;
- sécurité front MVC ;
- sécurité REST avec JWT ;
- Swagger/OpenAPI.

Dans ce TP, on ajoute OAuth2 Login avec GitHub uniquement pour la partie front.

---

## 1. Dépendance Maven ajoutée

Dans `pom.xml`, ajouter :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

Cette dépendance apporte :

- les classes OAuth2 Client ;
- les filtres Spring Security nécessaires ;
- le support de `oauth2Login()` ;
- les configurations automatiques Spring Boot.

---

## 2. Configuration GitHub côté GitHub

Aller sur GitHub :

```text
Settings
→ Developer settings
→ OAuth Apps
→ New OAuth App
```

Renseigner par exemple :

```text
Application name:
Duplicata Impots Formation

Homepage URL:
http://localhost:8080

Authorization callback URL:
http://localhost:8080/login/oauth2/code/github
```

L'URL de callback est très importante.

Spring Security utilise par défaut le modèle suivant :

```text
/login/oauth2/code/{registrationId}
```

Ici, le `registrationId` est :

```text
github
```

Donc l'URL complète est :

```text
http://localhost:8080/login/oauth2/code/github
```

---

## 3. Configuration Spring Boot

Dans `application.properties`, le projet contient :

```properties
spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID:Ov23liZfHPLcitSHTJRl}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET:change-me}
spring.security.oauth2.client.registration.github.scope=read:user,user:email
```

Le `client-id` peut être présent dans le projet de formation.

Le `client-secret`, lui, ne doit pas être commité en clair.

Il faut donc le fournir au lancement via une variable d'environnement.

### Sous Windows PowerShell

```powershell
$env:GITHUB_CLIENT_ID="Ov23liZfHPLcitSHTJRl"
$env:GITHUB_CLIENT_SECRET="votre_secret_github"
mvn spring-boot:run
```

### Sous Windows CMD

```cmd
set GITHUB_CLIENT_ID=Ov23liZfHPLcitSHTJRl
set GITHUB_CLIENT_SECRET=votre_secret_github
mvn spring-boot:run
```

### Sous Linux / macOS

```bash
export GITHUB_CLIENT_ID="Ov23liZfHPLcitSHTJRl"
export GITHUB_CLIENT_SECRET="votre_secret_github"
mvn spring-boot:run
```

Dans Eclipse, on peut aussi configurer ces variables dans :

```text
Run Configurations
→ Java Application
→ Environment
→ Add
```

Ajouter :

```text
GITHUB_CLIENT_ID
GITHUB_CLIENT_SECRET
```

---

## 4. Configuration Spring Security

Dans `SecurityConfig`, la chaîne MVC contient maintenant :

```java
.oauth2Login(oauth2 -> oauth2
        .loginPage("/login")
        .defaultSuccessUrl("/ui/duplicatas", true))
```

On conserve la page de login personnalisée.

On y ajoute simplement un lien vers :

```text
/oauth2/authorization/github
```

Ce n'est pas une URL créée par un contrôleur de l'application.

Elle est fournie automatiquement par Spring Security OAuth2 Client.

---

## 5. Page de login Thymeleaf

Dans `templates/security/login.html`, un bouton a été ajouté :

```html
<a class="button secondary" th:href="@{/oauth2/authorization/github}">
    Se connecter avec GitHub
</a>
```

Quand l'utilisateur clique dessus :

1. Spring Security redirige vers GitHub ;
2. GitHub demande l'autorisation ;
3. GitHub redirige vers `/login/oauth2/code/github` ;
4. Spring Security échange le code contre un token ;
5. Spring Security charge les informations de l'utilisateur GitHub ;
6. l'utilisateur est connecté dans la session HTTP ;
7. il est redirigé vers `/ui/duplicatas`.

---

## 6. Mapping des rôles

GitHub ne connaît pas les rôles métier de notre application.

Dans le corrigé, on ajoute donc un bean pédagogique :

```java
@Bean
GrantedAuthoritiesMapper userAuthoritiesMapper() {
    return authorities -> {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        ...
        return mappedAuthorities;
    };
}
```

Ainsi, tout utilisateur GitHub authentifié peut consulter les duplicatas.

Les actions suivantes restent réservées à `ROLE_ADMIN` :

- créer un duplicata depuis le front ;
- supprimer un duplicata depuis le front ;
- accéder à certains endpoints actuator ;
- vider les caches ;
- créer/supprimer via l'API REST.

Pour ce TP, le rôle `ADMIN` reste donc porté par le compte local :

```text
admin/admin
```

---

## 7. Pourquoi garder JWT pour l'API REST ?

Dans ce projet, on illustre deux usages différents :

| Zone | Mécanisme | Pourquoi |
|---|---|---|
| Front Thymeleaf `/ui/**` | Session + formulaire ou OAuth2 GitHub | Navigation web classique |
| API REST | JWT Bearer Token | API stateless consommable par Postman, Swagger ou un frontend JS |

OAuth2 Login est très pratique pour connecter un utilisateur dans une application web.

JWT est plus adapté pour sécuriser des appels API REST stateless.

---

## 8. Tester le TP

### Démarrer l'application

```bash
mvn spring-boot:run
```

### Accéder à la page de login

```text
http://localhost:8080/login
```

Tester :

- connexion locale `user/user` ;
- connexion locale `admin/admin` ;
- connexion GitHub.

### Accéder au front

```text
http://localhost:8080/ui/duplicatas
```

Un utilisateur GitHub doit pouvoir consulter la liste.

Il ne doit pas pouvoir accéder à :

```text
http://localhost:8080/ui/duplicatas/new
```

sauf si vous enrichissez le mapping des rôles.

---

## 9. Tester que JWT fonctionne toujours

### Obtenir un token JWT

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

Réponse attendue :

```json
{
  "token": "eyJ..."
}
```

### Appeler l'API REST avec le token

```bash
curl http://localhost:8080/duplicatas \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

Cela montre que l'ajout d'OAuth2 Login n'a pas remplacé la sécurité JWT de l'API.

---

## 10. Points importants à retenir

### `oauth2Login()`

Active le login OAuth2 pour une application web avec session HTTP.

### `/oauth2/authorization/github`

URL générée par Spring Security pour lancer le flux OAuth2.

### `/login/oauth2/code/github`

URL de callback appelée par GitHub après authentification.

### `registrationId`

Identifiant du fournisseur OAuth2.

Ici :

```text
github
```

### `client-id`

Identifiant public de l'application OAuth.

### `client-secret`

Secret privé de l'application OAuth.

Il ne doit pas être commité dans Git.

---

## 11. Structure des fichiers modifiés

```text
pom.xml
src/main/resources/application.properties
src/main/java/com/formation/config/SecurityConfig.java
src/main/resources/templates/security/login.html
README.md
```

---

## 12. Exercice 1

Afficher sur la page liste le nom de l'utilisateur connecté.

Indice : dans Thymeleaf, on peut utiliser :

```html
<span sec:authentication="name"></span>
```

Comparer le résultat entre :

- `user/user` ;
- `admin/admin` ;
- GitHub.

---

## 13. Exercice 2

Donner le rôle `ADMIN` à un login GitHub précis.

Dans `SecurityConfig`, compléter le mapper :

```java
Object login = oauth2UserAuthority.getAttributes().get("login");
if ("votre-login-github".equals(login)) {
    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
}
```

Relancer l'application et vérifier que l'utilisateur GitHub peut accéder à :

```text
/ui/duplicatas/new
```

---

## 14. Exercice 3

Observer les informations retournées par GitHub.

Dans le mapper, mettre temporairement un point d'arrêt ou un `System.out.println` sur :

```java
oauth2UserAuthority.getAttributes()
```

Observer notamment :

- `login` ;
- `id` ;
- `name` ;
- `email` ;
- `avatar_url`.

---

## 15. Problèmes fréquents

### Erreur `redirect_uri_mismatch`

L'URL de callback déclarée dans GitHub ne correspond pas à celle utilisée par Spring Security.

Vérifier dans GitHub :

```text
http://localhost:8080/login/oauth2/code/github
```

### Erreur `bad credentials` ou `invalid_client`

Le `client-secret` est absent ou incorrect.

Vérifier la variable d'environnement :

```text
GITHUB_CLIENT_SECRET
```

### Le bouton GitHub revient vers `/login?error`

Regarder la console de l'application.

Causes fréquentes :

- mauvais secret ;
- callback GitHub mal configuré ;
- application GitHub supprimée ;
- scopes incorrects.

---

## 16. Accès utiles

| Ressource | URL |
|---|---|
| Login | `http://localhost:8080/login` |
| Front duplicatas | `http://localhost:8080/ui/duplicatas` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| H2 Console | `http://localhost:8080/h2-console` |
| Actuator health | `http://localhost:8080/actuator/health` |
| API login JWT | `POST http://localhost:8080/api/auth/login` |

---

## 17. Conclusion 

Ce TP montre que Spring Security peut gérer plusieurs mécanismes dans la même application :

- formulaire HTML pour les comptes locaux ;
- OAuth2 Login avec GitHub pour le front ;
- JWT pour les APIs REST.

Le point central est la séparation des usages :

- OAuth2 Login pour connecter un utilisateur dans une application web ;
- JWT pour sécuriser des appels REST stateless ;
- rôles Spring Security pour exprimer les autorisations métier.
