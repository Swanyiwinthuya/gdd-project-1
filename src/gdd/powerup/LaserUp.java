package gdd.powerup;

import gdd.Factory;
import gdd.sprite.Player;

public class LaserUp extends PowerUp {
    public LaserUp(int x, int y) {
        super(x, y);
        setFrames(Factory.getFrames("laserUp"));
        animationDelay = 5;
    }

    @Override
    public void upgrade(Player player) {
        player.upgradeLaserShot();
        die();
    }
}
