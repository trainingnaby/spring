# TP - Programmation Orientée Aspect avec Spring AOP

## Objectif du TP

Ce projet démontre la **programmation orientée aspect** avec le Spring Framework classique.

Le projet est volontairement simple :

- pas de Spring Boot ;
- pas de REST ;
- pas de vues HTML ;
- pas de base de données ;
- uniquement Spring IoC et Spring AOP ;
- exécution via une simple classe `main`.

Le domaine métier est volontairement en français :

```text
Compte
ServiceCompte
deposer
retirer
consulterSolde
consulterCompte
```

L'objectif est de montrer comment ajouter des traitements transverses autour du métier sans modifier le code métier.

---

## 1. C'est quoi l'AOP ?

AOP signifie **Aspect Oriented Programming**, ou **programmation orientée aspect**.

L'idée principale est de séparer :

- le code métier ;
- les traitements techniques transverses.

Exemples de traitements transverses :

- journalisation ;
- mesure du temps d'exécution ;
- sécurité ;
- transaction ;
- gestion d'erreurs ;
- audit.

Sans AOP, on écrit souvent ce genre de code partout :

```java
System.out.println("Début méthode");
try {
    // métier
} catch (Exception e) {
    // log erreur
} finally {
    System.out.println("Fin méthode");
}
```

Avec AOP, on centralise ce comportement dans une classe séparée appelée **aspect**.

---

## 2. Exemple métier du projet

Le métier est représenté par un service bancaire très simple :

```java
@Service
public class ServiceCompte {

    public void deposer(double montant) {
        ...
    }

    public void retirer(double montant) {
        ...
    }

    public double consulterSolde() {
        ...
    }

    public Compte consulterCompte() {
        ...
    }
}
```

Ce service ne contient que la logique métier :

- déposer de l'argent ;
- retirer de l'argent ;
- consulter le solde ;
- consulter le compte.

Les logs, mesures de temps et traitements d'erreur sont placés dans des aspects.

---

## 3. Notions importantes AOP

### 3.1 Aspect

Un **aspect** est une classe qui contient un traitement transverse.

Exemple :

```java
@Aspect
@Component
public class AspectJournalisation {
}
```

### 3.2 Join Point

Un **join point** est un point d'exécution interceptable.

Avec Spring AOP, le join point le plus courant est :

```text
l'appel d'une méthode d'un bean Spring
```

Dans ce projet, les méthodes du service `ServiceCompte` sont des join points.

### 3.3 Pointcut

Un **pointcut** définit quelles méthodes doivent être interceptées.

Exemple :

```java
execution(* fr.formation.aop.service.ServiceCompte.*(..))
```

Signification :

```text
Intercepter toutes les méthodes de ServiceCompte,
quel que soit leur type de retour,
quel que soit leur nom,
quels que soient leurs paramètres.
```

### 3.4 Advice

Un **advice** est le code exécuté par l'aspect.

Exemples :

- `@Before`
- `@Around`
- `@After`
- `@AfterReturning`
- `@AfterThrowing`

---

## 4. Les types d'advice démontrés

## 4.1 @Before

Classe :

```text
AspectJournalisation
```

Code :

```java
@Before("execution(* fr.formation.aop.service.ServiceCompte.*(..))")
public void avantAppelMethode(JoinPoint joinPoint) {
    System.out.println("[AOP @Before] Avant appel de la méthode : "
            + joinPoint.getSignature().getName());
}
```

`@Before` s'exécute avant l'appel de la méthode métier.

Exemple :

```text
[AOP @Before] Avant appel de la méthode : deposer
[METIER] Dépôt de 250.0 euros
```

---

## 4.2 @Around

Classe :

```text
AspectTempsExecution
```

`@Around` entoure complètement la méthode métier.

Il peut :

- exécuter du code avant ;
- appeler la méthode métier ;
- exécuter du code après ;
- modifier le résultat ;
- empêcher l'appel de la méthode si nécessaire.

Code important :

```java
Object resultat = proceedingJoinPoint.proceed();
```

Sans cet appel, la méthode métier n'est jamais exécutée.

Dans ce projet, `@Around` mesure le temps d'exécution.

---

## 4.3 @After

Classe :

```text
AspectFinTraitement
```

`@After` s'exécute après la méthode, qu'elle réussisse ou qu'elle lève une exception.

C'est proche d'un bloc `finally`.

Exemple :

```text
[AOP @After] Après exécution de : retirer peu importe succès ou exception
```

---

## 4.4 @AfterReturning

Classe :

```text
AspectResultat
```

`@AfterReturning` s'exécute seulement si la méthode se termine normalement.

Dans ce projet, il intercepte seulement les méthodes qui commencent par `consulter` :

```java
execution(* fr.formation.aop.service.ServiceCompte.consulter*(..))
```

Il permet d'afficher le résultat retourné.

Exemple :

```text
[AOP @AfterReturning] La méthode consulterSolde a retourné : 1250.0
```

---

## 4.5 @AfterThrowing

Classe :

```text
AspectErreur
```

`@AfterThrowing` s'exécute seulement si la méthode lève une exception.

Dans ce projet, il se déclenche quand :

- on retire plus que le solde disponible ;
- on dépose un montant négatif.

Exemple :

```text
[AOP @AfterThrowing] Exception dans retirer : Solde insuffisant pour retirer 5000.0 euros
```

---

## 5. Configuration Spring

Le projet utilise une configuration Java classique :

```java
@Configuration
@ComponentScan(basePackages = "fr.formation.aop")
@EnableAspectJAutoProxy
public class ConfigurationSpring {
}
```

### @Configuration

Déclare une classe de configuration Spring.

### @ComponentScan

Demande à Spring de scanner les classes annotées :

- `@Component`
- `@Service`
- `@Aspect`

### @EnableAspectJAutoProxy

Active la création automatique de proxys AOP.

Sans cette annotation, les aspects ne s'appliquent pas.

---

## 6. Comment Spring AOP fonctionne ?

Spring AOP fonctionne grâce à des **proxys**.

Quand on récupère le bean :

```java
ServiceCompte serviceCompte = contexte.getBean(ServiceCompte.class);
```

Spring ne donne pas directement l'objet original.

Il fournit un objet proxy.

```text
Application
    ↓
Proxy Spring
    ↓
Aspects
    ↓
ServiceCompte réel
```

Le proxy intercepte les appels et déclenche les aspects.

---

## 7. Structure du projet

```text
spring-aop-compte-demo
├── pom.xml
├── README.md
└── src
    └── main
        └── java
            └── fr
                └── formation
                    └── aop
                        ├── application
                        │   └── ApplicationDemoAop.java
                        ├── aspect
                        │   ├── AspectErreur.java
                        │   ├── AspectFinTraitement.java
                        │   ├── AspectJournalisation.java
                        │   ├── AspectResultat.java
                        │   └── AspectTempsExecution.java
                        ├── config
                        │   └── ConfigurationSpring.java
                        ├── domaine
                        │   └── Compte.java
                        └── service
                            └── ServiceCompte.java
```

---

## 8. Importer dans Eclipse

1. Ouvrir Eclipse.
2. Aller dans :

```text
File > Import
```

3. Choisir :

```text
Maven > Existing Maven Projects
```

4. Sélectionner le dossier du projet.
5. Cliquer sur `Finish`.
6. Attendre le téléchargement des dépendances Maven.
7. Lancer la classe :

```text
fr.formation.aop.application.ApplicationDemoAop
```

avec :

```text
Run As > Java Application
```

---

## 9. Lancer avec Maven

```bash
mvn clean compile exec:java
```

---

## 10. Résultat attendu

La console doit afficher une séquence de logs ressemblant à ceci :

```text
===== 1. Consultation du compte =====
[AOP @Around] Début autour de : consulterCompte
[AOP @Before] Avant appel de la méthode : consulterCompte
[METIER] Consultation du compte
[AOP @AfterReturning] La méthode consulterCompte a retourné : Compte{numero='CPT-1001', solde=1000.0}
[AOP @After] Après exécution de : consulterCompte peu importe succès ou exception
[AOP @Around] Fin autour de : consulterCompte - durée : ...
```

L'ordre exact peut varier légèrement selon les proxys, mais les cinq types d'advice doivent être visibles.

---

## 11. Exercices supplémentaires

### Exercice 1

Modifier le pointcut de `AspectJournalisation` pour intercepter uniquement :

```text
deposer
retirer
```

Indice :

```java
execution(* fr.formation.aop.service.ServiceCompte.deposer(..))
```

et l'opérateur `||`.

### Exercice 2

Créer un nouvel aspect `AspectSecurite` qui interdit les retraits supérieurs à 1000 euros.

Indice : utiliser `@Around`.

### Exercice 3

Modifier `@AfterReturning` pour intercepter uniquement `consulterSolde`.

### Exercice 4

Créer une annotation personnalisée `@Auditable`, puis appliquer l'aspect seulement aux méthodes annotées.

### Exercice 5

Ajouter une méthode :

```java
public void virement(String ibanDestination, double montant)
```

et journaliser tous les virements avec un aspect.

---

## 12. Points importants à retenir

- AOP sert à séparer le métier des traitements transverses.
- Spring AOP utilise des proxys.
- Les aspects s'appliquent aux beans Spring.
- `@Before` s'exécute avant la méthode.
- `@Around` entoure la méthode.
- `@After` s'exécute dans tous les cas.
- `@AfterReturning` s'exécute seulement en cas de succès.
- `@AfterThrowing` s'exécute seulement en cas d'exception.
- Le coeur métier reste dans `ServiceCompte`.

---

## 13. Conclusion

Ce TP montre comment Spring AOP permet d'ajouter des comportements techniques sans polluer le code métier.

Dans une application réelle, les mêmes concepts sont utilisés pour :

- la gestion des transactions ;
- la sécurité ;
- le logging ;
- l'audit ;
- la mesure de performance ;
- la gestion centralisée des erreurs.
