package model;

public class SpeedBoost implements Improvement {
    private final String id = "SpeedBoost";
    private final String description = "+50% speed boost";
    private final long duration = 10_000;

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
        player.setCurrentSpeed(player.getBaseSpeed() * 1.5);
    }

    @Override
    public void revertEffect(Player player) {
        player.setCurrentSpeed(player.getBaseSpeed());
    }
}
