# Demo Spring Security 6 — Authentification par formulaire

Projet Maven Spring Boot simple pour démontrer l'authentification par formulaire avec Spring Security 6.

Architecture pédagogique :

```text
Utilisateur
    ↓
Spring Boot
    ↓
Form Login Spring Security
```

Ce projet est volontairement simple : pas de base de données, pas de OAuth2, pas de JWT. L'objectif est de comprendre les bases avant de passer à GitHub OAuth2 puis Google OpenID Connect.

---

## Objectifs pédagogiques

À la fin de cette démo, les stagiaires doivent comprendre :

1. La différence entre une page publique et une page protégée.
2. Le rôle de `SecurityFilterChain`.
3. Le principe de l'authentification par formulaire.
4. Le rôle de `UserDetailsService`.
5. Le rôle de `PasswordEncoder`.
6. La différence entre authentification et autorisation.
7. La notion de rôle : `ROLE_USER`, `ROLE_ADMIN`.
8. Le mécanisme de logout.
9. Le rôle de la session HTTP après authentification.

---

## Stack technique

- Java 17+
- Spring Boot 3.5.14
- Spring Security 6
- Thymeleaf
- Maven
- Eclipse IDE

> Spring Boot 3.x utilise Spring Security 6. Spring Boot 4.x utilise Spring Security 7. Pour une formation centrée sur Spring Security 6, ce projet reste donc sur Spring Boot 3.x.

---

## Structure du projet

```text
spring-security-form-login-demo
├── pom.xml
├── README.md
└── src
    └── main
        ├── java
        │   └── com.example.formlogin
        │       ├── FormLoginDemoApplication.java
        │       ├── config
        │       │   └── SecurityConfig.java
        │       └── controller
        │           └── PageController.java
        └── resources
            ├── application.properties
            ├── static
            │   └── css
            │       └── style.css
            └── templates
                ├── home.html
                ├── login.html
                ├── profile.html
                └── admin.html
```

---

## Comptes de test

| Utilisateur | Mot de passe | Rôles |
|---|---|---|
| `user` | `password` | `ROLE_USER` |
| `admin` | `admin` | `ROLE_USER`, `ROLE_ADMIN` |

Les utilisateurs sont déclarés en mémoire dans `SecurityConfig`.

---

## Lancer le projet en ligne de commande

Depuis le dossier du projet :

```bash
mvn spring-boot:run
```

Puis ouvrir :

```text
http://localhost:8080
```

---

## Importer dans Eclipse

1. Ouvrir Eclipse.
2. Aller dans `File` → `Import`.
3. Choisir `Maven` → `Existing Maven Projects`.
4. Sélectionner le dossier `spring-security-form-login-demo`.
5. Cliquer sur `Finish`.
6. Attendre le téléchargement des dépendances Maven.
7. Lancer la classe :

```text
com.example.formlogin.FormLoginDemoApplication
```

---

## Scénario de démonstration

### 1. Accès à une page publique

Ouvrir :

```text
http://localhost:8080/
```

Cette page est accessible sans authentification.

Dans `SecurityConfig` :

```java
.requestMatchers("/", "/login", "/css/**").permitAll()
```

Cela signifie : ces URLs sont publiques.

---

### 2. Accès à une page privée

Ouvrir :

```text
http://localhost:8080/profile
```

Résultat attendu : Spring Security redirige vers :

```text
http://localhost:8080/login
```

Pourquoi ?

Parce que `/profile` est déclaré comme endpoint nécessitant une authentification :

```java
.requestMatchers("/profile", "/user/**").authenticated()
```

---

### 3. Connexion avec un utilisateur simple

Se connecter avec :

```text
user / password
```

Résultat attendu : accès à `/profile`.

La page affiche :

```text
Utilisateur connecté : user
Authorities : ROLE_USER
```

Point pédagogique :

```text
Authentification = Spring sait qui est l'utilisateur.
```

---

### 4. Accès à la page admin avec un utilisateur non admin

Avec l'utilisateur `user`, ouvrir :

```text
http://localhost:8080/admin
```

Résultat attendu : erreur `403 Forbidden`.

Pourquoi ?

L'utilisateur est authentifié, mais il n'a pas le bon rôle.

Dans `SecurityConfig` :

```java
.requestMatchers("/admin/**").hasRole("ADMIN")
```

Point pédagogique :

```text
Authentification ≠ Autorisation
```

- Authentification : qui es-tu ?
- Autorisation : as-tu le droit d'accéder à cette ressource ?

---

### 5. Connexion avec l'administrateur

Se déconnecter, puis se connecter avec :

```text
admin / admin
```

Ouvrir :

```text
http://localhost:8080/admin
```

Résultat attendu : accès autorisé.

La page admin nécessite `ROLE_ADMIN`, et l'utilisateur `admin` possède ce rôle.

---

### 6. Logout

Cliquer sur `Se déconnecter`.

Le formulaire appelle :

```html
<form th:action="@{/logout}" method="post">
```

Spring Security invalide la session et redirige vers :

```text
/login?logout
```

Point important : par défaut, Spring Security protège le logout contre les attaques CSRF. C'est pourquoi le logout se fait en `POST`, pas en simple lien `GET`.

---

## Code central : SecurityConfig

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/css/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/profile", "/user/**").authenticated()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/profile", true)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        );

    return http.build();
}
```

---

## Explication du flux complet

```text
1. L'utilisateur demande /profile
2. Spring Security intercepte la requête
3. L'utilisateur n'est pas authentifié
4. Spring redirige vers /login
5. L'utilisateur envoie username/password
6. Spring vérifie les identifiants via UserDetailsService
7. Le mot de passe est vérifié avec PasswordEncoder
8. Si OK, Spring crée une Authentication
9. Spring stocke l'authentification dans la session HTTP
10. L'utilisateur accède à /profile
```

---

## Pourquoi utiliser PasswordEncoder ?

Même dans une démo, il faut éviter les mots de passe en clair.

Dans ce projet :

```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Puis :

```java
.password(passwordEncoder.encode("password"))
```

Cela montre aux stagiaires que Spring Security attend un mot de passe encodé.

---

## Points à expliquer oralement

### 1. Spring Security fonctionne avec une chaîne de filtres

Avant d'arriver dans le contrôleur MVC, la requête traverse Spring Security.

```text
Requête HTTP
    ↓
Security Filter Chain
    ↓
Controller Spring MVC
```

---

### 2. Le formulaire ne fait pas lui-même la sécurité

Le formulaire HTML envoie seulement :

```text
username
password
```

C'est Spring Security qui traite la requête `POST /login`.

Il n'y a donc pas besoin de créer soi-même un contrôleur `POST /login`.

---

### 3. La session locale

Après la connexion, Spring garde l'utilisateur connecté via une session HTTP.

```text
Navigateur
    ↓ cookie JSESSIONID
Spring Boot
    ↓
SecurityContext
```

Cela sera utile pour comparer ensuite avec OAuth2 Login GitHub.
