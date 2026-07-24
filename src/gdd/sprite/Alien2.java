package gdd.sprite;

import gdd.Factory;

public class Alien2 extends Enemy {
    private int startY;

    public Alien2(int x, int y, double difficultySpeed) {
        super(x, y);
        startY = y;
        hp = 2;
        scoreValue = 200;
        speed = 2.8 * difficultySpeed;
        setFrames(Factory.getFrames("alien2"));
        animationDelay = 5;
    }

    @Override
    public void act() {
        animate();
        age++;
        realX -= speed;
        realY = startY + Math.sin(age * 0.06) * 75;
        keepInsideScreenY();
        x = (int) realX;
        y = (int) realY;
        if (x < -getWidth() - 30) {
            die();
        }
    }
}
