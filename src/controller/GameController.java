package controller;

import model.*;
import view.CellRenderer;
import view.MainMenu;
import view.GameTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private JFrame frame;
    private GameTable table;
    private BoardTableModel boardTableModel;
    private Player player;
    private List<Ghost> ghosts;
    private List<Thread> ghostThreads;
    private ImprovementManager impManager;
    private Thread impThread;
    private PacmanAnimator pacmanAnimator;
    private Thread pacThread;
    private HighScoreManager highScoreManager;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JLabel timeLabel;
    private JLabel improvementLabel;
    private Thread gameLoopThread;
    private volatile boolean gameRunning;
    private int elapsedTime;

    private static final int CELL_SIZE = 30;
    private int[][] spawnCorners;

    public GameController() {
        highScoreManager = new HighScoreManager();
        showMainMenu();
    }

    private void showMainMenu() {
        if (frame != null) {
            frame.dispose();
        }
        frame = new JFrame("Pacman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        MainMenu menu = new MainMenu(this);
        frame.add(menu, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        setGlobalKeyBinding();
    }

    public void launchNewGameDialog() {
        JDialog dialog = new JDialog(frame, "New Game", true);
        Image bg = new ImageIcon(getClass().getClassLoader().getResource("resources/window_bg.png")).getImage();
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(null);
        JLabel rowsLabel = new JLabel("Enter rows (10–100):");
        rowsLabel.setBounds(20, 20, 150, 25);
        JTextField rowsField = new JTextField();
        rowsField.setBounds(180, 20, 100, 25);
        JLabel colsLabel = new JLabel("Enter cols (10–100):");
        colsLabel.setBounds(20, 60, 150, 25);
        JTextField colsField = new JTextField();
        colsField.setBounds(180, 60, 100, 25);
        JButton okButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/button_bg.png")));
        okButton.setText("OK");
        okButton.setHorizontalTextPosition(SwingConstants.CENTER);
        okButton.setVerticalTextPosition(SwingConstants.CENTER);
        okButton.setOpaque(false);
        okButton.setContentAreaFilled(false);
        okButton.setBorderPainted(false);
        okButton.setBounds(40, 110, 100, 40);
        JButton cancelButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/button_bg.png")));
        cancelButton.setText("Cancel");
        cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        cancelButton.setVerticalTextPosition(SwingConstants.CENTER);
        cancelButton.setOpaque(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setBounds(160, 110, 100, 40);
        panel.add(rowsLabel);
        panel.add(rowsField);
        panel.add(colsLabel);
        panel.add(colsField);
        panel.add(okButton);
        panel.add(cancelButton);
        dialog.setContentPane(panel);
        dialog.setSize(320, 200);
        dialog.setLocationRelativeTo(frame);
        okButton.addActionListener(e -> {
            try {
                int rows = Integer.parseInt(rowsField.getText());
                int cols = Integer.parseInt(colsField.getText());
                if (rows < 10 || rows > 100 || cols < 10 || cols > 100) {
                    throw new NumberFormatException();
                }
                dialog.dispose();
                startNewGame(rows, cols);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input. Please enter integer numbers between 10 and 100.");
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void startNewGame(int rows, int cols) {
        stopAllThreadsAndTasks();
        player = new Player();
        boolean[][] maze = new MazeGenerator(rows, cols).generate();
        int centerR = rows / 2;
        int centerC = cols / 2;
        if (maze[centerR][centerC]) {
            maze[centerR][centerC] = false;
        }
        spawnCorners = new int[][] {
                {1, 1},
                {1, cols - 2},
                {rows - 2, 1},
                {rows - 2, cols - 2}
        };
        for (int[] corner : spawnCorners) {
            int r = corner[0], c = corner[1];
            if (maze[r][c]) {
                maze[r][c] = false;
            }
        }
        ghosts = new ArrayList<>();
        boardTableModel = new BoardTableModel(rows, cols, maze, player, ghosts);
        player.setRow(centerR);
        player.setCol(centerC);
        player.setOffsetX(0f);
        player.setOffsetY(0f);
        table = new GameTable(boardTableModel, player, CELL_SIZE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(CELL_SIZE);
        for (int i = 0; i < cols; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(CELL_SIZE);
        }
        CellRenderer cellRenderer = new CellRenderer(boardTableModel, ghosts);
        table.setDefaultRenderer(Object.class, cellRenderer);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(cols * CELL_SIZE, rows * CELL_SIZE));
        JComponent root = frame.getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();
        String MOVE_LEFT  = "MOVE_LEFT";
        String MOVE_RIGHT = "MOVE_RIGHT";
        String MOVE_UP    = "MOVE_UP";
        String MOVE_DOWN  = "MOVE_DOWN";
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0), MOVE_LEFT);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0), MOVE_RIGHT);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0), MOVE_UP);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0), MOVE_DOWN);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A,0), MOVE_LEFT);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0), MOVE_RIGHT);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W,0), MOVE_UP);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,0), MOVE_DOWN);
        am.put(MOVE_LEFT, new AbstractAction() {
            public void actionPerformed(ActionEvent e) { player.setDirection(Direction.LEFT); }
        });
        am.put(MOVE_RIGHT, new AbstractAction() {
            public void actionPerformed(ActionEvent e) { player.setDirection(Direction.RIGHT); }
        });
        am.put(MOVE_UP, new AbstractAction() {
            public void actionPerformed(ActionEvent e) { player.setDirection(Direction.UP); }
        });
        am.put(MOVE_DOWN, new AbstractAction() {
            public void actionPerformed(ActionEvent e) { player.setDirection(Direction.DOWN); }
        });
        JPanel infoPanel = new JPanel();
        scoreLabel = new JLabel("Score: 0");
        livesLabel = new JLabel("Lives: " + player.getLives());
        timeLabel = new JLabel("Time: 0");
        improvementLabel = new JLabel("Improvement: none");
        infoPanel.add(scoreLabel);
        infoPanel.add(livesLabel);
        infoPanel.add(timeLabel);
        infoPanel.add(improvementLabel);
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());
        frame.add(infoPanel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        table.repaint();
        impManager = boardTableModel.getImprovementManager();
        impThread = new Thread(impManager, "Improvement-Thread");
        impThread.start();
        spawnGhosts();
        pacmanAnimator = new PacmanAnimator(player, boardTableModel, table);
        pacThread = new Thread(pacmanAnimator, "Pacman-Animator");
        pacThread.start();
        startGameLoop();
        setGlobalKeyBinding();
    }

    private void spawnGhosts() {
        if (ghostThreads != null) {
            for (Ghost g : ghosts) {
                g.stop();
            }
        }
        ghosts.clear();
        ghostThreads = new ArrayList<>();
        Color[] colors = { Color.RED, Color.PINK, Color.CYAN, Color.ORANGE };
        for (int i = 0; i < spawnCorners.length; i++) {
            int r = spawnCorners[i][0];
            int c = spawnCorners[i][1];
            Ghost ghost = new Ghost(r, c, colors[i], boardTableModel);
            ghosts.add(ghost);
            Thread t = new Thread(ghost, "Ghost-" + i);
            t.start();
            ghostThreads.add(t);
        }
    }

    private void startGameLoop() {
        gameRunning = true;
        elapsedTime = 0;
        SwingUtilities.invokeLater(() -> {
            timeLabel.setText("Time: 0");
            scoreLabel.setText("Score: " + player.getScore());
            livesLabel.setText("Lives: " + player.getLives());
            improvementLabel.setText("Improvement: none");
        });
        gameLoopThread = new Thread(() -> {
            while (gameRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                elapsedTime++;
                SwingUtilities.invokeLater(() -> {
                    timeLabel.setText("Time: " + elapsedTime);
                    scoreLabel.setText("Score: " + player.getScore());
                    livesLabel.setText("Lives: " + player.getLives());
                    String currentImp = impManager.getCurrentImprovementId();
                    improvementLabel.setText("Improvement: " + (currentImp != null ? currentImp : "none"));
                });
                if (boardTableModel.areAllDotsCollected()) {
                    SwingUtilities.invokeLater(this::startNextRound);
                }
                if (player.getLives() <= 0) {
                    gameRunning = false;
                    SwingUtilities.invokeLater(this::endGame);
                    break;
                }
            }
        }, "Game-Loop");
        gameLoopThread.start();
    }

    private void startNextRound() {
        boardTableModel.resetDots();
        int centerR = boardTableModel.getRowCount() / 2;
        int centerC = boardTableModel.getColumnCount() / 2;
        int oldRow = player.getRow();
        int oldCol = player.getCol();
        player.setRow(centerR);
        player.setCol(centerC);
        player.setOffsetX(0f);
        player.setOffsetY(0f);
        player.setDirection(Direction.NONE);
        player.setJustDied(true);
        boardTableModel.fireTableCellUpdated(oldRow, oldCol);
        boardTableModel.fireTableCellUpdated(centerR, centerC);
        spawnGhosts();
    }

    private void endGame() {
        if (pacmanAnimator != null) pacmanAnimator.stop();
        if (impManager != null) impManager.stop();
        if (ghosts != null) {
            for (Ghost g : ghosts) g.stop();
        }
        gameRunning = false;
        if (gameLoopThread != null && gameLoopThread.isAlive()) gameLoopThread.interrupt();
        JDialog dialog = new JDialog(frame, "Game Over", true);
        Image bg = new ImageIcon(getClass().getClassLoader().getResource("resources/window_bg.png")).getImage();
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(null);
        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setBounds(20, 20, 120, 25);
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 20, 150, 25);
        JButton okButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/button_bg.png")));
        okButton.setText("OK");
        okButton.setHorizontalTextPosition(SwingConstants.CENTER);
        okButton.setVerticalTextPosition(SwingConstants.CENTER);
        okButton.setOpaque(false);
        okButton.setContentAreaFilled(false);
        okButton.setBorderPainted(false);
        okButton.setBounds(40, 70, 100, 40);
        JButton cancelButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/button_bg.png")));
        cancelButton.setText("Cancel");
        cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        cancelButton.setVerticalTextPosition(SwingConstants.CENTER);
        cancelButton.setOpaque(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setBounds(160, 70, 100, 40);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(okButton);
        panel.add(cancelButton);
        dialog.setContentPane(panel);
        dialog.setSize(340, 160);
        dialog.setLocationRelativeTo(frame);
        okButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                highScoreManager.addScore(name, player.getScore());
                dialog.dispose();
                showHighScores();
            }
        });
        cancelButton.addActionListener(e -> {
            dialog.dispose();
            showHighScores();
        });
        dialog.setVisible(true);
    }

    public void showHighScores() {
        JDialog dialog = new JDialog(frame, "High Scores", true);
        Image bg = new ImageIcon(getClass().getClassLoader().getResource("resources/window_bg.png")).getImage();
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new BorderLayout(10, 10));
        List<ScoreEntry> scores = highScoreManager.getScores();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (ScoreEntry entry : scores) {
            listModel.addElement(entry.toString());
        }
        JList<String> list = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane(list);
        JButton backButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("resources/button_bg.png")));
        backButton.setText("Back");
        backButton.setHorizontalTextPosition(SwingConstants.CENTER);
        backButton.setVerticalTextPosition(SwingConstants.CENTER);
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> dialog.dispose());
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void setGlobalKeyBinding() {
        if (frame == null) return;
        JComponent comp = frame.getRootPane();
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
        comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "goMainMenu");
        comp.getActionMap().put("goMainMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (gameRunning) endGame(); else showMainMenu();
            }
        });
    }

    private void stopAllThreadsAndTasks() {
        if (pacmanAnimator != null) pacmanAnimator.stop();
        if (impManager != null) impManager.stop();
        if (ghosts != null) {
            for (Ghost g : ghosts) g.stop();
        }
        gameRunning = false;
        if (gameLoopThread != null && gameLoopThread.isAlive()) gameLoopThread.interrupt();
    }
}
