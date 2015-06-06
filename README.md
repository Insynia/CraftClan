# Projet CraftClan @Insynia
___
###### Dernière modification: 25/05/2015
###### Par Doc Cobra et Sharowin.
___

**[TRIEZ MOI](#triez-moi)**

**[Tuto](#tuto)**

**[Concept](#concept)**

**[Installation](#installation)**
* [Serveur minecraft](#serveur-minecraft)
* [Plugin](#plugin)
* [Web server](#web-server)

**[To-do list](#to-do-list)**
* [Phase 1](#phase-1)

___
# Mémo

#### Clean le README

#### Configurer Essentials et créer une zone de vente
___

## Tuto

### --- TODO ---

La zone de farming
Rejoindre une faction
Capturer un point


## Concept

Craft Clan est un mod de minecraft qui allie le survival / build et la compétitivité.
Un système de factions et de territoires permet aux joueurs de capturer des points stratégiques aux ennemis et les oblige à défendre les leurs.
Le monde est auto-suffisant avec un monde dédié au farming qui est régénéré en boucle, les joueurs les plus mal intentionnés ne peuvent pas causer de tort aux autres joueurs.
En effet tant que le joueur n'a pas créé ou rejoint une faction ainsi que capturé un point, il ne peux pas détruire ou construire sur le monde principal.


D'autres éléments seront apportés au mod au fur et à mesure, comme l'ajout d'évenement du type arène ou parcours.

## Installation

Les exemples de commandes ne sont pas compatibles avec toutes les OS
La plupart des commandes est effectuée sous ubuntu et MacOS

Le dépôt git correspondant sera initialisé, pull et sur la branche master
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

### Web server

La commande `./install.sh` crée les fichiers nécessaires
Il faut cependant vérifier les dépendances nécessaires comme `nginx`

Générer les assets en static

```bash
bundle exec rake assets:precompile
```

Lancer le service de nginx ou le reload

```bash
sudo service nginx restart
```

Lancer le serveur applicatif

```bash
DB_URL=mysql://user:password@url:port/craftclan unicorn -E production -c config/unicorn.rb
```

### Gameplay

## To-do list

#### Compte rendu de l'alpha du 01/05/15
##### [F] Problèmes liés au mode attaque:
        - [x] Certains points neutres ne sont pas capturables.
        - [ ] Problèmes liés au PVP des failers.
##### Idées
        - Mettre un bloc de GLASS en haut des tours pour éviter la capture par le haut des points.
        - Skin de faction (au moins une couleur).
        - Protection des points:
            - Coût: (20.diamants * niveau) ou (20.diamants * niveau^2)
        - Mise en place d'un PVP off au sein d'une même faction.
        - Mise en place de reminders d'attaque pour les attaquant et les défenseurs.
            - Ex: Vous devez capturer le point "point" sinon tappez /cc surrend
        - Changer le mode de capture: Le /cc capture est trop long
            - Idées: /cap /ccap ou lorsque le joueur tape sur le glass au dessus du beacon.
##### Constructions:
        - [x] Spawn débuté.
        - [x] Refonte des structures des points de niveaux 1 à 10.
            // Mettre à nouveau le -1 sur la génération des structures pour la génération des points.
            
        - [ ] Création d'une "île" en dessous des points pour protéger le pe beacon.
##### Points trop grands avec overlap sur la couche supérieure.


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