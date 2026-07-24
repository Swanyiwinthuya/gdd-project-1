package gdd.sprite;

import gdd.Factory;

public class Alien1 extends Enemy {
    public Alien1(int x, int y, double difficultySpeed) {
        super(x, y);
        hp = 1;
        scoreValue = 100;
        speed = 2.2 * difficultySpeed;
        setFrames(Factory.getFrames("alien1"));
        animationDelay = 8;
    }

    @Override
    public void act() {
        super.act();
        realY += Math.sin(age * 0.08) * 1.7;
        keepInsideScreenY();
        y = (int) realY;
    }
}
