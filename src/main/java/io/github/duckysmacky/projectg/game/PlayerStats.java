package io.github.duckysmacky.projectg.game;

public class PlayerStats {
    private final String username;
    private int kills;
    private int deaths;
    private int lives;

    public PlayerStats(String username) {
        this.username = username;
        this.kills = 0;
        this.deaths = 0;
        this.lives = 0;
    }

    public void resetStats() {
        this.kills = 0;
        this.deaths = 0;
        this.lives = 0;
    }

    public void addKills(int count) {
        kills += count;
    }

    public void removeKills(int count) {
        kills -= count;
    }

    public void setKills(int count) {
        kills = count;
    }

    public void registerDeath() {
        deaths++;
        if (lives > 0) lives--;
    }

    public void addDeaths(int count) {
        deaths += count;
    }

    public void removeDeaths(int count) {
        deaths -= count;
    }

    public void setDeaths(int count) {
        deaths = count;
    }

    public void addLives(int count) {
        lives += count;
    }

    public void removeLives(int count) {
        lives -= count;
    }

    public void setLives(int count) {
        this.lives = count;
    }

    public String getUsername() {
        return username;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getLives() {
        return lives;
    }
}
