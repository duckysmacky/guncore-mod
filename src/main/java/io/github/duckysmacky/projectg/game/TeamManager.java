package io.github.duckysmacky.projectg.game;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeamManager {
    private static TeamManager instance;
    private Map<Team, List<UUID>> teams;

    private TeamManager() {}

    public static TeamManager instance() {
        if (instance == null) {
            instance = new TeamManager();
        }

        return instance;
    }
}
