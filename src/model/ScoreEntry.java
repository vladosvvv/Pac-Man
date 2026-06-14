package model;

import java.io.Serializable;

public class ScoreEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String playerName;
    private final int score;
    private final long timestamp;

    public ScoreEntry(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
        this.timestamp = System.currentTimeMillis();
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return playerName + " - " + score;
    }
}