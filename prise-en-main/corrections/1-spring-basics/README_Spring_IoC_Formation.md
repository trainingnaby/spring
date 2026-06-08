# Module IoC Spring – README de formation

## Pourquoi commencer par le module IoC ?

Le cœur du framework Spring repose sur l'**IoC (Inversion of Control)** et l'**Injection de Dépendances (Dependency Injection)**.

Avant d'utiliser Spring MVC, Spring Data JPA, Spring Security ou Spring Boot, il est essentiel de comprendre comment Spring crée, configure et relie les objets de l'application.

L'objectif de ce module est de comprendre :

- Comment Spring crée les objets.
- Comment Spring gère leur cycle de vie.
- Comment les dépendances sont injectées.
- Comment configurer l'application de différentes manières.
- Comment Spring résout automatiquement les dépendances.

Cette compréhension constitue la base de tous les autres modules Spring.

---

# Concepts fondamentaux

## Bean Spring

Un **bean** est un objet Java dont le cycle de vie est géré par Spring.

Exemple sans Spring :

```java
UserService service = new UserService();
```

L'objet est créé manuellement.

Exemple avec Spring :

```java
@Service
public class UserService {
}
```

Spring crée l'objet automatiquement et le met à disposition dans son conteneur.

Un bean peut être déclaré via :

- XML
- Configuration Java (`@Configuration` + `@Bean`)
- Annotations (`@Component`, `@Service`, `@Repository`, `@Controller`)

---

## Contexte Spring (ApplicationContext)

Le **contexte Spring** est le conteneur qui stocke et gère les beans.

Ses responsabilités principales sont :

- Création des beans.
- Gestion du cycle de vie.
- Injection des dépendances.
- Résolution des dépendances.
- Gestion de la configuration.

On peut le voir comme une grande collection d'objets gérés par Spring.

```text
Application
     |
     v
ApplicationContext
     |
+----+----+
|         |
v         v
Bean A   Bean B
```

---

## Inversion of Control (IoC)

L'Inversion of Control consiste à déléguer à Spring la responsabilité de créer et fournir les objets nécessaires.

Sans IoC :

```java
UserRepository repository = new UserRepository();
UserService service = new UserService(repository);
```

Avec IoC :

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

Spring fournit automatiquement le `UserRepository`.

---

# Configuration du contexte Spring

Durant les exercices, plusieurs approches de configuration ont été étudiées.

## 1. Configuration XML

Déclaration des beans dans un fichier XML.

```xml
<bean id="userService"
      class="com.example.UserService"/>
```

### Avantages

- Historique.
- Séparation complète entre code et configuration.

### Inconvénients

- Verbose.
- Peu utilisé dans les nouveaux projets.

---

## 2. Configuration Java

Utilisation d'une classe annotée `@Configuration`.

```java
@Configuration
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserService();
    }
}
```

### Avantages

- Type-safe.
- Configuration centralisée.

---

## 3. Configuration par annotations

Utilisation du composant scanning.

```java
@Service
public class UserService {
}
```

```java
@ComponentScan("com.example")
```

### Avantages

- Configuration minimale.
- Très utilisée dans Spring Boot.

---

# Import de configurations

Il est souvent nécessaire de répartir la configuration sur plusieurs fichiers.

## Import XML

```xml
<import resource="services.xml"/>
```

## Import Java

```java
@Configuration
@Import(DatabaseConfig.class)
public class AppConfig {
}
```

Cette approche permet de modulariser la configuration.

---

# Injection des dépendances

L'injection de dépendances permet à Spring de fournir automatiquement les objets nécessaires.

## Injection par constructeur

Méthode recommandée.

```java
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

### Avantages

- Dépendances obligatoires.
- Immutabilité.
- Facile à tester.

---

## Injection par setter

```java
@Service
public class UserService {

    private UserRepository repository;

    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }
}
```

### Utilisation

Pour les dépendances optionnelles ou modifiables.

---

## Injection par champ

```java
@Autowired
private UserRepository repository;
```

### Limites

- Plus difficile à tester.
- Dépendances moins visibles.

Utilisation déconseillée dans les nouveaux projets.

---

# Résolution automatique des dépendances

## Autowiring par type

Spring recherche un bean correspondant au type demandé.

```java
@Autowired
private UserRepository repository;
```

Spring cherche un bean de type `UserRepository`.

---

## Ambiguïté entre plusieurs beans

Lorsque plusieurs beans du même type existent, Spring ne sait plus lequel utiliser.

---

# @Qualifier

Permet de préciser explicitement le bean à injecter.

```java
@Autowired
@Qualifier("mysqlRepository")
private UserRepository repository;
```

Spring injecte le bean nommé `mysqlRepository`.

---

# @Primary

Permet de définir un bean par défaut.

```java
@Bean
@Primary
public UserRepository mysqlRepository() {
    return new MysqlRepository();
}
```

Lorsqu'aucun `@Qualifier` n'est précisé, Spring utilisera ce bean.

---

# Spring Expression Language (SpEL)

SpEL permet d'évaluer des expressions au sein de la configuration Spring.

Exemple :

```java
@Value("#{2 + 3}")
private int resultat;
```

Accès à une propriété :

```java
@Value("#{databaseConfig.url}")
private String url;
```

SpEL permet :

- Les calculs.
- Les conditions.
- L'accès aux propriétés.
- Les appels de méthodes.

---

# Ce qu'il faut retenir

À l'issue de ce module, vous devez être capable de :

- Expliquer le rôle de l'IoC.
- Définir ce qu'est un bean.
- Définir ce qu'est le contexte Spring.
- Configurer un contexte Spring en XML.
- Configurer un contexte Spring en Java.
- Configurer un contexte Spring avec des annotations.
- Utiliser les imports de configuration.
- Réaliser une injection par constructeur, setter ou champ.
- Comprendre le fonctionnement de l'autowiring.
- Résoudre les ambiguïtés avec `@Qualifier` et `@Primary`.
- Utiliser les bases du langage SpEL.

La maîtrise de ces concepts constitue le socle indispensable pour aborder les autres modules de l'écosystème Spring.
