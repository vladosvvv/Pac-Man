package model;

import java.awt.Point;
import java.util.*;

public class MazeGenerator {
    private final int rows;
    private final int cols;
    private final boolean[][] maze;
    private final Random rnd = new Random();

    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.maze = new boolean[rows][cols];
    }

    public boolean[][] generate() {
        //заповнюємо всю матрицю стінами
        for (int r = 0; r < rows; r++) {
            Arrays.fill(maze[r], true);
        }

        //робимо коридори шириною в одну клітину
        int startR = (rnd.nextInt((rows - 1) / 2) * 2) + 1;
        int startC = (rnd.nextInt((cols - 1) / 2) * 2) + 1;
        carve(startR, startC);

        //додаткові прорізи кожну 5 стінку + коридор
        addExtraPassages();

        //телепорти посередині лівого/правого краю
        int midRow = rows / 2;
        maze[midRow][0] = false;
        maze[midRow][cols - 1] = false;

        //забезпечуємо досяжність з центра
        enforceReachabilityFromCenter();

        //відкриваємо стінки праворуч
        int beforeRightCol = cols - 2;
        if (beforeRightCol >= 0) {
            for (int r = 0; r < rows; r++) {
                maze[r][beforeRightCol] = false;
            }
        }

        //відкриваємо стінки знизу
        int beforeBottomRow = rows - 2;
        if (beforeBottomRow >= 0) {
            for (int c = 0; c < cols; c++) {
                maze[beforeBottomRow][c] = false;
            }
        }

        //поновлюємо зовнішню обвідку
        for (int c = 0; c < cols; c++) {
            maze[0][c] = true;
            maze[rows - 1][c] = true;
        }
        //лівий та правий край
        for (int r = 0; r < rows; r++) {
            maze[r][0] = true;
            maze[r][cols - 1] = true;
        }
        //відкриваємо лише телепорти в зовнішній обвідці
        maze[midRow][0] = false;
        maze[midRow][cols - 1] = false;

        return maze;
    }

    private void carve(int r, int c) {
        maze[r][c] = false;

        List<Point> directions = Arrays.asList(
                new Point(-2, 0),
                new Point(2, 0),
                new Point(0, -2),
                new Point(0, 2)
        );
        Collections.shuffle(directions, rnd);

        for (Point dir : directions) {
            int nr = r + dir.x;
            int nc = c + dir.y;
            if (inBounds(nr, nc) && maze[nr][nc]) {
                maze[r + dir.x / 2][c + dir.y / 2] = false;
                carve(nr, nc);
            }
        }
    }

    private boolean inBounds(int r, int c) {
        return r > 0 && r < rows - 1 && c > 0 && c < cols - 1;
    }

    private void addExtraPassages() {
        int wallCount = 0;
        for (int r = 1; r < rows - 1; r++) {
            for (int c = 1; c < cols - 1; c++) {
                if (maze[r][c]) {
                    wallCount++;
                    if (wallCount % 5 == 0) {
                        maze[r][c] = false;
                    }
                }
            }
        }
    }

    private void enforceReachabilityFromCenter() {
        boolean[][] visited = new boolean[rows][cols];
        Queue<Point> queue = new LinkedList<>();

        int centerR = rows / 2;
        int centerC = cols / 2;
        if (maze[centerR][centerC]) {
            boolean found = false;
            int[] dr = {0, 1, 0, -1};
            int[] dc = {1, 0, -1, 0};
            for (int i = 0; i < 4 && !found; i++) {
                int nr = centerR + dr[i], nc = centerC + dc[i];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !maze[nr][nc]) {
                    centerR = nr;
                    centerC = nc;
                    found = true;
                }
            }
            if (maze[centerR][centerC]) {
                return;
            }
        }

        visited[centerR][centerC] = true;
        queue.add(new Point(centerR, centerC));
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            for (int i = 0; i < 4; i++) {
                int nr = p.x + dr[i];
                int nc = p.y + dc[i];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !maze[nr][nc] && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    queue.add(new Point(nr, nc));
                }
            }
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!maze[r][c] && !visited[r][c]) {
                    maze[r][c] = true;
                }
            }
        }
    }
}
