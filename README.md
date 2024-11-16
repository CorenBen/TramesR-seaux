# Visualisateur de trames

##  Description

Application permettant l'analyse et la visualisation de trames réseaux. Le projet combine un analyseur Java pour le traitement des données et une interface de visualisation construite avec JavaFX.

### Analyseur (Backend)

#### Composants Principaux
* `Converter`
  - Entrée : fichiers traces (.txt)
  - Sortie : tableau de trames
  - Fonction : conversion des données brutes

* `Analyzer`
  - Traitement des trames
  - Subdivision des octets
  - Analyse détaillée
  - Conversion en format lisible

### Visualiseur (Frontend)

#### Modèle MVC
* `Info` (DTO)
  - Stockage des données analysées
  - Interface entre l'analyseur et le visualiseur

* `Controllers/`
  - `MainController` : contrôleur principal
  - Gestion de la logique applicative
  - Coordination des vues

* `Views/`
  - Interface utilisateur
  - Affichage des données
  - Interaction utilisateur

## Technologies

- **Backend**: Java
- **Frontend**: JavaFX
- **Build Tool**: Maven
- **Architecture**: MVC Pattern
