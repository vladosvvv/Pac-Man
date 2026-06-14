package model;

import javax.swing.table.AbstractTableModel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BoardTableModel extends AbstractTableModel {
    private final int rows, cols;
    private final boolean[][] maze;
    private final boolean[][] hasDot;
    private final Improvement[][] imps;
    private final Player player;
    private final List<Ghost> ghosts;
    private final ImprovementManager impManager;

    public BoardTableModel(int rows, int cols, boolean[][] maze,
                           Player player, List<Ghost> ghosts) {
        this.rows = rows;
        this.cols = cols;
        this.maze = maze;
        this.hasDot = new boolean[rows][cols];
        this.imps = new Improvement[rows][cols];
        this.player = player;
        this.ghosts = ghosts;
        this.impManager = new ImprovementManager(this, player);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!maze[r][c]) {
                    hasDot[r][c] = true;
                }
            }
        }
    }

    @Override
    public int getRowCount() {
        return rows;
    }

    @Override
    public int getColumnCount() {
        return cols;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

    public boolean isWall(int r, int c) {
        return maze[r][c];
    }

    public boolean hasDot(int r, int c) {
        return hasDot[r][c];
    }

    public void checkDotCollected(int r, int c) {
        if (hasDot[r][c]) {
            hasDot[r][c] = false;
            player.addScore(10 * player.getScoreMultiplier());
            fireTableCellUpdated(r, c);
        }
    }

    public Improvement checkImprovementCollected(int r, int c) {
        Improvement imp = imps[r][c];
        if (imp != null) {
            imps[r][c] = null;
            fireTableCellUpdated(r, c);
            return imp;
        }
        return null;
    }

    public void placeImprovementAt(Improvement imp, int r, int c) {
        imps[r][c] = imp;
        fireTableCellUpdated(r, c);
    }

    public Improvement getImprovementAt(int r, int c) {
        return imps[r][c];
    }

    public Point getRandomFreeCell() {
        List<Point> freeCells = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!maze[r][c]) {
                    if (player.getRow() == r && player.getCol() == c) continue;
                    boolean occupiedByGhost = false;
                    for (Ghost g : ghosts) {
                        if (g.getRow() == r && g.getCol() == c) {
                            occupiedByGhost = true;
                            break;
                        }
                    }
                    if (occupiedByGhost) continue;
                    if (imps[r][c] != null) continue;
                    freeCells.add(new Point(r, c));
                }
            }
        }
        if (freeCells.isEmpty()) return null;
        return freeCells.get((int) (Math.random() * freeCells.size()));
    }

    public Player getPlayer() {
        return player;
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }

    public ImprovementManager getImprovementManager() {
        return impManager;
    }

    public boolean areAllDotsCollected() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!maze[r][c] && hasDot[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    public void resetDots() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!maze[r][c]) {
                    hasDot[r][c] = true;
                }
            }
        }
        fireTableDataChanged();
    }
}
