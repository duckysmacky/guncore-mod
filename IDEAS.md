## General
- [x] Add READMEs
- [ ] Add build workflow
- [ ] Make auto
- [ ] Replace CSG with custom kit setter
- [x] Restrict changing equipment when game is active (allow in paused state), so players won't cheat
- [ ] Make gun categories be configurable via .json config, should include "kind" (primary/secondary) and custom icons (default: current)
- [ ] Make maps be configurable via .json config
- [ ] Add proper map switching mid-game: add /game map <name> command & GUI buttons, teleport players to set map (re-generated beforehand)
- [ ] Include map download links in README (version-specific)
- [ ] Include live server config in repository for download (version-specific)

## 1.12.2
- [x] Add `/game spawnpoint` command to set spawnpoint + set gamemode to survival + set bedrock under player
- [x] Add a button in "Game" menu to toggle the same `/game spawnpoint` behavior but for all players
- [x] Add game setting (in the settings page) to enable/disable kill-only death-registration: if enabled, only if other player kills another player, a kill + death will be registed. if its off, all the deaths (like fall damage, self-kill) will count toward deaths
- [x] Fix bug where player with most kills wins a ffa game (should be the one left alive), check if simiar bug exists for teams gamemode
- [x] Add live scoreboard/teams as part of the GUI on the right side of the screen
- [x] Add support for build-a-class system similar to 1.20.1
- [ ] Make kit categories be configurable via .json config
- [ ] Expand kit variants on click

