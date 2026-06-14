package model;

public class Invincibility implements Improvement {
    private final String id = "Invincibility";
    private final long duration = 6_000; // 6 seconds

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
        player.setInvincible(true);
    }

    @Override
    public void revertEffect(Player player) {
        player.setInvincible(false);
    }
}