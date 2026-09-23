# Démonstration Spring Boot : comprendre l'auto-configuration

Ce projet accompagne la partie **Spring Boot** de la formation *Spring - développer des applications d'entreprise*.

L'objectif est de repartir d'une idée déjà connue avec Spring classique : pour obtenir un bean, on écrit généralement une configuration et un `@Bean`. Avec Spring Boot, une bibliothèque peut fournir cette configuration et demander à Boot de l'activer automatiquement lorsqu'elle est présente dans l'application.

Le projet reste volontairement petit. Il contient deux modules Maven :

```text
spring-boot-autoconfiguration-demo
|
+-- greeting-spring-boot-starter   bibliothèque qui fournit l'auto-configuration
|
+-- demo-application               application Spring Boot qui utilise la bibliothèque
```

## 1. Ce que l'on veut obtenir

L'application `demo-application` utilise un `GreetingService` :

```java
@Bean
CommandLineRunner demo(GreetingService greetingService) {
    return args -> System.out.println(greetingService.greet("les stagiaires"));
}
```

Pourtant, dans ce module, il n'y a :

- ni `@Bean GreetingService` ;
- ni `@Component` sur `GreetingService` ;
- ni `@Import(GreetingAutoConfiguration.class)`.

Le bean arrive dans le contexte grâce à l'auto-configuration fournie par l'autre module.

## 2. Le bean à auto-configurer

Dans `greeting-spring-boot-starter`, `GreetingService` est une classe Java ordinaire :

```java
public class GreetingService {

    private final String prefix;

    public GreetingService(String prefix) {
        this.prefix = prefix;
    }

    public String greet(String name) {
        return prefix + " " + name + " !";
    }
}
```

Il n'est volontairement pas annoté `@Component`.

## 3. La classe d'auto-configuration

Le starter contient :

```java
@AutoConfiguration
@EnableConfigurationProperties(GreetingProperties.class)
public class GreetingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GreetingService greetingService(GreetingProperties properties) {
        return new GreetingService(properties.getPrefix());
    }
}
```

`@AutoConfiguration` indique qu'il s'agit d'une configuration destinée à être chargée par le mécanisme d'auto-configuration de Spring Boot.

Le `@Bean` est finalement le même principe qu'en Spring classique. La différence est que l'application cliente n'a pas à importer cette configuration elle-même.

`@ConditionalOnMissingBean` est important : le bean est créé **seulement si l'application n'a pas déjà fourni son propre `GreetingService`**. Une auto-configuration doit en général proposer un comportement par défaut sans empêcher l'application de le remplacer.

## 4. Comment Spring Boot découvre cette configuration ?

Le fichier suivant est la pièce à observer :

```text
greeting-spring-boot-starter/src/main/resources/
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

Son contenu est :

```text
fr.formation.springboot.greeting.autoconfigure.GreetingAutoConfiguration
```

Lors du démarrage, Spring Boot recherche les auto-configurations déclarées par les bibliothèques présentes dans le classpath. Ce fichier lui indique que notre JAR fournit `GreetingAutoConfiguration`.

Le chemin à retenir est donc :

```text
dépendance Maven présente
        |
        v
AutoConfiguration.imports trouvé dans le JAR
        |
        v
GreetingAutoConfiguration examinée par Spring Boot
        |
        v
conditions vérifiées
        |
        v
@Bean GreetingService créé
        |
        v
bean disponible dans l'application
```

C'est le même principe général que les starters Spring Boot habituels : une dépendance apporte des classes et peut également apporter une configuration automatique adaptée à leur présence.

## 5. Configuration par `application.properties`

Le starter expose aussi une petite propriété :

```properties
training.greeting.prefix=Bienvenue
```

Elle est liée à :

```java
@ConfigurationProperties(prefix = "training.greeting")
public class GreetingProperties {
    private String prefix = "Bonjour";
    // getters / setters
}
```

L'auto-configuration utilise ensuite cette valeur lors de la création du bean.

Essayez de remplacer `Bienvenue` par `Bonjour` ou `Salut`, puis relancez l'application.

## 6. Manipulation 1 - vérifier que l'auto-configuration fonctionne

Depuis la racine du projet :

```bash
mvn clean install
mvn -pl demo-application spring-boot:run
```

La console doit notamment afficher :

```text
Bienvenue les stagiaires !
```

## 7. Manipulation 2 - retirer le starter

Dans `demo-application/pom.xml`, commentez temporairement la dépendance :

```xml
<dependency>
    <groupId>fr.formation</groupId>
    <artifactId>greeting-spring-boot-starter</artifactId>
    <version>${project.version}</version>
</dependency>
```

L'application ne peut plus injecter `GreetingService`. C'est une bonne façon de constater que ce bean n'est pas découvert par le component scanning de `demo-application` : il était réellement fourni par l'auto-configuration du starter.

Remettez ensuite la dépendance.

## 8. Manipulation 3 - fournir son propre bean

Ajoutez dans `DemoApplication` :

```java
@Bean
GreetingService myGreetingService() {
    return new GreetingService("Bonjour depuis l'application");
}
```

Relancez.

L'application démarre sans conflit. Le bean du starter n'est plus créé à cause de :

```java
@ConditionalOnMissingBean
```

L'application garde donc le dernier mot.

C'est un principe très courant dans Spring Boot : **auto-configurer une valeur par défaut, puis laisser le développeur la remplacer s'il a un besoin particulier**.

## 9. Manipulation 4 - observer les décisions de Spring Boot

Ajoutez temporairement dans `application.properties` :

```properties
debug=true
```

Au démarrage, Spring Boot affiche son rapport d'évaluation des conditions (*Conditions Evaluation Report*). Recherchez `GreetingAutoConfiguration`.

Ce rapport est particulièrement utile lorsqu'on veut comprendre pourquoi une auto-configuration a été appliquée ou non.

## 10. Ce qu'il faut retenir

Avec Spring classique, on écrit et on importe explicitement beaucoup de configuration.

Spring Boot ne supprime pas cette configuration : il en fournit une partie automatiquement à partir de ce qui est présent dans le classpath et des conditions déclarées par les auto-configurations.

Dans cet exemple :

1. `demo-application` dépend du starter ;
2. le starter déclare `GreetingAutoConfiguration` dans `AutoConfiguration.imports` ;
3. Boot découvre cette auto-configuration au démarrage ;
4. les conditions sont évaluées ;
5. si aucun `GreetingService` n'existe déjà, le `@Bean` est exécuté ;
6. le bean peut ensuite être injecté normalement dans l'application.

L'auto-configuration n'est donc pas de la magie : elle repose toujours sur le conteneur Spring, des classes de configuration et des conditions. Spring Boot automatise surtout leur découverte et leur activation.

## Importer le projet dans Eclipse

Prérequis : **JDK 17 ou supérieur** et Maven.

Dans Eclipse :

1. `File` > `Import...`
2. `Maven` > `Existing Maven Projects`
3. sélectionner le dossier `spring-boot-autoconfiguration-demo`
4. vérifier que les trois `pom.xml` sont détectés
5. terminer l'import

Si Eclipse ne met pas immédiatement les dépendances à jour : clic droit sur le projet > `Maven` > `Update Project...`.

Pour lancer la démonstration, exécuter `DemoApplication` avec `Run As` > `Java Application` ou `Spring Boot App` si Spring Tools est installé.
