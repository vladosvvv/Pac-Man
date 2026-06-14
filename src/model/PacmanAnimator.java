package model;

import javax.swing.SwingUtilities;
import view.GameTable;

public class PacmanAnimator implements Runnable {
    private final Player player;
    private final BoardTableModel model;
    private final GameTable table;

    private final int framesPerCell = 8;
    private final long baseFrameDelay = 30;
    private final int mouthToggleIntervalFrames = 20;

    private boolean running = true;

    public PacmanAnimator(Player player, BoardTableModel model, GameTable table) {
        this.player = player;
        this.model = model;
        this.table = table;
    }

    @Override
    public void run() {
        int mouthFrameCounter = 0;
        Direction activeDir = Direction.NONE;

        int teleportRow = model.getRowCount() / 2;

        while (running) {
            if (player.isJustDied()) {
                player.setOffsetX(0f);
                player.setOffsetY(0f);
                player.setMouthOpen(false);
                activeDir = Direction.NONE;
                player.setDirection(Direction.NONE);
                player.setJustDied(false);
                SwingUtilities.invokeLater(table::repaint);
            }

            if (player.getOffsetX() == 0f && player.getOffsetY() == 0f) {
                activeDir = player.getDirection();
            }

            if (activeDir == Direction.NONE) {
                mouthFrameCounter++;
                if (mouthFrameCounter >= mouthToggleIntervalFrames) {
                    mouthFrameCounter = 0;
                    player.setMouthOpen(!player.isMouthOpen());
                    SwingUtilities.invokeLater(table::repaint);
                }
                sleepFrame();
                continue;
            }

            int dr = 0, dc = 0;
            switch (activeDir) {
                case LEFT:  dc = -1; break;
                case RIGHT: dc = 1;  break;
                case UP:    dr = -1; break;
                case DOWN:  dr = 1;  break;
                default:    break;
            }

            int oldRow = player.getRow();
            int oldCol = player.getCol();
            int newRow = oldRow + dr;
            int newCol = oldCol + dc;

            if (oldRow == teleportRow) {
                if (activeDir == Direction.LEFT && oldCol == 0) {
                    newCol = model.getColumnCount() - 1;
                }
                else if (activeDir == Direction.RIGHT && oldCol == model.getColumnCount() - 1) {
                    newCol = 0;
                }
            }
            if (newRow >= 0 && newRow < model.getRowCount() &&
                    newCol >= 0 && newCol < model.getColumnCount() &&
                    !model.isWall(newRow, newCol)) {

                boolean interruptedByDeath = false;

                for (int frame = 1; frame <= framesPerCell; frame++) {
                    if (player.isJustDied()) {
                        interruptedByDeath = true;
                        break;
                    }

                    float fraction = (float) frame / framesPerCell;
                    player.setOffsetX(dc * fraction);
                    player.setOffsetY(dr * fraction);

                    mouthFrameCounter++;
                    if (mouthFrameCounter >= mouthToggleIntervalFrames) {
                        mouthFrameCounter = 0;
                        player.setMouthOpen(!player.isMouthOpen());
                    }

                    SwingUtilities.invokeLater(table::repaint);
                    sleepFrame();
                }

                if (interruptedByDeath) {
                    continue;
                }

                player.setRow(newRow);
                player.setCol(newCol);
                player.setOffsetX(0f);
                player.setOffsetY(0f);
                player.setMouthOpen(false);
                SwingUtilities.invokeLater(table::repaint);

                model.checkDotCollected(newRow, newCol);
                Improvement imp = model.checkImprovementCollected(newRow, newCol);
                if (imp != null) {
                    model.getImprovementManager().onPlayerCollectsImprovement(imp);
                }
            } else {
                player.setOffsetX(0f);
                player.setOffsetY(0f);

                mouthFrameCounter++;
                if (mouthFrameCounter >= mouthToggleIntervalFrames) {
                    mouthFrameCounter = 0;
                    player.setMouthOpen(!player.isMouthOpen());
                    SwingUtilities.invokeLater(table::repaint);
                }
                sleepFrame();
            }
        }
    }
    private void sleepFrame() {
        double speed = player.getCurrentSpeed();
        long actualDelay = (long) (baseFrameDelay / speed);
        if (actualDelay < 1) actualDelay = 1;
        try {
            Thread.sleep(actualDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void stop() {
        running = false;
    }
}
