import controller.GameController;

public class PacmanGame {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new GameController());
    }
}
