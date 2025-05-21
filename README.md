# Animals BodyGuards

Animals BodyGuards est un plugin Spigot qui fait apparaître des créatures hostiles pour défendre les animaux passifs de Minecraft lorsqu’ils subissent des dégâts.

## Fonctionnement

Quand un animal protégé est attaqué ou encaisse des dégâts, le plugin peut invoquer un ou plusieurs mobs hostiles pour l’assister. Les conditions d’apparition et certains comportements se règlent dans `config.yml`.

### Options du fichier `config.yml`

- `bodyguards_die_with_their_master` : si `true`, les bodyguards meurent en même temps que l’animal qu’ils protègent.
- `special_names` : active ou non l’affichage de noms colorés pour l’animal défendu et ses gardes.
- `bodyguardsspawnsat.animalsnaturaldamages` : apparition lorsqu’un dégât naturel touche l’animal (chute, feu…).
- `bodyguardsspawnsat.entitiesdamageanimals` : apparition lorsqu’une autre entité inflige des dégâts.
- `bodyguardsspawnsat.playersdamageanimals` : apparition lorsqu’un joueur attaque l’animal.

### Commandes

`/animalsbodyguards` (alias `/animalsbg`) propose deux sous-commandes :

- `/animalsbg mode` : active ou désactive la mort des bodyguards avec leur maître.
- `/animalsbg names` : affiche ou masque les noms personnalisés des animaux et de leurs gardes.

### Compilation

Le plugin se compile avec Maven :

```bash
mvn package
```

Le fichier JAR sera disponible dans le dossier `target/`.

### Fichiers importants

- `AnimalsBodyGuards.java` : classe principale du plugin.
- `AnimalsEvent.java` : gère les événements de dégâts, décès et ciblage.
- `CommandAnimalsBodyGuards.java` : exécute la commande `/animalsbodyguards`.
- `plugin.yml` : déclare le plugin et ses commandes.

Les données de bodyguards sont sauvegardées dans `saves.yml` à l’arrêt du serveur puis rechargées au démarrage suivant.
