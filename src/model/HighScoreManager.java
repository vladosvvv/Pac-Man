package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {
    private static final String FILE_NAME = "highscores.ser";
    private List<ScoreEntry> scores;

    public HighScoreManager() {
        loadScores();
    }


    private void loadScores() {
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            scores = new ArrayList<>();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            scores = (List<ScoreEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            scores = new ArrayList<>();
        }
    }

    public void addScore(String name, int score) {
        scores.add(new ScoreEntry(name, score));
        Collections.sort(scores, Comparator.comparingInt(ScoreEntry::getScore).reversed());
        if (scores.size() > 10) {
            scores = scores.subList(0, 10);
        }
        saveScores();
    }

    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ScoreEntry> getScores() {
        return new ArrayList<>(scores);
    }
}