# TP 13 — Spring Security sur les routes front MVC / Thymeleaf

## Objectif du TP

Ce TP part du dernier corrigé `12_fil_rouge_actuator_corrige` et ajoute une première couche de sécurité avec Spring Security.

L'objectif est volontairement limité à la partie **front MVC Thymeleaf** :

- authentifier les utilisateurs avec un formulaire de connexion ;
- protéger les pages `/ui/**` ;
- appliquer des autorisations selon les rôles ;
- comprendre la configuration de base de Spring Security ;
- comprendre les notions de routes publiques, routes protégées, CORS et CSRF ;
- conserver les routes REST publiques pour un futur TP sécurité REST/JWT.

La sécurisation des services REST sera traitée plus tard. Dans ce TP, les endpoints REST restent donc en `permitAll()`.

---

## Ce que le projet contient

Le projet contient maintenant :

- `spring-boot-starter-security` ;
- `thymeleaf-extras-springsecurity6` ;
- une classe `SecurityConfig` ;
- une page de connexion personnalisée ;
- une page d'accès refusé ;
- deux utilisateurs en mémoire ;
- une configuration d'autorisations par routes ;
- la protection CSRF des formulaires Thymeleaf ;
- une configuration CORS pédagogique.

---

## Comptes de test

Deux comptes sont configurés en mémoire dans `SecurityConfig`.

| Login | Mot de passe | Rôle | Droits |
|---|---|---|---|
| `user` | `user` | `ROLE_USER` | consulter la liste et le détail des duplicatas |
| `admin` | `admin` | `ROLE_USER`, `ROLE_ADMIN` | consulter, créer et supprimer des duplicatas |

Ces comptes sont volontairement simples pour le TP.

En production, les utilisateurs seraient stockés en base de données, dans un annuaire LDAP, dans Keycloak, ou dans un autre fournisseur d'identité.

---

## Importer le projet dans Eclipse

1. Ouvrir Eclipse.
2. Aller dans `File > Import...`.
3. Choisir `Maven > Existing Maven Projects`.
4. Sélectionner le dossier du projet.
5. Cliquer sur `Finish`.
6. Attendre la fin du téléchargement des dépendances Maven.
7. Lancer la classe :

```text
com.formation.DuplicataImpotsApplication
```

---

## Lancer l'application

Avec Maven :

```bash
mvn spring-boot:run
```

Ou depuis Eclipse :

```text
Clic droit sur DuplicataImpotsApplication
Run As > Java Application
```

Application :

```text
http://localhost:8080
```

La racine `/` redirige vers :

```text
http://localhost:8080/ui/duplicatas
```

Si l'utilisateur n'est pas connecté, Spring Security redirige vers :

```text
http://localhost:8080/login
```

---

## Exercice 1 — Ajouter Spring Security

### À faire 

Ajouter la dépendance suivante dans le `pom.xml` :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Dès que cette dépendance est ajoutée, Spring Boot active une sécurité par défaut :

- toutes les routes sont protégées ;
- une page de login générée automatiquement est disponible ;
- un utilisateur `user` est créé automatiquement ;
- le mot de passe est affiché dans la console au démarrage.

### À observer

Démarrer l'application et ouvrir :

```text
http://localhost:8080/ui/duplicatas
```

Spring Security demande une authentification.

### Message pédagogique

Spring Boot configure déjà beaucoup de choses automatiquement, mais dans une vraie application il faut reprendre la main avec une configuration explicite.

---

## Exercice 2 — Créer une configuration de sécurité

### Classe à créer

```text
src/main/java/com/formation/config/SecurityConfig.java
```

Dans le corrigé, cette classe contient :

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // beans SecurityFilterChain, UserDetailsService, PasswordEncoder, CORS...
}
```

### Concepts importants

#### `SecurityFilterChain`

C'est le coeur de la configuration web de Spring Security.

Elle permet de déclarer :

- quelles routes sont publiques ;
- quelles routes nécessitent une authentification ;
- quelles routes nécessitent un rôle précis ;
- le type de login ;
- le logout ;
- la gestion CSRF ;
- la gestion CORS.

#### `UserDetailsService`

C'est le composant qui permet à Spring Security de charger les utilisateurs.

Dans ce TP, on utilise :

```java
InMemoryUserDetailsManager
```

Cela permet de créer des utilisateurs directement dans le code pour rester simple.

#### `PasswordEncoder`

Spring Security n'accepte pas les mots de passe en clair sans encodeur.

Le corrigé utilise :

```java
BCryptPasswordEncoder
```

Même en TP, c'est une bonne habitude.

---

## Exercice 3 — Créer les utilisateurs en mémoire

Dans le corrigé :

```java
@Bean
UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails lecteur = org.springframework.security.core.userdetails.User
            .withUsername("user")
            .password(passwordEncoder.encode("user"))
            .roles("USER")
            .build();

    UserDetails admin = org.springframework.security.core.userdetails.User
            .withUsername("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("USER", "ADMIN")
            .build();

    return new InMemoryUserDetailsManager(lecteur, admin);
}
```

### À tester

Se connecter avec :

```text
user / user
```

Puis avec :

```text
admin / admin
```

---

## Exercice 4 — Protéger les routes front

La règle métier choisie pour le TP est la suivante :

| Route | Rôle attendu |
|---|---|
| `GET /ui/duplicatas` | `USER` ou `ADMIN` |
| `GET /ui/duplicatas/{id}` | `USER` ou `ADMIN` |
| `GET /ui/duplicatas/new` | `ADMIN` |
| `POST /ui/duplicatas` | `ADMIN` |
| `POST /ui/duplicatas/{id}/delete` | `ADMIN` |

Dans le corrigé :

```java
.requestMatchers(HttpMethod.GET, "/ui/duplicatas/new").hasRole("ADMIN")
.requestMatchers(HttpMethod.POST, "/ui/duplicatas", "/ui/duplicatas/*/delete").hasRole("ADMIN")
.requestMatchers(HttpMethod.GET, "/ui/duplicatas", "/ui/duplicatas/*").hasAnyRole("USER", "ADMIN")
```

### Point important

L'ordre des règles est important.

La règle :

```java
/ui/duplicatas/*
```

peut aussi correspondre à :

```text
/ui/duplicatas/new
```

Il faut donc placer la règle spécifique `/ui/duplicatas/new` avant la règle plus générale `/ui/duplicatas/*`.

---

## Exercice 5 — Garder les routes REST publiques

Dans ce TP, on ne sécurise pas encore les services REST.

Les routes suivantes sont donc explicitement publiques :

```java
.requestMatchers("/duplicatas", "/duplicatas/**", "/duplicatas_dto", "/duplicatas_dto/**").permitAll()
```

### À tester

Même sans être connecté, ces URL doivent rester accessibles :

```text
http://localhost:8080/duplicatas
http://localhost:8080/duplicatas/dup-demo-001
http://localhost:8080/swagger-ui.html
```

La sécurité REST, avec JWT notamment, sera traitée dans un TP séparé.

---

## Exercice 6 — Créer une page de login personnalisée

Le corrigé ajoute :

```text
src/main/resources/templates/security/login.html
```

et le contrôleur :

```text
src/main/java/com/formation/mvc/SecurityPageController.java
```

La configuration indique à Spring Security d'utiliser cette page :

```java
.formLogin(form -> form
        .loginPage("/login")
        .defaultSuccessUrl("/ui/duplicatas", true)
        .permitAll())
```

### À retenir

- `loginPage("/login")` indique l'URL de la page de connexion.
- Le formulaire doit poster vers `/login`.
- Les champs attendus par défaut sont `username` et `password`.

---

## Exercice 7 — Gérer l'accès refusé

Quand un utilisateur est connecté mais n'a pas le bon rôle, il ne doit pas retourner au login.

Il doit obtenir une erreur d'autorisation.

Le corrigé configure :

```java
.exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"))
```

Page ajoutée :

```text
src/main/resources/templates/security/access-denied.html
```

### À tester

1. Se connecter avec `user/user`.
2. Aller sur :

```text
http://localhost:8080/ui/duplicatas/new
```

Résultat attendu : page `Accès refusé`.

3. Se reconnecter avec `admin/admin`.
4. La page de création est accessible.

---

## Exercice 8 — Comprendre CSRF

### C'est quoi CSRF ?

CSRF signifie `Cross-Site Request Forgery`.

C'est une attaque où un site externe essaie de faire exécuter une action à un utilisateur déjà connecté sur votre application.

Exemple :

- l'utilisateur est connecté à votre application ;
- il visite un autre site malveillant ;
- ce site envoie en douce un formulaire POST vers votre application ;
- sans protection CSRF, l'application pourrait accepter l'action.

### Choix du TP

Pour les pages Thymeleaf, CSRF reste activé.

C'est important parce que le front MVC utilise des formulaires HTML :

- création d'un duplicata ;
- suppression d'un duplicata ;
- déconnexion.

Chaque formulaire POST contient donc :

```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
```

### À tester

Supprimer ce champ dans `form.html` ou `list.html`, puis essayer de poster un formulaire.

Résultat attendu : erreur `403 Forbidden`.

### Exceptions pédagogiques

Dans le corrigé, CSRF est ignoré pour :

```java
.ignoringRequestMatchers("/h2-console/**")
.ignoringRequestMatchers("/duplicatas/**", "/duplicatas_dto/**", "/cache/**")
```

Pourquoi ?

- H2 Console est un outil local de TP.
- Les routes REST ne sont pas le sujet de ce TP.
- Les routes REST seront sécurisées plus proprement plus tard.

---

## Exercice 9 — Comprendre CORS

### C'est quoi CORS ?

CORS signifie `Cross-Origin Resource Sharing`.

Le navigateur applique CORS lorsqu'une application web appelle une API située sur une autre origine.

Exemples d'origines différentes :

```text
http://localhost:3000
http://localhost:8080
```

ou :

```text
https://front.monapp.fr
https://api.monapp.fr
```

### Important

CORS est une protection côté navigateur.

Ce n'est pas une authentification.

Ce n'est pas une autorisation métier.

### Dans notre TP

Le front Thymeleaf est servi par la même application que le backend :

```text
http://localhost:8080/ui/duplicatas
```

Il n'a donc pas vraiment besoin de CORS.

Mais le corrigé montre une configuration simple pour préparer le futur TP REST :

```java
@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:5500"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

Et dans la chaîne de sécurité :

```java
.cors(Customizer.withDefaults())
```

---

## Exercice 10 — Adapter les vues Thymeleaf selon le rôle

Le projet utilise :

```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

Cela permet d'écrire dans les templates :

```html
<a sec:authorize="hasRole('ADMIN')" th:href="@{/ui/duplicatas/new}">
    Générer un duplicata
</a>
```

ou :

```html
<form sec:authorize="hasRole('ADMIN')">
    ...
</form>
```

### À observer

Avec `user/user` :

- le bouton créer n'apparaît pas ;
- le bouton supprimer n'apparaît pas.

Avec `admin/admin` :

- le bouton créer apparaît ;
- le bouton supprimer apparaît.

Attention : masquer un bouton dans l'interface ne suffit jamais.

Il faut aussi sécuriser la route côté serveur, ce que fait `SecurityConfig`.

---

## Exercice 11 — Tester les endpoints Actuator

Le projet contient encore Actuator.

Les endpoints suivants sont publics :

```text
http://localhost:8080/actuator/health
http://localhost:8080/actuator/info
```

Les autres endpoints Actuator sont réservés à l'administrateur :

```java
.requestMatchers("/actuator/**").hasRole("ADMIN")
```

### À tester

Sans connexion ou avec `user/user` :

```text
http://localhost:8080/actuator/beans
```

Résultat attendu : accès refusé ou redirection login.

Avec `admin/admin`, l'accès est autorisé.

---

## Exercice 12 — Scénario de validation complet

### Cas 1 — utilisateur non connecté

Ouvrir :

```text
http://localhost:8080/ui/duplicatas
```

Résultat attendu : redirection vers `/login`.

### Cas 2 — utilisateur lecteur

Se connecter avec :

```text
user / user
```

Résultats attendus :

- consultation liste OK ;
- consultation détail OK ;
- création interdite ;
- suppression interdite ;
- boutons admin masqués.

### Cas 3 — utilisateur administrateur

Se connecter avec :

```text
admin / admin
```

Résultats attendus :

- consultation liste OK ;
- consultation détail OK ;
- création OK ;
- suppression OK ;
- boutons admin visibles.

### Cas 4 — services REST

Sans être connecté, ouvrir :

```text
http://localhost:8080/duplicatas
```

Résultat attendu : accessible.

---

## Fichiers importants du corrigé

```text
pom.xml
src/main/java/com/formation/config/SecurityConfig.java
src/main/java/com/formation/mvc/SecurityPageController.java
src/main/resources/templates/security/login.html
src/main/resources/templates/security/access-denied.html
src/main/resources/templates/duplicatas/list.html
src/main/resources/templates/duplicatas/detail.html
src/main/resources/templates/duplicatas/form.html
```

---

## Points à retenir

- Spring Security protège tout par défaut dès que le starter est présent.
- `SecurityFilterChain` permet de définir les règles de sécurité HTTP.
- `hasRole("ADMIN")` vérifie en réalité l'autorité `ROLE_ADMIN`.
- L'ordre des règles de sécurité est important.
- CSRF doit rester activé sur les formulaires web classiques.
- CORS concerne les appels navigateur entre origines différentes.
- Masquer un bouton dans Thymeleaf ne remplace jamais une vraie autorisation serveur.
- Dans ce TP, les routes REST restent publiques volontairement.

---

## Suite logique

Le prochain TP pourra porter sur la sécurité REST :

- désactiver CSRF uniquement pour l'API REST ;
- protéger les endpoints REST ;
- exposer `/auth/login` ;
- générer un JWT ;
- valider le JWT dans un filtre ;
- tester les statuts `401`, `403` et `200`.
