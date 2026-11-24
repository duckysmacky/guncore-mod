# Changelog

All notable changes to Guncore for Minecraft 1.12.2, newest first.

## v0.7.0

#### Added

- **Player profiles**: every player has a saved profile with stats (games, wins, losses, K/D), records (best kill streak, times MVP, time played) and top 5 favorites for game modes, kits, weapons and equipment. Open it from the player head in the main menu or with `/game stats [player]`. Stats are saved to `config/guncore/stats.db` when a round ends.
- **Global stats menu**: opens from the profile page and shows the top 10 players for each stat and record, plus the most picked kits, weapons, game modes and equipment.
- **Rarity Score loadout system**: switch with `/game loadout_system rarity_score`. Each pick costs points by its rarity, and a loadout can't go over the max score of the active equipment mode. Comes with `tight`, `balanced` and `loose` presets that can be edited in the settings menu or `settings.json`. The loadout's score is shown in item lore, and gun names are colored by rarity.
- `/game mythics <true|false>` to turn mythic items and kits on or off.
- **World settings**: `/game item_pickup <true|false>` controls whether players can pick up items, and `/game world <gamerule> <true|false>` sets gamerules. Both are saved and applied on server start and config reload.
- **Menu config**: gun categories, kit classes and maps are now defined in `config/guncore/menus/*.json` and shown as centered category pages.
- Random buttons for weapons, kits and locations at the top of every selection menu.
- Pagination for menus with more entries than fit on one page.
- Edit per-gamemode round time, lives and kill target in chat by clicking the value in the settings menu.
- `/guncore location add <map> <name> [<x> <y> <z>]` to add a location from in-game, using your position by default.
- `/getitemnbt` to print the held item's id, damage and NBT in a clickable chat message, in the format used by the item catalogs.

#### Changed

- **Custom Starting Gear (CSG Kits) is now a required dependency.** Guncore won't load without it, on the server or on any client.
- `game.json` is replaced by `settings.json`, which also holds equipment and world settings and is saved whenever a setting changes. Settings from an old `game.json` are not carried over.
- The settings menu is split into Game, World and Equipment pages.
- Kit tiers now map to rarities, and weapon menu icons use vanilla items instead of Modern Warfare items.
- Setting changes are announced to players with a consistent message.
- Items handed out by the equipment menu can't be dropped, so the selection rules can't be bypassed by dropping and re-picking up items.

## v0.6.2

#### Added

- Tab completion for every `/game` and `/guncore` subcommand.

#### Changed

- The selected equipment mode is now saved in `game.json` and kept across server restarts.
- `/guncore config_reload` is now limited to operators, and it refreshes the menu of every online player instead of only the sender.

## v0.6.1

#### Added

- Teammates get a glowing outline in team modes, so allies are easy to spot.

#### Changed

- Vanilla teams are created automatically when a player joins a team, and their colors match the mod teams.

## v0.6.0

#### Added

- **Build-a-class equipment mode**: players assemble their own loadout from a weapon, lethal, tactical, gadget, two utilities, a consumable and armor, instead of picking a premade kit. Adds an armor catalog and new build menus.
- `/game equipment` command to switch between kit mode and build-a-class mode.

#### Changed

- Guns are split into primary and secondary weapon catalogs, each with its own category menu.
- Equipment can no longer be changed while a round is running.

#### Fixed

- Wrong gamerule names in the settings menu.

## v0.5.0

#### Added

- **Kill-based win condition**: a round can now end when a player (or team) reaches a kill target. Set it per gamemode with `killTarget` and switch with `/game mode_variant kills`.
- Kill-only life loss setting: lose a life only to player kills instead of any death.
- `/game spawnpoint` and `/game spawnpoint_all` to set spawnpoints for players, with a button in the game menu.
- **Sidebar scoreboard** and a **tab-list scoreboard** drawn in the GUI.
- A proper README with the config and command docs.

#### Changed

- `/game register_death` is replaced by `/game register_kill <victim> [killer]`.
- Reworked HUD overlay design and more accurate round duration.
- The mod config system and the `/guncore` command were refactored.

#### Fixed

- The player with the most kills could win over the last player alive in the Lives gamemode.
- Config was not loading on the client side.
- Config reload bug.

## v0.4.0

First release under the name Guncore, for Minecraft 1.12.2.

#### Added

- **Rounds** with three gamemodes (FFA, Team Deathmatch and Hostage) and time or lives win conditions, controlled with the `/game` command.
- **Server-side game logic**: the server holds the game state, config and equipment, and works on dedicated servers.
- **In-game menu** (`/menu` or a keybind) to join teams, pick kits, guns and gadgets, choose locations, toggle settings and run game events. Menu pages are filled dynamically from the catalogs.
- **Loadouts** from CSG Kits, plus guns and gadgets, all defined in JSON catalogs under `config/guncore`. Melee weapons count as main weapons.
- Random kit selection and location teleporting.
- Player stats (kills, deaths and lives), editable with `/game kills|lives|deaths`, shown on a basic info HUD and a text scoreboard.
- `/game register_death` to count a death by hand.
- `/guncore config_reload` to reload config without a restart. The server pushes the updated config to all clients.
- Global sound playing support.
