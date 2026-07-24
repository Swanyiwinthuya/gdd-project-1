package gdd.powerup;

import gdd.sprite.Player;
import gdd.sprite.Sprite;

abstract public class PowerUp extends Sprite {
    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.dx = -3;
    }

    @Override
    public void act() {
        animate();
        x += dx;
        y += (int) (Math.sin(animationTick * 0.4) * 2);
        if (x < -getWidth() - 20) {
            die();
        }
    }

    abstract public void upgrade(Player player);
}
