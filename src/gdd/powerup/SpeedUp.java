package gdd.powerup;

import gdd.Factory;
import gdd.sprite.Player;

public class SpeedUp extends PowerUp {
    public SpeedUp(int x, int y) {
        super(x, y);
        setFrames(Factory.getFrames("speedUp"));
        animationDelay = 6;
    }

    @Override
    public void upgrade(Player player) {
        player.upgradeSpeed();
        die();
    }
}
