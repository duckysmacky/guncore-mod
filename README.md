# Guncore

Guncore is a Forge mod adding a full game-control layer for gun PVP servers: round-based gamemodes (FFA, Team Deathmatch, Hostage), a kit/loadout system, live stats (kills/deaths/lives) shown via a HUD overlay and text scoreboard, and an in-game GUI menu for players and admins. It's built for the gun-PVP styled modpacks and servers, sitting on top of a separate weapon mod for the actual guns.

## Supported versions

This mod is developed on two parallel version branches:
- `1.12.2`
- `1.20.1`

They target different Minecraft versions and different underlying weapon mods, so features and configuration differ between them. Pick the branch matching your modpack's Minecraft version.

| Feature | 1.12.2 | 1.20.1 |
|---|---|---|
| Gamemodes (FFA / TDM / Hostage) | ✅ | ✅ |
| Loadouts | Premade kits (via CSG mod) | Custom build-a-class equipment system |
| Lives / kill-only death toggle | ✅ | ✅ |
| Spawnpoint utility commands | ✅ | ❌ |
| In-game GUI menu + HUD overlay | ✅ | ✅ |
| Weapon mod | Vic's Modern Warfare 2.0 | TACZ |

## Installation

Download the `.jar` matching your branch/Minecraft version from the [Releases](https://github.com/duckysmacky/guncore-mod/releases) page and drop it into your server's (and client's) `mods` folder, alongside the required weapon mod listed above.

This is **both** a client and server mod, so the version of the mod should match in both places.

---

# Guncore 1.12.2 overview

Targets Minecraft 1.12.2 / Forge. Guncore itself doesn't add guns - it manages the game around them (rounds, kits, kills/deaths/lives, teams) and expects guns and ammo to come from the weapon mod below, referenced in the catalog by their item registry IDs.

## Mod dependencies

- [Vic's Modern Warfare 2.0](https://www.curseforge.com/minecraft/mc-mods/vics-modern-warfare) - source of gun/ammo items referenced in `guns.json`. **Version 2.0 in specific**, not 1.0, 3.0 or Cubed
- [CSG Kits](https://www.curseforge.com/minecraft/mc-mods/csgo-kits) - loadout kits, given via the `csg_kit give <kit_id> <player>` command

Guncore calls their commands/items by ID, so any compatible replacement mod would also work as long as the IDs in the catalog match.

## Commands

- `/menu` - opens the equipment/game GUI
- `/guncore config_reload` - reloads config from disk without a restart
- `/game start|end|reset|pause` - controls the current round
- `/game mode <ffa|tdm|hostage>` / `/game mode_variant <time|lives|kills>` - sets gamemode and win condition
- `/game kills|lives|deaths <add|remove|set> <player> <amount>` - edits player stats
- `/game kill_only_lives <true|false>` - toggles whether only player-caused kills cost a life (vs. all deaths)
- `/game spawnpoint [player]` / `/game spawnpoint_all` - sets spawnpoint, survival mode, and bedrock under the target(s)
- `/game scoreboard` / `/game teams` - prints current stats/team rosters

Run `/guncore help` in-game for the full up-to-date list.

## Configuration

All config lives **server-side** only, under the server's Forge config directory (the same folder holding `config/` for every other mod - typically `<server_root>/config/guncore/`). Files are generated with example content the first time the server starts; only the server's copy matters, clients receive it over the network and never read local files.

```
config/guncore/
├── game.json               # per-gamemode round settings
└── catalog/
    ├── kits.json           # loadout kits (CSG Kits IDs)
    ├── guns.json           # guns + ammo (Vic's Modern Warfare item IDs)
    ├── gadgets.json        # grenades, utility items, etc.
    └── locations.json      # named teleport/spawn points per map
```

Edit the JSON, then run `/guncore config_reload` (or restart) - the server re-reads the files and pushes the updated catalog to every connected client, no client-side files or restarts needed.

### `game.json`

One block per gamemode: `roundLengthSec`, `startingLives`, `killTarget`.

```json
{
  "ffaConfig": { "roundLengthSec": 600, "startingLives": 5, "killTarget": 15 },
  "tdmConfig": { "roundLengthSec": 900, "startingLives": 3, "killTarget": 15 },
  "hostageConfig": { "roundLengthSec": 1200, "startingLives": 3, "killTarget": 15 }
}
```

### `catalog/*.json`

Each catalog file is a JSON **array** of entries. Every entry shares `enabled`, `name` and `descriptionLines` (supports `&`-color codes); the rest depends on the catalog. Set `enabled: false` to hide an entry without deleting it.

**`kits.json`** - `kitId` is what gets passed to `csg_kit give <kitId> <player>`; `variantIds` lists optional kit variants selectable via the same command:

```json
{
  "enabled": true,
  "name": "Hunter",
  "class": "assault",
  "tier": "basic",
  "kitId": "hunter",
  "iconItemId": "minecraft:iron_sword",
  "variantIds": [],
  "descriptionLines": [
    "&7The Hunter Kit is perfect for players who want a balanced loadout for various combat situations."
  ]
}
```
`class` is one of `assault`, `skirmisher`, `assassin`, `sentinel`, `special`; `tier` is one of `basic`, `advanced`, `complex`, `professional`, `forbidden`, `special`.

**`guns.json`** - `gunItemId`/`ammoItemId` are registry names from Vic's Modern Warfare (or any mod using the same item IDs):

```json
{
  "enabled": true,
  "name": "M4A1",
  "category": "assault_rifle",
  "rarity": "rare",
  "gunItemId": "vicsmodernwarfare:m4a1",
  "ammoItemId": "vicsmodernwarfare:ammo_556x45",
  "ammoItemAmount": 90,
  "descriptionLines": ["&7A reliable, all-purpose assault rifle."]
}
```
`category` is one of `assault_rifle`, `battle_rifle`, `dmr`, `lmg`, `smg`, `shotgun`, `sniper_rifle`, `sidearm`, `melee`, `special`; `rarity` uses the same values as gadgets, below.

**`gadgets.json`** - any item (vanilla or modded), optionally bundled with extra items:

```json
{
  "enabled": true,
  "name": "Water Bucket",
  "rarity": "common",
  "itemId": "minecraft:water_bucket",
  "itemAmount": 1,
  "additionalItemIds": [],
  "descriptionLines": ["&7Useful for putting out fires or landing safely from heights."]
}
```
`rarity` is one of `common`, `uncommon`, `rare`, `epic`, `legendary`, `mythic`.

**`locations.json`** - named points used by teleport/spawn features, tied to a map:

```json
{
  "enabled": true,
  "mapId": "newport",
  "name": "Spawn",
  "coordinates": { "x": 0, "y": 80, "z": 0 },
  "descriptionLines": ["&7The main spawn point of the city."]
}
```
`mapId` is one of `newport`, `radiant`, `shmar`, `audia`, `city17`.
