package gdd.sprite;

import gdd.Factory;

public class Explosion extends Sprite {
    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        setFrames(Factory.getFrames("explosion"));
        animationDelay = 3;
        visibleFrames = 18;
    }

    @Override
    public void act() {
        animate();
        visibleCountDown();
    }
}
