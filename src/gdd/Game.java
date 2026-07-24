package gdd;

import gdd.scene.EndingScene;
import gdd.scene.LoadingScene;
import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.TitleScene;
import javax.swing.JFrame;

public class Game extends JFrame {
    private TitleScene titleScene;
    private LoadingScene loadingScene;
    private Scene1 scene1;
    private Scene2 scene2;
    private EndingScene endingScene;

    private Difficulty difficulty = Difficulty.NORMAL;
    private int score = 0;

    public Game() {
        Factory.preload();
        initUI();
        loadTitle();
    }

    private void initUI() {
        setTitle("Star Viper - Side Scroll Shooter");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void loadTitle() {
        stopCurrentScenes();
        getContentPane().removeAll();
        titleScene = new TitleScene(this);
        add(titleScene);
        titleScene.start();
        revalidate();
        repaint();
    }

    public void loadLoading() {
        stopCurrentScenes();
        getContentPane().removeAll();
        loadingScene = new LoadingScene(this);
        add(loadingScene);
        loadingScene.start();
        revalidate();
        repaint();
    }

    public void loadScene1() {
        stopCurrentScenes();
        resetScore();
        getContentPane().removeAll();
        scene1 = new Scene1(this);
        add(scene1);
        scene1.start();
        revalidate();
        repaint();
    }

    public void loadScene2() {
        stopCurrentScenes();
        getContentPane().removeAll();
        scene2 = new Scene2(this);
        add(scene2);
        scene2.start();
        revalidate();
        repaint();
    }

    public void loadEnding(boolean won) {
        stopCurrentScenes();
        getContentPane().removeAll();
        endingScene = new EndingScene(this, won);
        add(endingScene);
        endingScene.start();
        revalidate();
        repaint();
    }

    private void stopCurrentScenes() {
        if (titleScene != null) {
            titleScene.stop();
        }
        if (loadingScene != null) {
            loadingScene.stop();
        }
        if (scene1 != null) {
            scene1.stop();
        }
        if (scene2 != null) {
            scene2.stop();
        }
        if (endingScene != null) {
            endingScene.stop();
        }
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public void resetScore() {
        score = 0;
    }
}
