package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    private Image backgroundImage;

    public MainMenu(GameController controller) {
        // Завантажуємо фон головного меню
        backgroundImage = new ImageIcon(
                getClass().getClassLoader().getResource("resources/menu_bg.png")
        ).getImage();

        setLayout(new GridLayout(3, 1, 10, 10));
        setOpaque(false); // Дозволяємо бачити фон під компонентами

        // Кнопка "New Game"
        JButton newGameBtn = new JButton(
                new ImageIcon(getClass().getClassLoader().getResource("resources/button_bg.png"))
        );
        newGameBtn.setText("New Game");
        newGameBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        newGameBtn.setVerticalTextPosition(SwingConstants.CENTER);
        newGameBtn.setOpaque(false);
        newGameBtn.setContentAreaFilled(false);
        newGameBtn.setBorderPainted(false);
        newGameBtn.addActionListener(e -> controller.launchNewGameDialog());

        // Кнопка "High Scores"
        JButton highScoresBtn = new JButton(
                new ImageIcon(getClass().getClassLoader().getResource("resources/button_bg.png"))
        );
        highScoresBtn.setText("High Scores");
        highScoresBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        highScoresBtn.setVerticalTextPosition(SwingConstants.CENTER);
        highScoresBtn.setOpaque(false);
        highScoresBtn.setContentAreaFilled(false);
        highScoresBtn.setBorderPainted(false);
        highScoresBtn.addActionListener(e -> controller.showHighScores());

        // Кнопка "Exit"
        JButton exitBtn = new JButton(
                new ImageIcon(getClass().getClassLoader().getResource("resources/button_bg.png"))
        );
        exitBtn.setText("Exit");
        exitBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        exitBtn.setVerticalTextPosition(SwingConstants.CENTER);
        exitBtn.setOpaque(false);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setBorderPainted(false);
        exitBtn.addActionListener(e -> System.exit(0));

        add(newGameBtn);
        add(highScoresBtn);
        add(exitBtn);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
