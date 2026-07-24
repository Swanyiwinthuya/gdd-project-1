package gdd.sprite;

import gdd.Factory;
import gdd.Global;

public class Shot extends Sprite {
    private int speed;
    private int damage;
    private boolean laser;

    public Shot(int x, int y, int dy) {
        this(x, y, dy, 1, false);
    }

    public Shot(int x, int y, int dy, int damage) {
        this(x, y, dy, damage, false);
    }

    public Shot(int x, int y, int dy, int damage, boolean laser) {
        this.dy = dy;
        this.damage = Math.max(1, damage);
        this.laser = laser;
        initShot(x, y);
    }

    private void initShot(int x, int y) {
        if (laser) {
            setFrames(Factory.getFrames("laserShot"));
            speed = 18;
            animationDelay = 2;
            setX(x + Global.PLAYER_WIDTH - 6);
            setY(y + Global.PLAYER_HEIGHT / 2 - 7);
        } else {
            setFrames(Factory.getFrames("shot"));
            speed = 14;
            animationDelay = 3;
            setX(x + Global.PLAYER_WIDTH - 5);
            setY(y + Global.PLAYER_HEIGHT / 2 - 4);
        }
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void act() {
        animate();
        x += speed;
        y += dy;
        if (x > Global.BOARD_WIDTH + 70 || y < 0 || y > Global.BOARD_HEIGHT) {
            die();
        }
    }
}
