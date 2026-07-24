package gdd;

public enum Difficulty {
    EASY("EASY", 0.64, 6),
    NORMAL("NORMAL", 0.88, 4),
    HARD("HARD", 1.12, 3);

    private final String label;
    private final double enemySpeedMultiplier;
    private final int playerLives;

    Difficulty(String label, double enemySpeedMultiplier, int playerLives) {
        this.label = label;
        this.enemySpeedMultiplier = enemySpeedMultiplier;
        this.playerLives = playerLives;
    }

    public String getLabel() {
        return label;
    }

    public double getEnemySpeedMultiplier() {
        return enemySpeedMultiplier;
    }

    public int getPlayerLives() {
        return playerLives;
    }
}
