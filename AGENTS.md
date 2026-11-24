# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Project

Guncore is a Forge mod (Minecraft 1.12.2, Java 8, ForgeGradle 3) that adds a game-control layer for gun PVP servers: 
rounds (FFA / TDM / Hostage), equipment selection (CSG kits or build-a-class), stats, HUD and an in-game GUI menu. It 
adds no guns itself; it references weapon items from any weapon mod by ID (Vic's Modern Warfare 2.0 in the examples) and runs CSG Kits commands by ID. See `README.md`
for the player/admin-facing config and command docs (keep it in sync when changing commands or JSON formats).

Two version branches live in this repo: `1.12.2` (this one) and `1.20.1` (separate codebase, TACZ). Do not port code 
between them blindly.

## Commands

```
./gradlew build          # builds + reobfuscates jar (jar.finalizedBy reobfJar) -> build/libs/guncore-1.12.2-<version>.jar
./gradlew runClient      # dev client, working dir run/ (uses -noverify because weaponlib coremod breaks stackmap frames)
./gradlew runServer      # dev server, working dir run/
```

- No tests exist (`src/test` is empty). Verify by building and running in the dev client/server.
- The mod version is set in `build.gradle` (`version`); `GuncoreMod.VERSION` is a separate, stale constant - bump
`build.gradle` for releases.
- `mods/` (weapon mod jars), `run/` and `server_config/` (copy of the live server's `config/guncore` + CSG config) are 
local-only; `run/` and `server_config/` are gitignored.

## Architecture

Single mod, source split by side under `io.github.duckysmacky.guncore`:

- `common/` - shared code. `config/`, `game/` (enums/DTOs: `GameMode`, `GameState`, `Team`, `EquipmentMode`, 
`PlayerStats`, `CommandExecutor`), `network/` (packets), `util/`.
- `server/` - authoritative game logic: `GameManager` (round state machine, teams, stats, lives), 
`EquipmentManager`/`EquipmentController` (giving kits/builds/guns to players), `commands/` (`/game`, `/guncore`,
`/menu`), `events/ServerEventHandler`.
- `client/` - GUI menu (`gui/menu`: `MenuManager`/`MenuGui` + one `*MenuPage` per screen built from `entries/`), 
HUD overlays (`gui/hud`), keybinds, `game/ClientGameInfo` (client mirror of server state). `ClientProxy` vs `CommonProxy`
are wired via `@SidedProxy` in `GuncoreMod`.

### Server-authoritative model

- The server holds all state and config. Clients never read config files; `ConfigManager.load()` (server) reads JSON from the Forge config dir (`config/guncore/`), then each registry pushes its files to all clients via `SyncRegistryPacket`. Menu pages render from the client's cached registries.
- `game.json` (`GameConfig`) is server-side only and is written back on load to fill missing fields, and on `setEquipmentMode` (equipment mode persists across restarts).
- Catalog and menu sections are both `Registry`s (`common/config/registry/`): one enum of JSON files per registry, own config directory, synced via `SyncRegistryPacket`. `ConfigManager` loads them in order - menu sections (`menus/`, `MenuSectionType`: gun categories, kit classes, maps) before the catalog, since catalog entries reference section ids (`category`, `class`, `mapId`).
- Catalogs are defined by the `CatalogType` enum (file name, entry class, example supplier) - adding a catalog means a new enum value + entry class under `common/config/catalog/`, plus a menu page if it should be selectable.
- All packets are registered in `PacketHandler.init()`; the `packetId++` order defines the wire IDs, so client and server must run the same mod version and new packets must be appended consistently. Each packet is a nested `Handler` class and does its work in `addScheduledTask` on the world thread.
- GUI actions are mostly implemented by sending a packet or a command string: `CommandExecutor.execute()` runs the command directly on a server, or sends `ExecuteCommandPacket` from a client, which the server runs **as the server**. Kits are handed out this way (`csg_kit give <kitId> <player>`), so new GUI features usually mean either a new packet or a new `/game` subcommand.

### Equipment modes

`EquipmentMode.KITS` (CSG kit + primary + secondary + kit gadget) vs `BUILDER` (build-a-class: weapon/lethal/tactical/gadget/2 utilities/consumable/armor), switched via `/game equipment`, not allowed while a round is running. Builder mode relies on a CSG `base` kit using only hotbar slots `0:8`, `0:17`, `0:26`, `0:35` (see README).

## Gotchas

- Dev-environment code uses MCP names; `reobfJar` remaps to SRG for production. Test the built jar (not only `runClient`) before releasing if you touch reflection or obfuscated names.
- Tab completion and permission checks for commands are implemented per command class in `server/commands/`; new `/game` / `/guncore` subcommands need both.
