# Visualisateur de trames

## Membre du groupe
 - Corentin BENEDETTI 3700291


##  Description
Le projet est divisé en deux parties :
L'analyseur écrit en Java est composé de deux classes principales
    -Converter prenant en entrée une trace .txt et renvoyant un tableau de trames
    -Analyzer prenant le tableau de trames, subdivisant chaque octet pour faciliter l'analyse et convertissant leurs informations en texte

Le visualisateur écrit avec JavaFX et Maven utilise un système model-view-controller pour gérer l'affichage et une classe de transition pour récupérer les informations de l'analyseur :
    -Info est une classe stockant seulement les informations de l'analyseur (comme un DTO)
    -Les dossiers controllers et views font partie du modèle-view-controller, ce qu'il y a surtout à retenir c'est que chaque controller a un viewer associé,
    que MainController est le chef des controller et va lancer la fenetre de visualisation.