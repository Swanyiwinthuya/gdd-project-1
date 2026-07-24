package gdd.sprite;

import gdd.Factory;
import gdd.Global;

public class BossEnemy extends Enemy {
    private int maxHp;
    private int fireTimer;
    private int bossStage;

    public BossEnemy(int x, int y, double difficultySpeed, int bossStage) {
        super(x, y);
        this.bossStage = bossStage;

        if (bossStage == 1) {
            maxHp = (int) (95 * difficultySpeed);
            scoreValue = 7000;
            speed = 1.25;
        } else {
            maxHp = (int) (145 * difficultySpeed);
            scoreValue = 13000;
            speed = 1.08;
        }

        hp = maxHp;
        setFrames(Factory.getFrames("boss"));
        animationDelay = 5;
    }

    public BossEnemy(int x, int y, double difficultySpeed) {
        this(x, y, difficultySpeed, 2);
    }

    @Override
    public void act() {
        animate();
        age++;
        fireTimer++;

        if (realX > Global.BOARD_WIDTH - getWidth() - 14) {
            realX -= speed;
        }

        if (bossStage == 1) {
            realY += Math.sin(age * 0.042) * 1.8;
        } else {
            realY += Math.sin(age * 0.052) * 2.5;
        }

        keepInsideScreenY();
        x = (int) realX;
        y = (int) realY;
    }

    public boolean canFire() {
        int delay = bossStage == 1 ? 54 : 38;
        if (fireTimer > delay) {
            fireTimer = 0;
            return true;
        }
        return false;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getBossStage() {
        return bossStage;
    }
}
