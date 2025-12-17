package me.stormyzz.wanted.statistics;

public class playerStats {
    private int kills;
    private int deaths;
    private int wantedLevel;
    private int killstreak;
    private int credits;

    public playerStats(int kills, int deaths, int wantedLevel, int killstreak, int credits) {
        this.kills = kills;
        this.deaths = deaths;
        this.wantedLevel = wantedLevel;
        this.killstreak = killstreak;
        this.credits = credits;
    }

    public int getKills() {
        return kills;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getDeaths() {
        return deaths;
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
