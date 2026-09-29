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
| Spawnpoint utility commands | ✅ | ✅ |
| Custom HUD | Sidebar & tab menu | Sidebar & tab menu |
| Weapon mod | Vic's Modern Warfare 2.0 | TACZ |

## Installation

Download the `.jar` matching your branch/Minecraft version from the [Releases](https://github.com/duckysmacky/guncore-mod/releases) page and drop it into your server's (and client's) `mods` folder, alongside the required weapon mod listed above.

This is **both** a client and server mod, so the version of the mod should match in both places.

---

# Guncore 1.20.1 overview

Targets Minecraft 1.20.1 / Forge. Guncore owns the whole equipment system (weapons, gadgets, armor, perks) and hands out items directly, building them straight from TACZ's item API rather than referencing pre-made kits.

## Mod dependencies

- [TACZ (Timeless and Classics Zero)](https://www.curseforge.com/minecraft/mc-mods/tacz) - source of every gun and ammo item, and the kill-attribution event Guncore listens to for stats

Guncore compiles directly against TACZ's API (`com.tacz.guns.api...`) to build gun/ammo `ItemStack`s and to detect gun kills (`EntityKillByGunEvent`), so the server must have TACZ installed for weapons and kill tracking to work at all.

## Commands

- `/menu` - opens the equipment/game GUI
- `/guncore config_reload` - reloads config from disk and refreshes the menu, without a restart
- `/guncore help` - lists all of the mod's commands
- `/game start|end|reset|pause` - controls the current round
- `/game mode <ffa|tdm|hostage>` - sets the gamemode
- `/game mode_variant <time|lives|kills>` - sets the win condition
- `/game kills|lives|deaths <add|remove|set> <player> <amount>` - edits player stats
- `/game register_kill <victim> [killer]` - registers a kill/death for the victim, attributed to `killer` (or to whoever ran the command if omitted)
- `/game kill_only_lives <true|false>` - if enabled (default), only a death caused by another player costs a life; if disabled, any death (fall damage, self-kill, etc.) does. Deaths are always counted. Also available in the menu's settings page
- `/game spawnpoint [player]` - sets the spawnpoint of the player (defaults to you) to their current location, switches them to survival and places bedrock underneath
- `/game spawnpoint_all` - same as above, for every online player (also a button in the game menu)
- `/game scoreboard` / `/game teams` - prints current stats/team rosters

## Configuration

All config is **server-side only**, under `<server_root>/config/guncore/`, generated with example content on first start, and pushed to clients over the network.

```
config/guncore/
├── game.json                    # per-gamemode round settings
└── catalog/
    ├── main-weapons.json        # primary weapon slot
    ├── secondary-weapons.json   # secondary weapon slot
    ├── lethals.json             # grenades, throwables
    ├── tacticals.json           # flashbangs, smokes, etc.
    ├── gadgets.json             # utility gadgets
    ├── utility.json             # misc utility items
    ├── consumables.json         # medkits, food, etc.
    ├── perks.json               # passive perks
    ├── armor.json               # full armor sets
    └── locations.json           # named teleport/spawn points per map
```

Edit the JSON, then run `/guncore config_reload` - the server re-reads every catalog file and reopens/refreshes the menu for connected players.

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

Each file is a JSON **array** of entries, all sharing `enabled`, `name` and `descriptionLines` (`&`-color codes supported). `enabled: false` hides an entry without deleting it.

**`main-weapons.json` / `secondary-weapons.json`** - `gunId`/`ammoId` are TACZ gun/ammo IDs; `fireMode` is optional (`null` defaults to `"SEMI"`, other values e.g. `"AUTO"`, `"BURST"` depend on what the gun supports):

```json
{
  "enabled": true,
  "name": "Glock 17",
  "category": "pistol",
  "rarity": "common",
  "gunId": "tacz:glock_17",
  "fireMode": null,
  "ammoId": "tacz:9mm",
  "ammoAmount": 32,
  "descriptionLines": ["&7The classic pistol"]
}
```
`category` is one of `assault_rifle`, `battle_rifle`, `dmr`, `lmg`, `smg`, `shotgun`, `sniper_rifle`, `pistol`, `special`, `melee`; `rarity` uses the shared values below.

**`lethals.json`, `tacticals.json`, `gadgets.json`, `utility.json`, `consumables.json`, `perks.json`** - all identical structure, any vanilla or modded item, with optional raw NBT and bundled extra items (`additionalItems`, each an `{ id, amount }`; use `[]` for none):

```json
{
  "enabled": true,
  "name": "Water Bucket",
  "rarity": "common",
  "itemId": "minecraft:water_bucket",
  "itemAmount": 1,
  "nbtData": null,
  "additionalItems": [{ "id": "minecraft:bucket", "amount": 2 }],
  "descriptionLines": ["&7A bucket filled with water."]
}
```
`nbtData` takes a raw SNBT string (e.g. `"{CustomModelData:1}"`) applied to the item; leave it `null` for plain items. `rarity` is one of `common`, `uncommon`, `rare`, `epic`, `legendary`, `mythic`, `secret`.

**`armor.json`** - a full helmet/chestplate/leggings/boots set given together:

```json
{
  "enabled": true,
  "name": "Iron Armor",
  "rarity": "common",
  "helmetItemId": "minecraft:iron_helmet",
  "chestplateItemId": "minecraft:iron_chestplate",
  "leggingsItemId": "minecraft:iron_leggings",
  "bootsItemId": "minecraft:iron_boots",
  "descriptionLines": ["&7The most basic armor"]
}
```

**`locations.json`** - named points used by teleport/spawn features, tied to a map; `mapId` currently only supports `other`:

```json
{
  "enabled": true,
  "mapId": "other",
  "name": "Spawn",
  "coordinates": { "x": 0, "y": 80, "z": 0 },
  "descriptionLines": ["&7This is an example location."]
}
```
