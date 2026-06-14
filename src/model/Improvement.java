package model;

import java.io.Serializable;

public interface Improvement extends Serializable {
    String getId();
    long getDurationMillis();
    void applyEffect(Player player);
    void revertEffect(Player player);
}