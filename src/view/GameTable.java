package view;

import model.BoardTableModel;
import model.Player;
import model.Direction;
import javax.swing.JTable;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

public class GameTable extends JTable {
    private final Player player;
    private final int cellSize;

    public GameTable(BoardTableModel model, Player player, int cellSize) {
        super(model);
        this.player = player;
        this.cellSize = cellSize;
        setShowGrid(false);
        setIntercellSpacing(new java.awt.Dimension(0, 0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int diameter = (int) (cellSize * 0.8);

        int baseRow = player.getRow();
        int baseCol = player.getCol();
        Rectangle cellRect = getCellRect(baseRow, baseCol, false);

        float offX = player.getOffsetX() * cellSize;
        float offY = player.getOffsetY() * cellSize;

        int cellX = cellRect.x;
        int cellY = cellRect.y;
        int drawX = (int) (cellX + offX + (cellSize - diameter) / 2f);
        int drawY = (int) (cellY + offY + (cellSize - diameter) / 2f);

        int mouthAngle = player.isMouthOpen() ? 30 : 5;
        int startAngle;
        Direction dir = player.getDirection();
        switch (dir) {
            case LEFT:
                startAngle = 180 + mouthAngle / 2;
                break;
            case RIGHT:
                startAngle = 0 + mouthAngle / 2;
                break;
            case UP:
                startAngle = 90 + mouthAngle / 2;
                break;
            case DOWN:
                startAngle = 270 + mouthAngle / 2;
                break;
            default:
                startAngle = 0;
        }
        int extentAngle = 360 - mouthAngle;

        g.setColor(Color.YELLOW);
        g.fillArc(drawX, drawY, diameter, diameter, startAngle, extentAngle);
    }
}
