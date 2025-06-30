package me.stormyzz.wanted.statistics;

public class playerStats {
    private int kills;
    private int deaths;
    private int wantedLevel;
    private int killstreak;

    public playerStats(int kills, int deaths, int wantedLevel, int killstreak) {
        this.kills = kills;
        this.deaths = deaths;
        this.wantedLevel = wantedLevel;
        this.killstreak = killstreak;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getWantedLevel() {
        return wantedLevel;
    }

    public void setWantedLevel(int wantedLevel) {
        this.wantedLevel = wantedLevel;
    }

    public int getKillstreak() {
        return killstreak;
    }

    public void setKillstreak(int killstreak) {
        this.killstreak = killstreak;
    }

    public void addKill() {
        this.kills++;
        this.killstreak++;
    }

    public void addDeath() {
        this.deaths++;
        this.killstreak = 0;
    }

    public void addWantedLevel() {
        this.wantedLevel++;
    }

    public void removeWantedLevel() {
        this.wantedLevel = 0;
    }

    public double getKDR() {
        if (deaths == 0) {
            return kills;
        }
        double kdr = (double) kills / deaths;
        return Math.round(kdr * 100.0) / 100.0;
    }

}
