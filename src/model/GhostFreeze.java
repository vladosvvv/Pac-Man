package model;

import java.util.List;

public class GhostFreeze implements Improvement {
    private final String id = "GhostFreeze";
    private final long duration = 5_000;
    private List<Ghost> ghosts;

    public GhostFreeze(List<Ghost> ghosts) {
        this.ghosts = ghosts;
    }

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
        for (Ghost ghost : ghosts) {
            ghost.freeze();
        }
    }

    @Override
    public void revertEffect(Player player) {
        for (Ghost ghost : ghosts) {
            ghost.unfreeze();
        }
    }
}
