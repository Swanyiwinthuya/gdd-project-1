package gdd.powerup;

import gdd.Factory;
import gdd.sprite.Player;

public class DamageUp extends PowerUp {
    public DamageUp(int x, int y) {
        super(x, y);
        setFrames(Factory.getFrames("damageUp"));
        animationDelay = 6;
    }

    @Override
    public void upgrade(Player player) {
        player.upgradeDamage();
        die();
    }
}
