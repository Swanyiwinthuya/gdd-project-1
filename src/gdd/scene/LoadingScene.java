package gdd.scene;

import static gdd.Global.*;

import gdd.Game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class LoadingScene extends JPanel {
    private final Game game;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private int frame;

    public LoadingScene(Game game) {
        this.game = game;
    }

    public void start() {
        setFocusable(true);
        setBackground(Color.black);
        timer = new Timer(DELAY, new GameCycle());
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawLoading(g);
    }

    private void drawLoading(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        for (int i = 0; i < 80; i++) {
            int x = Math.floorMod(i * 97 - frame * 4, BOARD_WIDTH);
            int y = Math.floorMod(i * 53, BOARD_HEIGHT);
            int size = i % 6 == 0 ? 2 : 1;
            g.setColor(i % 4 == 0 ? new Color(120, 230, 255) : Color.WHITE);
            g.fillRect(x, y, size, size);
        }

        g.setColor(new Color(0, 22, 38));
        g.fillRoundRect(82, 190, BOARD_WIDTH - 164, 250, 28, 28);
        g.setColor(new Color(0, 230, 255));
        g.drawRoundRect(82, 190, BOARD_WIDTH - 164, 250, 28, 28);
        g.drawRoundRect(88, 196, BOARD_WIDTH - 176, 238, 22, 22);

        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        g.setColor(new Color(255, 230, 90));
        String text = "NOW LOADING";
        int x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, x, 265);

        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        String sub = "SYSTEM CHECK  |  PILOT READY  |  STAGE 1";
        x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(sub)) / 2;
        g.drawString(sub, x, 306);

        int barX = 142;
        int barY = 350;
        int barW = BOARD_WIDTH - 284;
        int barH = 28;
        int fill = Math.min(barW, frame * barW / LOADING_FRAMES);
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barW, barH);
        g.setColor(new Color(0, 210, 255));
        g.fillRect(barX + 2, barY + 2, Math.max(0, fill - 4), barH - 3);
        g.setColor(new Color(255, 230, 90));
        for (int i = 0; i < fill; i += 24) {
            g.drawLine(barX + i, barY + 2, barX + i + 10, barY + barH - 2);
        }

        g.setColor(new Color(255, 255, 255, 35));
        for (int y = 0; y < BOARD_HEIGHT; y += 4) {
            g.drawLine(0, y, BOARD_WIDTH, y);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        frame++;
        if (frame >= LOADING_FRAMES) {
            game.loadScene1();
        }
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            update();
            repaint();
        }
    }
}
