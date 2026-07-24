package gdd.sprite;

import gdd.Factory;
import gdd.Global;

public class EnemyShot extends Sprite {
    public EnemyShot(int x, int y, int dy) {
        this.x = x;
        this.y = y;
        this.dx = -7;
        this.dy = dy;
        setFrames(Factory.getFrames("enemyShot"));
        animationDelay = 3;
    }

    @Override
    public void act() {
        animate();
        x += dx;
        y += dy;
        if (x < -50 || y < 0 || y > Global.BOARD_HEIGHT) {
            die();
        }
    }
}
