package model;

public class ExtraLife implements Improvement {
    private final String id = "ExtraLife";
    private final long duration = 0;

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
        player.incrementLives();
    }

    @Override
    public void revertEffect(Player player) {
    }
}