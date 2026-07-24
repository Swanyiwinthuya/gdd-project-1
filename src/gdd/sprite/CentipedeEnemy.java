package gdd.sprite;

import gdd.Factory;
import gdd.Global;

public class CentipedeEnemy extends Enemy {
    private int startY;
    private int fireTimer;

    public CentipedeEnemy(int x, int y, double difficultySpeed) {
        super(x, y);
        startY = y;
        hp = 8;
        scoreValue = 1500;
        speed = 1.45 * difficultySpeed;
        setFrames(Factory.getFrames("centipede"));
        animationDelay = 5;
    }

    @Override
    public void act() {
        animate();
        age++;
        fireTimer++;
        realX -= speed;
        realY = startY + Math.sin(age * 0.045) * 70;
        keepInsideScreenY();
        x = (int) realX;
        y = (int) realY;
        if (x < -getWidth() - 40) {
            die();
        }
    }

    public boolean canFire() {
        if (fireTimer > 125) {
            fireTimer = 0;
            return true;
        }
        return false;
    }
}
