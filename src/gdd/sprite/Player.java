package gdd.sprite;

import gdd.Factory;
import gdd.Global;
import java.awt.event.KeyEvent;

public class Player extends Sprite {
    private int currentSpeed = 5;
    private int speedLevel = 0;
    private int shotLevel = 1;
    private int damageLevel = 1;
    private int shieldCharges = 0;
    private boolean laserShot = false;
    private int lives;

    public Player(int lives) {
        this.lives = lives;
        initPlayer();
    }

    private void initPlayer() {
        setFrames(Factory.getFrames("player"));
        animationDelay = 6;
        setX(Global.PLAYER_START_X);
        setY(Global.PLAYER_START_Y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public void setSpeed(int speed) {
        currentSpeed = Math.max(1, speed);
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public int getShotLevel() {
        return shotLevel;
    }

    public int getDamageLevel() {
        return damageLevel;
    }

    public int getShieldCharges() {
        return shieldCharges;
    }

    public boolean hasLaserShot() {
        return laserShot;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
        if (lives <= 0) {
            setDying(true);
        }
    }

    public void addLife() {
        if (lives < 5) {
            lives++;
        }
    }

    public boolean useShield() {
        if (shieldCharges > 0) {
            shieldCharges--;
            return true;
        }
        return false;
    }

    public void upgradeSpeed() {
        if (speedLevel < 2) {
            speedLevel++;
            currentSpeed += 2;
        }
    }

    public void upgradeShot() {
        if (shotLevel < 4) {
            shotLevel++;
        }
    }

    public void upgradeDamage() {
        if (damageLevel < 3) {
            damageLevel++;
        }
    }

    public void upgradeShield() {
        if (shieldCharges < 3) {
            shieldCharges++;
        }
    }

    public void upgradeLaserShot() {
        laserShot = true;
    }

    @Override
    public void act() {
        animate();
        x += dx;
        y += dy;

        if (x <= 2) {
            x = 2;
        }
        if (x >= Global.BOARD_WIDTH - getWidth() - 8) {
            x = Global.BOARD_WIDTH - getWidth() - 8;
        }
        if (y <= Global.DASHBOARD_HEIGHT + 6) {
            y = Global.DASHBOARD_HEIGHT + 6;
        }
        if (y >= Global.BOARD_HEIGHT - getHeight() - 35) {
            y = Global.BOARD_HEIGHT - getHeight() - 35;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            dx = -currentSpeed;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            dx = currentSpeed;
        }
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            dy = -currentSpeed;
        }
        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            dy = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            dx = 0;
        }
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            dy = 0;
        }
    }
}
