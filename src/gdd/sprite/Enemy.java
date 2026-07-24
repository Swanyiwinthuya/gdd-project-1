package gdd.sprite;

import gdd.Global;

public class Enemy extends Sprite {
    protected int hp = 1;
    protected int scoreValue = 100;
    protected double speed = 2.0;
    protected double realX;
    protected double realY;
    protected int age;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.realX = x;
        this.realY = y;
    }

    @Override
    public void act() {
        animate();
        age++;
        realX -= speed;
        x = (int) realX;
        y = (int) realY;
        if (x < -getWidth() - 30) {
            die();
        }
    }

    public void act(int direction) {
        act();
    }

    public boolean takeHit(int damage) {
        hp -= damage;
        if (hp <= 0) {
            setDying(true);
            return true;
        }
        return false;
    }

    public int getHp() {
        return hp;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public boolean touchesLeftSide() {
        return x < 5;
    }

    protected void keepInsideScreenY() {
        int minY = Global.DASHBOARD_HEIGHT + 10;
        int maxY = Global.BOARD_HEIGHT - getHeight() - 25;
        if (realY < minY) {
            realY = minY;
        }
        if (realY > maxY) {
            realY = maxY;
        }
    }
}
