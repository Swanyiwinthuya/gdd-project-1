package gdd.powerup;

import gdd.Factory;
import gdd.sprite.Player;

public class MultiShot extends PowerUp {
    public MultiShot(int x, int y) {
        super(x, y);
        setFrames(Factory.getFrames("multiShot"));
        animationDelay = 6;
    }

    @Override
    public void upgrade(Player player) {
        player.upgradeShot();
        die();
    }
}
