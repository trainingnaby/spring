# Spring — comprendre `@Primary`

Petit projet utilisé dans la formation **Spring : développer des applications d'entreprise**.

L'objectif est de répondre à une question fréquente :

> Si un bean est déclaré `@Primary`, à quoi servent les autres beans du même type ?

## Ce que montre le projet

L'application dispose de trois implémentations de `NotificationService` :

- `EmailNotificationService` : déclarée `@Primary` ;
- `SmsNotificationService` ;
- `TeamsNotificationService`.

Trois situations sont ensuite observées.

### 1. Injection sans précision

`CommandeService` demande simplement un `NotificationService` :

```java
public CommandeService(NotificationService notificationService) {
    this.notificationService = notificationService;
}
```

Comme plusieurs beans correspondent au type demandé, Spring choisit celui marqué `@Primary` : `EmailNotificationService`.

**À retenir : `@Primary` désigne le choix par défaut.**

### 2. Choix explicite avec `@Qualifier`

`AlerteService` a besoin spécifiquement de l'envoi par SMS :

```java
public AlerteService(
        @Qualifier("smsNotificationService") NotificationService notificationService) {
    this.notificationService = notificationService;
}
```

Le `@Qualifier` l'emporte sur le choix par défaut. Le bean `SmsNotificationService` est donc injecté, même si un autre bean est `@Primary`.

**À retenir : `@Qualifier` permet de demander explicitement un bean.**

### 3. Injection de toutes les implémentations

`DiffusionService` reçoit une collection :

```java
public DiffusionService(List<NotificationService> notificationServices) {
    this.notificationServices = notificationServices;
}
```

Spring injecte les trois implémentations. `@Primary` n'exclut donc pas les autres beans du contexte.

## Lancer le projet

Prérequis : JDK 17 ou supérieur et Maven.

```bash
mvn clean compile
mvn exec:java
```

La sortie illustre successivement le choix par défaut, le choix explicite par `@Qualifier`, puis l'utilisation de toutes les implémentations.

## A tester

Retirer temporairement `@Primary` de `EmailNotificationService`, puis relancer l'application. Spring ne pourra plus choisir automatiquement le bean à injecter dans `CommandeService` et signalera une ambiguïté.

Remettre ensuite `@Primary` et vérifier que l'application fonctionne à nouveau.

On peut enfin déplacer `@Primary` sur une autre implémentation pour constater que seul le **choix par défaut** change.

## Structure

```text
src/main/java/fr/formation/entreprise/
├── Application.java
├── config/
│   └── AppConfig.java
├── notification/
│   ├── NotificationService.java
│   ├── EmailNotificationService.java
│   ├── SmsNotificationService.java
│   └── TeamsNotificationService.java
└── service/
    ├── CommandeService.java
    ├── AlerteService.java
    └── DiffusionService.java
```

## Synthèse

`@Primary` ne signifie pas « utiliser toujours ce bean ».

Il signifie : **« si plusieurs beans conviennent et qu'aucune précision supplémentaire n'est donnée, choisir celui-ci en priorité »**.

Les autres beans restent enregistrés dans le contexte Spring et peuvent être sélectionnés avec `@Qualifier` ou injectés collectivement (`List`, `Set`, etc.).
