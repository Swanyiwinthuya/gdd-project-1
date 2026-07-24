package gdd.powerup;

import gdd.Factory;
import gdd.sprite.Player;

public class ShieldUp extends PowerUp {
    public ShieldUp(int x, int y) {
        super(x, y);
        setFrames(Factory.getFrames("shieldUp"));
        animationDelay = 6;
    }

    @Override
    public void upgrade(Player player) {
        player.upgradeShield();
        die();
    }
}
