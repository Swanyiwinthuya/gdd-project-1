package gdd.scene;

import gdd.AudioPlayer;
import gdd.Difficulty;
import gdd.Factory;
import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TitleScene extends JPanel {
    private int frame = 0;
    private Image image;
    private AudioPlayer audioPlayer;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private Game game;

    private final String[] teamMembers = {
        "Arkar Phyo",
        "Zwe Khant Lin"
    };

    public TitleScene(Game game) {
        this.game = game;
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);
        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();
        initTitle();
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
            System.err.println("Error closing title audio player.");
        }
    }

    private void initTitle() {
        image = Factory.getImage("title");
    }

    private void initAudio() {
        try {
            audioPlayer = new AudioPlayer(Factory.getAudio("title"));
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Title audio not available.");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        if (image != null) {
            g.drawImage(image, 0, 0, d.width, d.height, this);
        }
        drawTeamPanel(g);
        drawBottomPanel(g);
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawTeamPanel(Graphics g) {
        int panelX = 18;
        int panelY = 18;
        int panelW = 265;
        int panelH = 104;

        g.setColor(new Color(5, 18, 40, 185));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 20, 20);
        g.setColor(new Color(255, 220, 90));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 20, 20);

        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.setColor(new Color(255, 230, 120));
        g.drawString("TEAM MEMBERS", panelX + 16, panelY + 26);

        g.setColor(new Color(90, 210, 255));
        g.drawLine(panelX + 16, panelY + 34, panelX + panelW - 16, panelY + 34);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.PLAIN, 14));
        int y = panelY + 56;
        for (String member : teamMembers) {
            g.drawString("- " + member, panelX + 18, y);
            y += 18;
        }
    }

    private void drawBottomPanel(Graphics g) {
        int panelX = 58;
        int panelY = 585;
        int panelW = d.width - 116;
        int panelH = 92;

        g.setColor(new Color(0, 0, 0, 185));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 18, 18);
        g.setColor(new Color(90, 210, 255));
        g.drawRoundRect(panelX, panelY, panelW, panelH, 18, 18);

        if (frame % 60 < 34) {
            g.setColor(new Color(255, 230, 120));
        } else {
            g.setColor(Color.WHITE);
        }
        g.setFont(new Font("Consolas", Font.BOLD, 28));
        String start = "PRESS SPACE TO START";
        int x = (d.width - g.getFontMetrics().stringWidth(start)) / 2;
        g.drawString(start, x, panelY + 33);

        g.setFont(new Font("Consolas", Font.BOLD, 17));
        g.setColor(new Color(120, 230, 255));
        String diff = "CHOOSE DIFFICULTY: [1] EASY   [2] NORMAL   [3] HARD";
        x = (d.width - g.getFontMetrics().stringWidth(diff)) / 2;
        g.drawString(diff, x, panelY + 60);

        g.setColor(Color.WHITE);
        String current = "CURRENT: " + game.getDifficulty().getLabel();
        x = (d.width - g.getFontMetrics().stringWidth(current)) / 2;
        g.drawString(current, x, panelY + 80);
    }

    private void update() {
        frame++;
    }

    private void doGameCycle() {
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_1) {
                game.setDifficulty(Difficulty.EASY);
            } else if (key == KeyEvent.VK_2) {
                game.setDifficulty(Difficulty.NORMAL);
            } else if (key == KeyEvent.VK_3) {
                game.setDifficulty(Difficulty.HARD);
            } else if (key == KeyEvent.VK_SPACE) {
                game.loadLoading();
            }
        }
    }
}
