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
| Loadouts | Premade kits (via CSG mod) **or** build-a-class (switchable) | Custom build-a-class equipment system |
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

- [Vic's Modern Warfare 2.0](https://www.curseforge.com/minecraft/mc-mods/vics-modern-warfare) - source of gun/ammo items referenced in `primary-weapons.json` / `secondary-weapons.json`. **Version 2.0 in specific**, not 1.0, 3.0 or Cubed
- [CSG Kits](https://www.curseforge.com/minecraft/mc-mods/csgo-kits) - loadout kits, given via the `csg_kit give <kit_id> <player>` command (build-a-class mode also needs a `base` kit defined in the CSG config, see below)

Guncore calls their commands/items by ID, so any compatible replacement mod would also work as long as the IDs in the catalog match.

## Commands

- `/menu` - opens the equipment/game GUI
- `/guncore config_reload` - reloads config from disk without a restart
- `/game start|end|reset|pause` - controls the current round
- `/game mode <ffa|tdm|hostage>` / `/game mode_variant <time|lives|kills>` - sets gamemode and win condition
- `/game kills|lives|deaths <add|remove|set> <player> <amount>` - edits player stats
- `/game equipment <kits|builder>` - switches the equipment selection system (default `builder`; global, saved in `settings.json`, can't be changed during a round)
- `/game loadout_system <flexible|rarity_score>` - switches how loadouts are limited (default `flexible`, see [Rarity Score system](#rarity-score-system))
- `/guncore location add <map> <name> [<x> <y> <z>]` - (ops) adds an entry to `locations.json` and refreshes the menus. Uses your position when no coordinates are given (`~` works). Wrap a name with spaces in double quotes, e.g. `"Main Spawn"`. The command rewrites the whole file, so anything in it that is not a regular location field (e.g. unknown keys) is dropped
- `/game mythics <true|false>` - enables or disables mythic items and kits (default enabled); disabled mythics are hidden from the menus and can't be equipped
- `/game world <gamerule> <true|false>` - sets a world gamerule from `world.gamerules` (saved to `settings.json`); `/game world setup` resets them to defaults and applies them
- `/game kill_only_lives <true|false>` - toggles whether only player-caused kills cost a life (vs. all deaths)
- `/game item_pickup <true|false>` - toggles whether players can pick up items (default `true`; useful when other mods still drop items)
- `/game spawnpoint [player]` / `/game spawnpoint_all` - sets spawnpoint, survival mode, and bedrock under the target(s)
- `/game scoreboard` / `/game teams` - prints current stats/team rosters
- `/game stats [player]` - opens the profile of yourself or another player (online or offline); the console gets a chat printout. The only `/game` subcommand available to non-ops

Run `/guncore help` in-game for the full up-to-date list.

Items handed out by the equipment menu (guns, ammo, build-a-class items, armor; not CSG kit contents) can't be dropped while a round isn't running. During a round they can be thrown out.

## Player profiles

Every player has a persistent profile, opened from the player head in the middle of the main menu or with `/game stats`. It shows:

- **Stats** - games played, wins, losses, win rate, kills, deaths, K/D
- **Records** - most kills in a round, best kill streak, times MVP (top player of a round), time played, average kills per round
- **Favorites** - top 5 game modes, kits, weapons (primary and secondary), lethals, tacticals, gadgets, utility, consumables and armor

Stats are saved only when a round **ends** (`/game end` or a win condition), never on `/game reset`. Favorites count the equipment a player had selected when the round **started**, so browsing items before the round doesn't count. In team modes everyone on the winning team gets a win; players on no team and everyone else get a loss.

The gold block on the profile page opens the **global stats** menu: one button per stat and record showing the top 10 players on the server, plus the 10 most picked kits, weapons, game modes and equipment across all players. Win rate, K/D and average kills per round only rank players with at least 5 games.

Profiles are stored in a SQLite database at `config/guncore/stats.db`, created on the first server start (the SQLite driver is bundled in the mod jar). Favorites are keyed by the catalog entry `name`, so renaming an entry starts a new count. Delete the file to reset all stats.

## Configuration

All config lives **server-side** only, under the server's Forge config directory (the same folder holding `config/` for every other mod - typically `<server_root>/config/guncore/`). Files are generated with example content the first time the server starts; only the server's copy matters, clients receive it over the network and never read local files.

```
config/guncore/
├── settings.json           # game / world / equipment settings (also edited in-game)
├── stats.db                # player profiles (SQLite, written by the mod, see "Player profiles")
├── menus/
│   ├── gun_categories.json     # weapon categories shown in the weapon menus
│   ├── kit_classes.json        # kit classes shown in the kits menu
│   └── maps.json               # maps shown in the locations menu
└── catalog/
    ├── kits.json               # kit-based: loadout kits (CSG Kits IDs)
    ├── kit-gadgets.json        # kit-based: gadgets
    ├── primary-weapons.json    # both modes: primary guns + ammo (Vic's Modern Warfare item IDs)
    ├── secondary-weapons.json  # both modes: secondary guns + ammo
    ├── lethals.json            # build-a-class: explosives and other killing tools
    ├── tacticals.json          # build-a-class: debuff / utility grenades
    ├── gadgets.json            # build-a-class: main special item (movement, deployables, mythic vouchers)
    ├── utility.json            # build-a-class: helpful items (used for two utility slots)
    ├── consumables.json        # build-a-class: healing, food, blocks, single-use items
    ├── armor.json              # build-a-class: full armor sets
    └── locations.json          # named teleport/spawn points per map
```

Edit the JSON, then run `/guncore config_reload` (or restart) - the server re-reads the files and pushes the updated menus and catalog to every connected client, no client-side files or restarts needed.

### `settings.json`

Created with defaults on first start and rewritten whenever a setting is changed in-game or by command, so values survive restarts. Missing values are filled with defaults on load.

- `game.mode` (`FFA`, `TDM`, `HOSTAGE`), `game.variant` (`TIME`, `LIVES`, `KILLS`) and `game.killOnlyLifeLoss` - the current game mode, win condition and life loss rule.
- `game.ffa` / `game.tdm` / `game.hostage` - per-gamemode `roundLengthSec`, `startingLives`, `killTarget`. Editable in the file or in-game: click the value in Menu -> Settings -> Game and type the new number in chat (round time in minutes; send `cancel` to abort).
- `equipment.mode` (`kits` or `builder`) - updated by `/game equipment`.
- `equipment.mythicsEnabled` - whether mythic items and kits can be selected, updated by `/game mythics`.
- `equipment.loadoutSystem` (`flexible` or `rarity_score`) - updated by `/game loadout_system`.
- `equipment.rarityPreset` / `equipment.rarityPresets` - the selected and all available rarity score presets, see [Rarity Score system](#rarity-score-system).
- `world.itemPickup` - whether players can pick up items (`true` by default).
- `world.gamerules` - gamerules (`true`/`false`). Applied to the world on server start and on `/guncore config_reload`. `keepInventory` is always forced to `true` and is not configurable.

```json
{
  "game": {
    "mode": "FFA",
    "variant": "LIVES",
    "killOnlyLifeLoss": true,
    "ffa": { "roundLengthSec": 600, "startingLives": 5, "killTarget": 15 },
    "tdm": { "roundLengthSec": 900, "startingLives": 3, "killTarget": 15 },
    "hostage": { "roundLengthSec": 1200, "startingLives": 3, "killTarget": 15 }
  },
  "world": {
    "itemPickup": true,
    "gamerules": {
      "doWeatherCycle": false,
      "doDaylightCycle": false,
      "doFireTick": false,
      "doTileDrops": false,
      "naturalRegeneration": true,
      "doMobSpawning": false
    }
  },
  "equipment": {
    "mode": "builder",
    "mythicsEnabled": true,
    "loadoutSystem": "flexible",
    "rarityPreset": "balanced",
    "rarityPresets": {
      "tight": {
        "scores": { "common": 0, "uncommon": 1, "rare": 3, "epic": 6, "legendary": 10, "mythic": 25 },
        "maxScore": { "kits": 25, "builder": 30 }
      },
      "balanced": {
        "scores": { "common": 0, "uncommon": 1, "rare": 2, "epic": 5, "legendary": 8, "mythic": 20 },
        "maxScore": { "kits": 20, "builder": 24 }
      },
      "loose": {
        "scores": { "common": 0, "uncommon": 1, "rare": 2, "epic": 3, "legendary": 5, "mythic": 20 },
        "maxScore": { "kits": 24, "builder": 32 }
      }
    }
  }
}
```

### `menus/*.json`

Sections the menus group catalog entries by: gun categories (`gun_categories.json`), kit classes (`kit_classes.json`) and maps (`maps.json`). Each file is a JSON **array**; the menu shows sections in file order, centered, and hides sections without any catalog entries. Catalog entries reference a section by its `id` (`category` for guns, `class` for kits, `mapId` for locations). `iconItemId` is any item registry name, `name` is the display name and `descriptionLines` the tooltip (`&`-color codes):

```json
{
  "enabled": true,
  "id": "assault_rifle",
  "name": "Assault Rifles",
  "iconItemId": "minecraft:iron_sword",
  "descriptionLines": ["&7Versatile automatic rifles"]
}
```
Default gun categories: `assault_rifle`, `battle_rifle`, `dmr`, `lmg`, `smg`, `shotgun`, `sniper_rifle`, `pistol`, `melee`, `special`. Kit classes and maps default to a single `default` placeholder - replace them with your own.

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
`class` is an `id` from `menus/kit_classes.json`; `tier` is one of `basic`, `advanced`, `complex`, `professional`, `forbidden`, `special`.

**`primary-weapons.json` / `secondary-weapons.json`** - `gunItemId`/`ammoItemId` are registry names from Vic's Modern Warfare (or any mod using the same item IDs). Primary guns go to hotbar slot 1, secondary to slot 2, the ammo is placed above them. The same gun may be listed in both files:

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
`category` is an `id` from `menus/gun_categories.json`; `rarity` uses the same values as items, below.

**`kit-gadgets.json`, `gadgets.json`, `lethals.json`, `tacticals.json`, `utility.json`, `consumables.json`** - same item format everywhere: any item (vanilla or modded), optionally with `damage` (metadata), `nbtData` (SNBT) and bundled extra items (max 3 are placed):

```json
{
  "enabled": true,
  "name": "Rocket Launcher",
  "rarity": "epic",
  "itemId": "somemod:rocket_launcher",
  "itemAmount": 1,
  "damage": 0,
  "nbtData": "{Charged:1b}",
  "additionalItems": [
    { "id": "somemod:rocket", "amount": 8, "damage": 0, "nbtData": "{Type:\"he\"}" }
  ],
  "descriptionLines": ["&7Useful for putting out fires or landing safely from heights."]
}
```
`damage`, `nbtData` (also inside `additionalItems`) are optional. `rarity` is one of `common`, `uncommon`, `rare`, `epic`, `legendary`, `mythic`.

**`armor.json`** - a set of armor pieces (any piece may be omitted): `helmetItemId`, `chestplateItemId`, `leggingsItemId`, `bootsItemId`, plus `name`, `rarity`, `descriptionLines`.

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
`mapId` is an `id` from `menus/maps.json`.

## Equipment modes

Switch with `/game equipment <kits|builder>` (ops, not during a round). `/menu` -> Equipment opens the menu of the active mode.

- **kits**: a CSG kit, a primary + secondary weapon (Weapons -> Primary/Secondary) and a gadget from `kit-gadgets.json`.
- **builder** (default): pick every slot yourself - primary weapon, secondary weapon, lethal, tactical, gadget, two utilities, consumable and an armor set. Hotbar slots 1-8 hold the main items (extras sit above them), so the CSG `base` kit - given by the "Get base items" button, which resets the inventory and then gives back your selection - must only use slots `0:8`, `0:17`, `0:26` and `0:35`. Mythic weapons are meant to need both "Mythic Weapon Voucher" items (one from gadgets, one from utility), this is not enforced by the mod.

Equipment can't be changed while a round is running (paused and not-started rounds are fine).

### Rarity Score system

With `/game loadout_system rarity_score` every pick costs points by its rarity, and a loadout can't go over the max score of the active equipment mode. Picks that would go over it are denied, and each accepted pick shows the loadout's score in chat. With `flexible` (default) there are no limits. Turning mythics off (`/game mythics false`) works with both systems.

- **Kit-based**: the kit, both weapons and the kit gadget count. Picking a kit resets the score to the kit's own cost, since the kit replaces the whole inventory.
- **Build-a-class**: every slot counts; replacing a slot only counts the new item.
- Kits count by tier: basic = common, advanced = uncommon, complex = rare, professional = epic, forbidden = legendary, special = mythic.

Scores come from the selected preset in `equipment.rarityPresets`. The built-in presets keep one mythic per loadout (two mythics always go over the max):

| Preset | Uncommon | Rare | Epic | Legendary | Mythic | Max (kits / builder) |
|---|---|---|---|---|---|---|
| `tight` | 1 | 3 | 6 | 10 | 25 | 25 / 30 |
| `balanced` (default) | 1 | 2 | 5 | 8 | 20 | 20 / 24 |
| `loose` | 1 | 2 | 3 | 5 | 20 | 24 / 32 |

Common items cost 0 in every built-in preset. Menu -> Settings -> Equipment selects a preset by name and edits the scores and max scores of the selected preset in chat. To add a preset, add an entry to `equipment.rarityPresets` in `settings.json` and run `/guncore config_reload`; values left out are taken from the built-in preset of the same name, or from `balanced`. Removed built-in presets are not added back.
