// File: model/ImprovementManager.java

package model;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ImprovementManager implements Runnable {
    private static final long GENERATION_INTERVAL = 5_000;
    private static final double GENERATION_PROBABILITY = 0.25;
    private final Random random = new Random();

    private final Map<Improvement, Long> activeImprovements = new ConcurrentHashMap<>();
    private final BoardTableModel board;
    private final Player player;
    private final List<Improvement> allImprovements;
    private volatile boolean running = true;

    public ImprovementManager(BoardTableModel board, Player player) {
        this.board = board;
        this.player = player;
        this.allImprovements = Arrays.asList(
                new SpeedBoost(),
                new ExtraLife(),
                new GhostFreeze(board.getGhosts()),
                new ScoreMultiplier(),
                new Invincibility()
        );
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(GENERATION_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if (random.nextDouble() < GENERATION_PROBABILITY) {
                spawnRandomImprovementOnBoard();
            }
            checkActiveImprovementsTimeout();
        }
    }

    private void spawnRandomImprovementOnBoard() {
        Improvement sample = allImprovements.get(random.nextInt(allImprovements.size()));
        Improvement newImp;
        if (sample instanceof GhostFreeze) {
            newImp = new GhostFreeze(board.getGhosts());
        } else {
            try {
                newImp = sample.getClass().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        List<Ghost> ghosts = board.getGhosts();
        if (ghosts.isEmpty()) {
            Point freeCell = board.getRandomFreeCell();
            if (freeCell == null) return;
            board.placeImprovementAt(newImp, freeCell.x, freeCell.y);
            return;
        }

        Ghost chosenGhost = ghosts.get(random.nextInt(ghosts.size()));
        int ghostRow = chosenGhost.getRow();
        int ghostCol = chosenGhost.getCol();

        board.placeImprovementAt(newImp, ghostRow, ghostCol);
    }

    private void checkActiveImprovementsTimeout() {
        long now = System.currentTimeMillis();
        for (Map.Entry<Improvement, Long> entry : new ArrayList<>(activeImprovements.entrySet())) {
            Improvement imp = entry.getKey();
            long startTime = entry.getValue();
            if (now - startTime >= imp.getDurationMillis()) {
                imp.revertEffect(player);
                activeImprovements.remove(imp);
            }
        }
    }

    public void onPlayerCollectsImprovement(Improvement imp) {
        imp.applyEffect(player);
        if (imp.getDurationMillis() > 0) {
            activeImprovements.put(imp, System.currentTimeMillis());
        }
    }

    public String getCurrentImprovementId() {
        if (activeImprovements.isEmpty()) return null;
        return activeImprovements.keySet().iterator().next().getId();
    }

    public void stop() {
        running = false;
    }
}
