# Projet CraftClan @Insynia
___
###### Dernière modification: 25/05/2015
###### Par Doc Cobra et Sharowin.
___

**[TRIEZ MOI](#triez-moi)**

**[Installation](#installation)**
* [Serveur minecraft](#serveur-minecraft)
* [Plugin](#plugin)
* [Web listener](#web-listener)
* [Web server](#web-server)

**[Concept](#concept)**
* [Gameplay](#gameplay)

**[To-do list](#to-do-list)**
* [Phase 1](#phase-1)

___
# TRIEZ MOI

#### Mode attaque limité a un try, respawn desactive(mute) attaque pour le player only
* Fix le tryhard de se planquer en mode attaque pour avoir des vagues attaque infinies

#### Bloc or pour la construciton sur base ennemi en mode attaque
* Cout d'attaque
* Eviter le cheat du style skybridge

#### Zone de farming exterieure et aleatoire accessible au spawn
* Fix des first player coincés
* Permet de regenerer la map sur une parcelle automatiquement donc no grief

#### Les points proches du spawn doivent etre plus petit dans l'ensemble
* Permet une grande competitivité autour du spawn (dur de garder plusieurs points)
* Facilite l'integration des nouveaux joueurs

___

## Installation

Les exemples de commandes ne sont pas compatibles avec toutes les OS
La plupart des commandes est effectuée sous ubuntu et MacOS

Le dépôt git sera initialisé, pull et sur la branche master
Le pwd est celui du depot git local

### Serveur minecraft

```bash
cd server
````

3 variables d'envirronnement doivent être définies:
- DB_USERNAME
- DB_URL
- DB_PASSWORD

```bash
export [VAR]=[VALEUR]
```

La commande `./start.sh` lancera le serveur et le web listener sur la base de donnée définie par les variables définies ci dessus
Un terminal indépendant sera créé avec `screen`, ce qui permettra de rattacher le terminal aec la commande `screen -r minecraft`

### Plugin

Le plugin doit être compilé à chaque modification en fichier JAR

```bash
cd server
```

Copier le fichier JAR dans `./plugins/`

### Web listener

### Web server

## Concept

Le concept du mod se reunut en 4 efpoksdgfj...

### Gameplay

## To-do list

** Phase I:
      Gameplay:
		Faction:
			- Générée par les joueurs.
			- Doivent pouvoir capturer des points de contrôle.

		Point de Contrôle:
		        - Créés par les administateurs.
			- Permettent d'établir une zone de contrôle.
			- Doit être capturé par une faction.
			- Confère des avantages:
			  	  ° Donne X %SoftCurrency tous les X unités de temps, à tous les membres de la faction.
			- Peut être amélioré:
			       	  ° Coût en "offrandes".
				  ° Augmentation de la durée de capture par les factions adverses.
				  ? Augmente la quantité de %SoftCurrency donnée par intervalle de temps.

	        Zone de capture:
		     	- Zone de ?x?x? autour du point de contrôle.
			- Zone au centre de la zone de contrôle.

		Zone de Contrôle:
		     	- Zone délimitée autour d'un point de contrôle. Dimension ?*?*MaxHeight.
			- Permet la construction de bâtiments dans la zone.
			- Seule la faction possédant le point de contrôle est capable de modifier l'intérieur de la zone de contrôle.

		Capture de points:
			- Toute faction se tenant sur la zone de capture peut capturer le dit point.
			- La capture du point nécessite:
			     ° Que la faction qui capture le point possède au moins 1 joueur vivant positionné sur la zone de capture.
		
		%Soft Currency:

		%HardCurrency: