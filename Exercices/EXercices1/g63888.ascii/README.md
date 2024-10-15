# AsciiPaint - Mini Projet

Ce projet consiste à développer une application de dessin en console appelée **AsciiPaint**. L'objectif est de permettre la création et la gestion de formes géométriques simples (cercles, rectangles, carrés) à afficher sous forme ASCII dans la console.

## Fonctionnalités

L'application **AsciiPaint** offre les fonctionnalités suivantes :
1. Ajouter une nouvelle forme : cercle, rectangle ou carré.
2. Afficher l'illustration (dessin) dans la console.
3. Afficher la liste des formes présentes dans le dessin.
4. Déplacer une forme.
5. Supprimer une forme.
6. Changer la couleur d'une forme (représentée par un caractère).

## Structure du Code

Le projet est structuré selon l'architecture MVC (Modèle-Vue-Contrôleur) :

- **Modèle** (`g63888.dev.ascii.model`) : Contient les classes et interfaces principales pour gérer les formes et le dessin. Cela inclut les classes `Point`, `Shape`, `Circle`, `Rectangle`, `Square`, `Drawing`, et `AsciiPaint`.
- **Vue** (`g63888.dev.ascii.view`) : Responsable de l'affichage du dessin dans la console.
- **Contrôleur** (`g63888.dev.ascii.controller`) : Gère les interactions de l'utilisateur (à implémenter).

### Diagramme de Classes

Le diagramme de classes comprend les associations suivantes :
- L'interface `Shape` définit les méthodes que toute forme doit implémenter (`move`, `isInside`, `getColor`, `setColor`).
- Les classes `Circle`, `Rectangle`, et `Square` sont des implémentations concrètes des formes, étendant la classe abstraite `ColoredShape`.
- La classe `Drawing` gère une collection de formes et propose des méthodes pour accéder aux formes et leurs attributs.
- La classe `AsciiPaint` agit comme une façade pour manipuler les formes dans le dessin.

## Usage

### Méthode principale

La méthode principale se trouve dans la classe `g12345.dev.ascii.App`. Cette méthode :

1. Instancie `AsciiPaint` et la vue.
2. Ajoute quelques formes au dessin.
3. Les déplace.
4. Affiche le dessin pour vérifier que tout fonctionne correctement.

### Tests Unitaires

Les tests unitaires sont à écrire pour toutes les méthodes de la classe `AsciiPaint`, notamment pour vérifier l'ajout, le déplacement, et la suppression de formes.

## Contributeurs

- Nom du projet : AsciiPaint
- Développeur : [MARTINEZ MUZELA]
- École : Haute École Bruxelles-Brabant, École Supérieure d’Informatique

## Licence

Ce projet est réalisé dans le cadre du cours de Développement 3 (3dev3a) pour l'année académique 2024-2025.


