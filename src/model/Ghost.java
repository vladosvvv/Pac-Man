package model;

import java.awt.Color;
import java.util.Random;

public class Ghost implements Runnable {
    private int row, col;
    private Direction direction = Direction.NONE;
    private final Color color;
    private final BoardTableModel model;
    private volatile boolean running = true;
    private volatile boolean frozen = false;
    private final Random rnd = new Random();

    public Ghost(int row, int col, Color color, BoardTableModel model) {
        this.row = row;
        this.col = col;
        this.color = color;
        this.model = model;
    }

    @Override
    public void run() {
        while (running) {
            if (!frozen) {
                direction = chooseNextDirection();
                int dr = 0, dc = 0;
                switch (direction) {
                    case LEFT:  dc = -1; break;
                    case RIGHT: dc = 1;  break;
                    case UP:    dr = -1; break;
                    case DOWN:  dr = 1;  break;
                    default:    break;
                }
                int newRow = row + dr;
                int newCol = col + dc;
                if (newRow >= 0 && newRow < model.getRowCount() &&
                        newCol >= 0 && newCol < model.getColumnCount() &&
                        !model.isWall(newRow, newCol)) {
                    int oldRow = row, oldCol = col;
                    row = newRow;
                    col = newCol;
                    model.fireTableCellUpdated(oldRow, oldCol);
                    model.fireTableCellUpdated(row, col);
                }

                Player p = model.getPlayer();
                if (row == p.getRow() &&
                        col == p.getCol() &&
                        !p.isInvincible()) {
                    p.dieOneLife();

                    int oldPlayerRow = p.getRow();
                    int oldPlayerCol = p.getCol();

                    int centerR = model.getRowCount() / 2;
                    int centerC = model.getColumnCount() / 2;

                    p.setRow(centerR);
                    p.setCol(centerC);
                    p.setOffsetX(0f);
                    p.setOffsetY(0f);

                    p.setDirection(Direction.NONE);

                    p.setJustDied(true);

                    model.fireTableCellUpdated(oldPlayerRow, oldPlayerCol);
                    model.fireTableCellUpdated(centerR, centerC);
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void stop() { running = false; }

    public void freeze() { frozen = true; }
    public void unfreeze() { frozen = false; }

    private Direction chooseNextDirection() {
        Direction[] dirs = Direction.values();
        return dirs[rnd.nextInt(4)];
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public Color getColor() { return color; }
}
