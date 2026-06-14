package view;

import model.BoardTableModel;
import model.Improvement;
import model.Ghost;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class CellRenderer extends JPanel implements TableCellRenderer {
    private final BoardTableModel board;
    private final List<Ghost> ghosts;
    private int currentRow = -1;
    private int currentCol = -1;

    public CellRenderer(BoardTableModel board, List<Ghost> ghosts) {
        this.board = board;
        this.ghosts = ghosts;
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        this.currentRow = row;
        this.currentCol = column;
        int cellWidth = table.getColumnModel().getColumn(column).getWidth();
        int cellHeight = table.getRowHeight(row);
        setPreferredSize(new Dimension(cellWidth, cellHeight));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        if (board.isWall(currentRow, currentCol)) {
            g.setColor(Color.BLUE.darker());
            g.fillRect(0, 0, w, h);
            return;
        }

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        Improvement imp = board.getImprovementAt(currentRow, currentCol);
        if (imp != null) {
            g.setColor(Color.YELLOW);
            int diameterImp = Math.min(w, h) / 2;
            g.fillOval((w - diameterImp) / 2, (h - diameterImp) / 2, diameterImp, diameterImp);
            return;
        }

        if (board.hasDot(currentRow, currentCol)) {
            g.setColor(Color.WHITE);
            int dotSize = Math.min(w, h) / 8;
            g.fillOval((w - dotSize) / 2, (h - dotSize) / 2, dotSize, dotSize);
        }

        for (Ghost ghost : ghosts) {
            if (ghost.getRow() == currentRow && ghost.getCol() == currentCol) {
                drawGhost(g, w, h, ghost);
                return;
            }
        }
    }

    private void drawGhost(Graphics g, int w, int h, Ghost ghost) {
        g.setColor(ghost.getColor());
        int width = w - 4;
        int height = h - 4;
        g.fillOval(2, 2, width, height);

        int waveCount = 3;
        int waveWidth = width / waveCount;
        for (int i = 0; i < waveCount; i++) {
            g.fillArc(2 + i * waveWidth, 2 + height / 2,
                    waveWidth, height / 2, 0, 180);
        }

        g.setColor(Color.WHITE);
        int eyeWidth = width / 5;
        int eyeHeight = height / 5;
        g.fillOval(2 + width / 4, 2 + height / 4, eyeWidth, eyeHeight);
        g.fillOval(2 + width * 2 / 4, 2 + height / 4, eyeWidth, eyeHeight);

        g.setColor(Color.BLACK);
        int pupilSize = eyeWidth / 2;
        g.fillOval(2 + width / 4 + eyeWidth / 4,
                2 + height / 4 + eyeHeight / 4, pupilSize, pupilSize);
        g.fillOval(2 + width * 2 / 4 + eyeWidth / 4,
                2 + height / 4 + eyeHeight / 4, pupilSize, pupilSize);
    }
}