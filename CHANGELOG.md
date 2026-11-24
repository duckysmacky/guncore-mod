# Changelog

All notable changes to Guncore for Minecraft 1.12.2, newest first.

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
