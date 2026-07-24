package gdd.sprite;

import java.awt.Image;

abstract public class Sprite {
    protected boolean visible;
    protected Image image;
    protected Image[] frames;
    protected boolean dying;
    protected int visibleFrames = 10;
    protected int x;
    protected int y;
    protected int dx;
    protected int dy;
    protected int frameIndex;
    protected int animationTick;
    protected int animationDelay = 8;

    public Sprite() {
        visible = true;
    }

    abstract public void act();

    protected void animate() {
        if (frames == null || frames.length <= 1) {
            return;
        }
        animationTick++;
        if (animationTick >= animationDelay) {
            animationTick = 0;
            frameIndex = (frameIndex + 1) % frames.length;
        }
    }

    public boolean collidesWith(Sprite other) {
        if (other == null || !this.isVisible() || !other.isVisible()) {
            return false;
        }
        return this.getX() < other.getX() + other.getWidth()
                && this.getX() + this.getWidth() > other.getX()
                && this.getY() < other.getY() + other.getHeight()
                && this.getY() + this.getHeight() > other.getY();
    }

    public boolean collideWithOther(Sprite other) {
        return collidesWith(other);
    }

    public void die() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void visibleCountDown() {
        if (visibleFrames > 0) {
            visibleFrames--;
        } else {
            visible = false;
        }
    }

    protected void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setFrames(Image[] frames) {
        this.frames = frames;
        if (frames != null && frames.length > 0) {
            this.image = frames[0];
        }
    }

    public Image getImage() {
        if (frames != null && frames.length > 0) {
            return frames[frameIndex];
        }
        return image;
    }

    public int getWidth() {
        Image img = getImage();
        return img == null ? 0 : img.getWidth(null);
    }

    public int getHeight() {
        Image img = getImage();
        return img == null ? 0 : img.getHeight(null);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public boolean isDying() {
        return this.dying;
    }
}
