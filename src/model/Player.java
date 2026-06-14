// Player.java
package model;

public class Player {
    private int row, col;
    private Direction direction = Direction.NONE;
    private boolean mouthOpen = false;

    private double baseSpeed = 1;
    private double currentSpeed = 1;

    private int lives = 3;
    private int score = 0;
    private int scoreMultiplier = 1;
    private boolean invincible = false;

    private float offsetX = 0f;
    private float offsetY = 0f;

    private boolean justDied = false;

    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }

    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isMouthOpen() {
        return mouthOpen;
    }
    public void setMouthOpen(boolean mouthOpen) {
        this.mouthOpen = mouthOpen;
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }

    public void setBaseSpeed(double baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }


    public int getLives() {
        return lives;
    }
    public void setLives(int lives) {
        this.lives = lives;
    }
    public void incrementLives() {
        this.lives++;
    }
    public void dieOneLife() {
        this.lives--;
    }


    public int getScore() {
        return score;
    }
    public void addScore(int pts) {
        this.score += pts;
    }
    public int getScoreMultiplier() {
        return scoreMultiplier;
    }
    public void setScoreMultiplier(int multiplier) {
        this.scoreMultiplier = multiplier;
    }


    public boolean isInvincible() {
        return invincible;
    }
    public void setInvincible(boolean inv) {
        this.invincible = inv;
    }


    public float getOffsetX() {
        return offsetX;
    }
    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }
    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }


    public boolean isJustDied() {
        return justDied;
    }
    public void setJustDied(boolean justDied) {
        this.justDied = justDied;
    }
}
