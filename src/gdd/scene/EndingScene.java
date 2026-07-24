package gdd.scene;

import gdd.AudioPlayer;
import gdd.Factory;
import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class EndingScene extends JPanel {
    private final Game game;
    private final boolean won;
    private int frame = 0;
    private Timer timer;
    private AudioPlayer audioPlayer;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);

    public EndingScene(Game game, boolean won) {
        this.game = game;
        this.won = won;
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);
        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();
        initAudio();
    }

    public void stop() {
        try {
            if (timer != null) {
                timer.stop();
            }
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing ending audio player.");
        }
    }

    private void initAudio() {
        try {
            String audioKey = won ? "victory" : "ending";
            audioPlayer = new AudioPlayer(Factory.getAudio(audioKey));
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Ending audio not available.");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawEnding(g);
    }

    private void drawEnding(Graphics g) {
        g.setColor(new Color(2, 4, 18));
        g.fillRect(0, 0, d.width, d.height);
        for (int i = 0; i < 180; i++) {
            int x = Math.floorMod(i * 97 - frame * 2, BOARD_WIDTH);
            int y = 40 + Math.floorMod(i * 53, BOARD_HEIGHT - 80);
            g.setColor(i % 4 == 0 ? new Color(150, 210, 255) : Color.WHITE);
            g.fillRect(x, y, 2, 2);
        }

        g.setFont(new Font("Consolas", Font.BOLD, 48));
        g.setColor(won ? new Color(120, 240, 255) : new Color(255, 80, 80));
        String title = won ? "VICTORY!" : "MISSION FAILED";
        int x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(title)) / 2;
        g.drawString(title, x, 245);

        g.setFont(new Font("Consolas", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        String score = "Final Score: " + game.getScore();
        x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(score)) / 2;
        g.drawString(score, x, 315);

        String difficulty = "Difficulty: " + game.getDifficulty().getLabel();
        x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(difficulty)) / 2;
        g.drawString(difficulty, x, 350);

        if (won) {
            g.setFont(new Font("Consolas", Font.BOLD, 20));
            g.setColor(new Color(255, 230, 120));
            String clear = "Both bosses destroyed. Galaxy secured.";
            x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(clear)) / 2;
            g.drawString(clear, x, 395);
        }

        if (frame % 60 < 35) {
            g.setColor(new Color(255, 230, 120));
        } else {
            g.setColor(Color.WHITE);
        }
        g.setFont(new Font("Consolas", Font.BOLD, 20));
        String instruction = "Press SPACE to return to title";
        x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(instruction)) / 2;
        g.drawString(instruction, x, 470);
        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        frame++;
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            update();
            repaint();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                game.loadTitle();
            }
        }
    }
}
