Oxono

Oxono est un jeu de plateau d’alignement simple et stratégique, où les diagonales n’existent pas. Ce projet consiste à implémenter ce jeu en Java, 
en suivant l'architecture MVC et en utilisant des patrons de conception tels que *Observer*, *Command*, et *Strategy*.

Sommaire
- Description du jeu](#description-du-jeu)
- [Fonctionnalités](#fonctionnalités)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [Architecture du projet](#architecture-du-projet)
- [Tests](#tests)
- [Licence](#licence)

## Description du jeu

Le jeu Oxono oppose deux joueurs, les roses et les noirs. Le plateau est constitué de 6x6 cases, avec :

- **16 jetons roses** et **16 jetons noirs**, moitié avec des croix et moitié avec des cercles.
- **2 totems bleus** (un avec une croix, l'autre avec un cercle).

### Règles du jeu :
1. Les totems sont placés au centre du plateau au début de la partie.
2. Les joueurs jouent à tour de rôle. Chaque tour consiste à :
   - Déplacer un totem (horizontalement ou verticalement) sans traverser de case occupée.
   - Placer un jeton de la même forme que le totem déplacé, sur une case libre adjacente au totem.
3. Le premier joueur à aligner **4 jetons de la même couleur** ou **4 jetons du même symbole** (horizontalement ou verticalement) remporte la partie.

[Site officiel du jeu](https://www.cosmoludo.com/oxono-fr)

## Fonctionnalités

- **Modèle complètement testé** : gère toute la logique du jeu.
- **Interface console** : permet de jouer via le terminal.
- **Interface JavaFX** : interface graphique pour une expérience utilisateur améliorée.
- **Undo/Redo** : possibilité de défaire et refaire les coups.
- **Adversaires intelligents** :
  - Niveau 0 : adversaire aléatoire.
  - Niveau 1 : adversaire qui ne manque aucun coup gagnant.
- **Architecture MVC** : séparation claire entre le modèle, la vue et le contrôleur.

## Prérequis

- **JDK** : Liberica Full 23
- **JavaFX** : inclus dans Liberica Full 23
- **JUnit** : pour les tests unitaires
- **Maven** : pour la gestion de dépendances et la compilation
- **Git** : pour la gestion de version

## Installation

1. **Cloner le dépôt :**
   ```bash
   git clone https://git.esi-bru.be/g12345-3dev3a/oxono-g12345.git
   cd oxono-g12345
   ```

2. **Compiler le projet avec Maven :**
   ```bash
   mvn clean install
   ```

3. **Exécuter l'application :**
   - **Console :**
     ```bash
     mvn exec:java -Dexec.mainClass="g12345.dev3.oxono.console.Main"
     ```
   - **JavaFX :**
     ```bash
     mvn exec:java -Dexec.mainClass="g12345.dev3.oxono.javafx.Main"
     ```

## Utilisation

### Interface Console

1. Lancer une partie.
2. Suivre les instructions pour déplacer les totems et placer les jetons.
3. Le plateau est affiché après chaque action.

### Interface JavaFX

1. Lancer l'application.
2. Choisir la taille du plateau et le niveau de l'adversaire.
3. Jouer en utilisant les clics pour déplacer les totems et placer les jetons.

## Architecture du projet

Le projet est organisé selon le patron MVC :

- **Modèle** : `g12345.dev3.oxono.model`
  - Contient la logique du jeu, les règles et la détection de victoire.
  - Facade pour interfacer avec les vues.

- **Vue Console** : `g12345.dev3.oxono.console`
  - Interface en ligne de commande pour tester le modèle.

- **Vue JavaFX** : `g12345.dev3.oxono.javafx`
  - Interface graphique interactive.

- **Contrôleur** : gère les interactions entre la vue et le modèle.

### Design Patterns utilisés

- **MVC (Model-View-Controller)** : Séparation des responsabilités.
- **Observer** : Pour notifier la vue des changements dans le modèle.
- **Command** : Pour implémenter l'undo/redo.
- **Strategy** : Pour gérer les différentes stratégies d'adversaires automatiques.

## Tests

Les tests unitaires sont écrits avec **JUnit**.

- Pour exécuter les tests :
  ```bash
  mvn test
  ```

- Les tests se trouvent dans le répertoire `src/test/java`.

## Licence

Ce projet est sous licence **Creative Commons Paternité - Partage à l’Identique 2.0 Belgique**. [En savoir plus](http://creativecommons.org/licenses/by-sa/2.0/be/).

Pour des autorisations au-delà de cette licence, veuillez contacter : esi-atl-list@he2b.be.

---

**Date de remise** : Vendredi 13 décembre 2024 à 18h.

**Haute École Bruxelles-Brabant**  
École Supérieure d’Informatique  
2024 - 2025  
3dev3a 

