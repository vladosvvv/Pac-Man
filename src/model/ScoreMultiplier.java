package model;

public class ScoreMultiplier implements Improvement {
    private final String id = "ScoreMultiplier";
    private final String description = "2x score for 8 seconds";
    private final long duration = 8_000;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getDurationMillis() {
        return duration;
    }

    @Override
    public void applyEffect(Player player) {
        player.setScoreMultiplier(2);
    }

    @Override
    public void revertEffect(Player player) {
        player.setScoreMultiplier(1);
    }
}