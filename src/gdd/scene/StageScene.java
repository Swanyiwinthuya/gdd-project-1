package gdd.scene;

import static gdd.Global.*;

import gdd.AudioPlayer;
import gdd.Difficulty;
import gdd.Factory;
import gdd.Game;
import gdd.SpawnDetails;
import gdd.powerup.DamageUp;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.LaserUp;
import gdd.powerup.ShieldUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.BossEnemy;
import gdd.sprite.CentipedeEnemy;
import gdd.sprite.Enemy;
import gdd.sprite.EnemyShot;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import gdd.sprite.Sprite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.Timer;

abstract public class StageScene extends JPanel {
    protected final Game game;
    protected final int stageNumber;
    protected final String stageName;
    protected final String bgMapPath;
    protected final String spawnMapPath;
    protected final String blockMapPath;
    protected final String audioKey;

    protected int frame = 0;
    protected int stageFrame = 0;
    protected boolean inGame = true;
    protected String message = "Game Over";

    protected List<PowerUp> powerups = new ArrayList<>();
    protected List<Enemy> enemies = new ArrayList<>();
    protected List<EnemyShot> enemyShots = new ArrayList<>();
    protected List<Explosion> explosions = new ArrayList<>();
    protected List<Shot> shots = new ArrayList<>();
    protected Player player;

    protected Map<Integer, List<SpawnDetails>> spawnMap = new HashMap<>();
    protected int[][] backgroundMap;
    protected int[][] blockMap;
    protected Image stageBackground;
    protected final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    protected Timer timer;
    protected AudioPlayer audioPlayer;
    protected int playerInvincibleFrames = 0;

    protected int enemyKills = 0;
    protected boolean bossWarningActive = false;
    protected int bossWarningFrame = 0;
    protected boolean bossSpawned = false;
    protected boolean bossDefeated = false;
    protected int dynamicEnemyWaveCounter = 0;
    protected int dynamicPowerUpCounter = 0;

    public StageScene(Game game, int stageNumber, String stageName, String bgMapPath, String spawnMapPath, String blockMapPath, String audioKey) {
        this.game = game;
        this.stageNumber = stageNumber;
        this.stageName = stageName;
        this.bgMapPath = bgMapPath;
        this.spawnMapPath = spawnMapPath;
        this.blockMapPath = blockMapPath;
        this.audioKey = audioKey;
        backgroundMap = Factory.loadCsvMap(bgMapPath);
        blockMap = Factory.loadCsvMap(blockMapPath);
        stageBackground = Factory.getImage(stageNumber == 1 ? "stage1bg" : "stage2bg");
        loadSpawnDetails();
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);
        gameInit();
        initAudio();
        timer = new Timer(DELAY, new GameCycle());
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    protected void initAudio() {
        try {
            String filePath = Factory.getAudio(audioKey);
            if (filePath != null) {
                audioPlayer = new AudioPlayer(filePath);
                audioPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Audio not available: " + e.getMessage());
        }
    }

    protected void gameInit() {
        player = new Player(game.getDifficulty().getPlayerLives());
    }

    protected void loadSpawnDetails() {
        try (BufferedReader br = new BufferedReader(new FileReader(spawnMapPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue;
                }
                int spawnFrame = Integer.parseInt(parts[0].trim());
                String type = parts[1].trim();
                int x = Integer.parseInt(parts[2].trim());
                int y = Integer.parseInt(parts[3].trim());
                addSpawn(spawnFrame, new SpawnDetails(type, x, y));
            }
        } catch (Exception e) {
            addFallbackSpawns();
        }
    }

    protected void addSpawn(int spawnFrame, SpawnDetails details) {
        List<SpawnDetails> list = spawnMap.get(spawnFrame);
        if (list == null) {
            list = new ArrayList<>();
            spawnMap.put(spawnFrame, list);
        }
        list.add(details);
    }

    protected void addFallbackSpawns() {
        int length = getStageLengthFrames();
        for (int f = 200; f < length - 600; f += 220) {
            addSpawn(f, new SpawnDetails("Alien1", BOARD_WIDTH + 20, 100 + (f % 450)));
            addSpawn(f + 70, new SpawnDetails(stageNumber == 1 ? "Alien1" : "Alien2", BOARD_WIDTH + 60, 170 + (f % 380)));
        }
        addSpawn(900, new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH + 50, 250));
        addSpawn(1350, new SpawnDetails("PowerUp-Shield", BOARD_WIDTH + 50, 330));
        addSpawn(1700, new SpawnDetails("PowerUp-MultiShot", BOARD_WIDTH + 50, 390));
        addSpawn(2200, new SpawnDetails("Centipede", BOARD_WIDTH + 80, 210));
        addSpawn(2700, new SpawnDetails("PowerUp-Damage", BOARD_WIDTH + 50, 240));
        addSpawn(3300, new SpawnDetails("PowerUp-Laser", BOARD_WIDTH + 50, 420));
    }

    protected int getStageLengthFrames() {
        return STAGE_LENGTH_FRAMES;
    }

    protected int getBossKillGoal() {
        return BOSS_KILL_GOAL;
    }

    protected int getBossWarningStartFrame() {
        return Math.max(2400, getStageLengthFrames() - BOSS_WARNING_FRAMES);
    }

    protected int getDynamicEnemyInterval() {
        Difficulty difficulty = game.getDifficulty();
        if (difficulty == Difficulty.EASY) {
            return 150;
        }
        if (difficulty == Difficulty.NORMAL) {
            return 120;
        }
        return 95;
    }

    protected int getDynamicPowerUpInterval() {
        Difficulty difficulty = game.getDifficulty();
        if (difficulty == Difficulty.EASY) {
            return 520;
        }
        if (difficulty == Difficulty.NORMAL) {
            return 1250;
        }
        return 1900;
    }

    protected String getDynamicPowerUpType() {
        Difficulty difficulty = game.getDifficulty();
        String[] easyList = {
            "PowerUp-SpeedUp", "PowerUp-Shield", "PowerUp-MultiShot", "PowerUp-Damage", "PowerUp-Laser",
            "PowerUp-Shield", "PowerUp-MultiShot", "PowerUp-Damage", "PowerUp-Shield", "PowerUp-Laser"
        };
        String[] normalList = {
            "PowerUp-SpeedUp", "PowerUp-MultiShot", "PowerUp-Shield", "PowerUp-Damage", "PowerUp-Laser", "PowerUp-Shield"
        };
        String[] hardList = {
            "PowerUp-SpeedUp", "PowerUp-MultiShot", "PowerUp-Shield", "PowerUp-Damage", "PowerUp-Laser"
        };
        String[] list = difficulty == Difficulty.EASY ? easyList : difficulty == Difficulty.NORMAL ? normalList : hardList;
        return list[dynamicPowerUpCounter % list.length];
    }

    protected int getDynamicSpawnY(int counter, int salt) {
        int minY = DASHBOARD_HEIGHT + 40;
        int maxY = BOARD_HEIGHT - 120;
        int range = Math.max(80, maxY - minY);
        return minY + Math.floorMod(counter * 97 + stageNumber * 61 + salt, range);
    }

    protected void updateDynamicStageSpawns() {
        if (bossSpawned || bossWarningActive || bossDefeated) {
            return;
        }
        if (stageFrame < 360 || stageFrame >= getBossWarningStartFrame() - 120) {
            return;
        }

        int enemyInterval = getDynamicEnemyInterval();
        if (stageFrame % enemyInterval == 0) {
            dynamicEnemyWaveCounter++;
            int y1 = getDynamicSpawnY(dynamicEnemyWaveCounter, 40);
            int y2 = getDynamicSpawnY(dynamicEnemyWaveCounter, 150);
            if (stageNumber == 1) {
                spawn(new SpawnDetails(dynamicEnemyWaveCounter % 4 == 0 ? "Alien2" : "Alien1", BOARD_WIDTH + 30, y1));
            } else {
                spawn(new SpawnDetails(dynamicEnemyWaveCounter % 3 == 0 ? "Alien1" : "Alien2", BOARD_WIDTH + 30, y1));
                if (game.getDifficulty() != Difficulty.EASY && dynamicEnemyWaveCounter % 3 == 0) {
                    spawn(new SpawnDetails("Alien1", BOARD_WIDTH + 80, y2));
                }
            }
        }

        int powerUpInterval = getDynamicPowerUpInterval();
        if (stageFrame >= 700 && stageFrame % powerUpInterval == 0) {
            String type = getDynamicPowerUpType();
            int y = getDynamicSpawnY(dynamicPowerUpCounter, 230);
            spawn(new SpawnDetails(type, BOARD_WIDTH + 50, y));
            dynamicPowerUpCounter++;
        }
    }

    protected int getBackgroundScrollSpeed() {
        return stageNumber == 1 ? 3 : 4;
    }

    protected void spawn(SpawnDetails sd) {
        double difficultySpeed = game.getDifficulty().getEnemySpeedMultiplier();
        switch (sd.type) {
            case "Alien1":
                enemies.add(new Alien1(sd.x, sd.y, difficultySpeed));
                break;
            case "Alien2":
                enemies.add(new Alien2(sd.x, sd.y, difficultySpeed));
                break;
            case "Centipede":
                enemies.add(new CentipedeEnemy(sd.x, sd.y, difficultySpeed));
                break;
            case "Boss":
                startBossWarning();
                break;
            case "PowerUp-SpeedUp":
                powerups.add(new SpeedUp(sd.x, sd.y));
                break;
            case "PowerUp-MultiShot":
                powerups.add(new MultiShot(sd.x, sd.y));
                break;
            case "PowerUp-Shield":
                powerups.add(new ShieldUp(sd.x, sd.y));
                break;
            case "PowerUp-Damage":
                powerups.add(new DamageUp(sd.x, sd.y));
                break;
            case "PowerUp-Laser":
                powerups.add(new LaserUp(sd.x, sd.y));
                break;
            default:
                System.out.println("Unknown spawn type: " + sd.type);
                break;
        }
    }

    protected void startBossWarning() {
        if (bossWarningActive || bossSpawned || bossDefeated) {
            return;
        }
        bossWarningActive = true;
        bossWarningFrame = 0;
        enemies.clear();
        enemyShots.clear();
        shots.clear();
        powerups.clear();
        Factory.playSfx("warning");
    }

    protected void spawnBossNow() {
        if (bossSpawned) {
            return;
        }
        double difficultySpeed = game.getDifficulty().getEnemySpeedMultiplier();
        int bossY = stageNumber == 1 ? 125 : 105;
        enemies.add(new BossEnemy(BOARD_WIDTH + 40, bossY, difficultySpeed, stageNumber));
        bossSpawned = true;
        bossWarningActive = false;
        bossWarningFrame = 0;
        Factory.playSfx("warning");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    protected void doDrawing(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        drawBackground(g);
        drawBlocks(g);
        drawExplosions(g);
        drawPowerUps(g);
        drawEnemies(g);
        drawEnemyShots(g);
        drawPlayer(g);
        drawShots(g);
        drawDashboard(g);
        if (inGame && frame < PREPARE_FRAMES) {
            drawReady(g);
        }
        if (inGame && bossWarningActive) {
            drawBossWarning(g);
        }
        if (!inGame) {
            drawGameOver(g);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    protected void drawBackground(Graphics g) {
        int playY = DASHBOARD_HEIGHT;
        int playH = BOARD_HEIGHT - DASHBOARD_HEIGHT;
        int scrollSpeed = getBackgroundScrollSpeed();
        int scrollPx = stageFrame * scrollSpeed;
        if (stageBackground != null) {
            int bgWidth = stageBackground.getWidth(this);
            if (bgWidth <= 0) {
                bgWidth = BOARD_WIDTH;
            }
            int offset = Math.floorMod(scrollPx, bgWidth);
            int x = -offset;
            while (x < BOARD_WIDTH) {
                g.drawImage(stageBackground, x, playY, bgWidth, playH, this);
                x += bgWidth;
            }
        } else if (backgroundMap != null && backgroundMap.length > 0) {
            int rows = backgroundMap.length;
            int cols = backgroundMap[0].length;
            int startCol = (scrollPx / BLOCK_WIDTH) % cols;
            int offset = scrollPx % BLOCK_WIDTH;
            int colsNeeded = BOARD_WIDTH / BLOCK_WIDTH + 3;
            for (int screenCol = 0; screenCol < colsNeeded; screenCol++) {
                int mapCol = (startCol + screenCol) % cols;
                int x = screenCol * BLOCK_WIDTH - offset;
                for (int row = 0; row < rows; row++) {
                    int y = playY + row * BLOCK_HEIGHT;
                    int cell = backgroundMap[row][mapCol];
                    drawBackgroundCell(g, cell, x, y, row, mapCol);
                }
            }
        }
        g.setColor(stageNumber == 1 ? new Color(255, 180, 80, 25) : new Color(40, 60, 180, 32));
        g.fillRect(0, playY, BOARD_WIDTH, playH);
        g.setColor(stageNumber == 1 ? new Color(255, 255, 255, 165) : new Color(190, 220, 255, 175));
        for (int i = 0; i < 48; i++) {
            int sx = Math.floorMod(i * 149 - stageFrame * 2, BOARD_WIDTH);
            int sy = playY + 8 + Math.floorMod(i * 83, playH - 20);
            int size = i % 3 == 0 ? 2 : 1;
            g.fillRect(sx, sy, size, size);
        }
    }

    protected void drawBackgroundCell(Graphics g, int cell, int x, int y, int row, int col) {
        switch (cell) {
            case 1:
                g.setColor(Color.WHITE);
                g.fillRect(x + 20, y + 18, 2, 2);
                break;
            case 2:
                g.setColor(new Color(180, 220, 255));
                g.fillOval(x + 18, y + 16, 4, 4);
                g.setColor(Color.WHITE);
                g.fillRect(x + 10, y + 29, 1, 1);
                g.fillRect(x + 35, y + 8, 1, 1);
                break;
            case 3:
                drawRock(g, x, y, row, col);
                break;
            case 4:
                g.setColor(new Color(120, 30, 150, 45));
                g.fillOval(x - 10, y - 6, 70, 60);
                g.setColor(new Color(255, 120, 180));
                g.fillRect(x + 16, y + 17, 1, 1);
                break;
            default:
                break;
        }
    }

    protected void drawBlocks(Graphics g) {
        if (blockMap == null || blockMap.length == 0) {
            return;
        }
        int cols = blockMap[0].length;
        int mapWidth = cols * BLOCK_WIDTH;
        int offset = Math.floorMod(stageFrame * getBackgroundScrollSpeed(), mapWidth);
        int baseX = -offset;
        for (int repeatX = baseX; repeatX < BOARD_WIDTH; repeatX += mapWidth) {
            for (int col = 0; col < cols; col++) {
                int x = repeatX + col * BLOCK_WIDTH;
                if (x < -BLOCK_WIDTH || x > BOARD_WIDTH) {
                    continue;
                }
                for (int row = 0; row < blockMap.length; row++) {
                    int cell = blockMap[row][col];
                    if (cell <= 0) {
                        continue;
                    }
                    int y = DASHBOARD_HEIGHT + row * BLOCK_HEIGHT;
                    drawBlockCell(g, cell, x, y, row, col);
                }
            }
        }
    }

    protected void drawBlockCell(Graphics g, int cell, int x, int y, int row, int col) {
        int seed = Math.abs(row * 97 + col * 53 + cell * 31);
        Color outline = new Color(42, 38, 34);
        Color shadow = new Color(78, 70, 62);
        Color base = cell == 1 ? new Color(154, 136, 108) : new Color(138, 132, 120);
        Color mid = cell == 1 ? new Color(184, 160, 120) : new Color(166, 158, 142);
        Color light = cell == 1 ? new Color(232, 204, 150) : new Color(212, 204, 184);
        Color moss = cell == 1 ? new Color(92, 142, 88) : new Color(86, 128, 106);

        int left = x + 4 + seed % 3;
        int top = y + 4 + (seed / 3) % 3;
        int right = x + BLOCK_WIDTH - 5 - (seed / 7) % 3;
        int bottom = y + BLOCK_HEIGHT - 5 - (seed / 11) % 3;

        int[] xs = {
            left + 8, left + 22, right - 5, right,
            right - 7, left + 20, left, left + 3
        };
        int[] ys = {
            top, top + 2, top + 8, top + 24,
            bottom - 4, bottom, bottom - 10, top + 15
        };

        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(x + 7, y + 36, BLOCK_WIDTH - 10, 10);
        g.setColor(outline);
        g.fillPolygon(xs, ys, xs.length);

        int[] xs2 = {
            xs[0] + 2, xs[1], xs[2] - 3, xs[3] - 5,
            xs[4] - 4, xs[5], xs[6] + 4, xs[7] + 3
        };
        int[] ys2 = {
            ys[0] + 3, ys[1] + 4, ys[2] + 3, ys[3],
            ys[4] - 3, ys[5] - 4, ys[6] - 3, ys[7]
        };
        g.setColor(base);
        g.fillPolygon(xs2, ys2, xs2.length);

        int[] topFaceX = {xs2[0], xs2[1], xs2[2], xs2[2] - 6, xs2[1] - 3};
        int[] topFaceY = {ys2[0], ys2[1], ys2[2], ys2[2] + 8, ys2[1] + 9};
        g.setColor(mid);
        g.fillPolygon(topFaceX, topFaceY, topFaceX.length);

        g.setColor(light);
        g.drawLine(xs2[0] + 2, ys2[0] + 1, xs2[2] - 8, ys2[2]);
        g.drawLine(xs2[0] + 3, ys2[0] + 3, xs2[6] + 5, ys2[6] - 1);
        g.setColor(shadow);
        g.drawLine(x + 13 + seed % 8, y + 15, x + 20 + seed % 9, y + 30);
        g.drawLine(x + 28, y + 15 + seed % 6, x + 38, y + 24 + seed % 7);
        g.drawLine(x + 12, y + 33, x + 26, y + 38);

        g.setColor(moss);
        if ((seed % 3) == 0) {
            g.fillRect(x + 9, y + 25, 8, 3);
            g.fillRect(x + 11, y + 28, 5, 2);
        } else if ((seed % 3) == 1) {
            g.fillRect(x + 30, y + 28, 9, 3);
            g.fillRect(x + 33, y + 31, 4, 2);
        }

        g.setColor(new Color(255, 255, 255, 65));
        g.fillRect(x + 14 + seed % 12, y + 10 + seed % 9, 2, 2);
        g.fillRect(x + 27 + seed % 8, y + 20 + seed % 8, 2, 1);
    }

    protected void drawRock(Graphics g, int x, int y, int row, int col) {
        int shade = 80 + Math.abs((row * 31 + col * 17) % 60);
        g.setColor(new Color(shade, shade - 12, shade - 30));
        int[] xs = {x, x + 10, x + 28, x + 48, x + 42, x + 18};
        int[] ys = {y + 15, y, y + 8, y + 18, y + 48, y + 40};
        g.fillPolygon(xs, ys, xs.length);
        g.setColor(new Color(180, 165, 120));
        g.drawPolygon(xs, ys, xs.length);
    }

    protected void drawEnemies(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
        }
    }

    protected void drawPowerUps(Graphics g) {
        for (PowerUp p : powerups) {
            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }
        }
    }

    protected void drawPlayer(Graphics g) {
        if (player == null || !player.isVisible()) {
            return;
        }
        if (playerInvincibleFrames > 0 && frame % 12 < 6) {
            return;
        }
        if (player.getShieldCharges() > 0) {
            g.setColor(new Color(80, 220, 255, 115));
            g.drawOval(player.getX() - 7, player.getY() - 6, player.getWidth() + 14, player.getHeight() + 12);
            g.drawOval(player.getX() - 10, player.getY() - 9, player.getWidth() + 20, player.getHeight() + 18);
        }
        g.drawImage(player.getImage(), player.getX(), player.getY(), this);
    }

    protected void drawShots(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    protected void drawEnemyShots(Graphics g) {
        for (EnemyShot shot : enemyShots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    protected void drawExplosions(Graphics g) {
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
            }
        }
    }

    protected void drawDashboard(Graphics g) {
        g.setColor(new Color(3, 4, 9));
        g.fillRect(0, 0, BOARD_WIDTH, DASHBOARD_HEIGHT);
        g.setColor(new Color(40, 40, 55));
        for (int x = 0; x < BOARD_WIDTH; x += 18) {
            g.drawLine(x, 0, x, DASHBOARD_HEIGHT);
        }
        g.setColor(new Color(0, 220, 255));
        g.drawLine(0, DASHBOARD_HEIGHT - 2, BOARD_WIDTH, DASHBOARD_HEIGHT - 2);
        g.setColor(new Color(255, 80, 150));
        g.drawLine(0, DASHBOARD_HEIGHT - 1, BOARD_WIDTH, DASHBOARD_HEIGHT - 1);

        g.setFont(new Font("Monospaced", Font.BOLD, 15));
        g.setColor(new Color(150, 235, 255));
        g.drawString("1P", 12, 18);
        g.setColor(Color.WHITE);
        g.drawString(String.format("%06d", game.getScore()), 42, 18);
        g.setColor(new Color(255, 230, 90));
        g.drawString("HI " + String.format("%06d", Math.max(game.getScore(), 250400)), 150, 18);
        g.setColor(new Color(255, 255, 255));
        g.drawString("STAGE " + stageNumber, 302, 18);
        g.setColor(new Color(255, 125, 125));
        g.drawString("LIFE " + player.getLives(), 402, 18);
        g.setColor(new Color(145, 230, 255));
        g.drawString("DIFF " + game.getDifficulty().getLabel(), 506, 18);

        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.setColor(new Color(150, 235, 255));
        g.drawString("SPEED", 12, 40);
        drawMiniBar(g, 64, 30, player.getSpeedLevel(), 2, new Color(0, 220, 255));
        g.setColor(Color.WHITE);
        g.drawString("DOUBLE", 128, 40);
        drawMiniBar(g, 184, 30, player.getShotLevel(), 4, new Color(255, 230, 90));
        g.setColor(new Color(120, 240, 150));
        g.drawString("SHLD " + player.getShieldCharges(), 270, 40);
        g.setColor(new Color(255, 150, 90));
        g.drawString("DMG " + player.getDamageLevel(), 340, 40);
        g.setColor(player.hasLaserShot() ? new Color(80, 230, 255) : new Color(105, 105, 120));
        g.drawString(player.hasLaserShot() ? "LASER ON" : "LASER --", 392, 40);
        g.setColor(new Color(255, 210, 120));
        g.drawString("KILL " + enemyKills + "/" + getBossKillGoal(), 486, 40);

        String bossText;
        if (bossDefeated) {
            bossText = "BOSS CLEAR";
        } else if (bossSpawned) {
            bossText = "BOSS ACTIVE";
        } else if (bossWarningActive) {
            bossText = "WARNING";
        } else {
            bossText = "TIME " + stageFrame + "/" + getStageLengthFrames();
        }
        g.setColor(new Color(255, 100, 190));
        g.drawString(bossText, 600, 40);

        BossEnemy boss = getBoss();
        if (boss != null && boss.isVisible()) {
            int barW = 150;
            int x = BOARD_WIDTH - barW - 12;
            int y = 46;
            g.setColor(Color.WHITE);
            g.drawRect(x, y, barW, 7);
            g.setColor(new Color(255, 45, 45));
            int hpW = (int) (barW * (boss.getHp() / (double) boss.getMaxHp()));
            g.fillRect(x + 1, y + 1, Math.max(0, hpW - 2), 5);
        }
    }

    protected void drawMiniBar(Graphics g, int x, int y, int value, int max, Color color) {
        for (int i = 0; i < max; i++) {
            g.setColor(i < value ? color : new Color(45, 45, 55));
            g.fillRect(x + i * 13, y, 10, 10);
            g.setColor(Color.WHITE);
            g.drawRect(x + i * 13, y, 10, 10);
        }
    }

    protected void drawReady(Graphics g) {
        int secondsLeft = 3 - frame / 60;
        String text = secondsLeft > 0 ? "READY " + secondsLeft : "GO!";
        g.setFont(new Font("Monospaced", Font.BOLD, 46));
        int width = g.getFontMetrics().stringWidth(text);
        g.setColor(new Color(0, 0, 0, 155));
        g.fillRoundRect(BOARD_WIDTH / 2 - 160, BOARD_HEIGHT / 2 - 70, 320, 100, 24, 24);
        g.setColor(new Color(120, 240, 255));
        g.drawString(text, (BOARD_WIDTH - width) / 2, BOARD_HEIGHT / 2);
    }

    protected void drawBossWarning(Graphics g) {
        int framesLeft = Math.max(0, BOSS_WARNING_FRAMES - bossWarningFrame);
        int secondsLeft = (int) Math.ceil(framesLeft / 60.0);
        if (secondsLeft <= 0) {
            secondsLeft = 1;
        }

        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        Color warningColor = frame % 30 < 15 ? new Color(255, 45, 45) : new Color(255, 230, 80);
        g.setColor(warningColor);
        g.drawRect(24, 78, BOARD_WIDTH - 48, BOARD_HEIGHT - 120);
        g.drawRect(29, 83, BOARD_WIDTH - 58, BOARD_HEIGHT - 130);

        g.setFont(new Font("Monospaced", Font.BOLD, 58));
        String warning = "WARNING";
        int x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(warning)) / 2;
        g.drawString(warning, x, 230);

        g.setFont(new Font("Monospaced", Font.BOLD, 28));
        g.setColor(Color.WHITE);
        String incoming = "STAGE " + stageNumber + " ORGANIC BOSS INCOMING";
        x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(incoming)) / 2;
        g.drawString(incoming, x, 290);

        g.setFont(new Font("Monospaced", Font.BOLD, 80));
        String count = String.valueOf(secondsLeft);
        x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(count)) / 2;
        g.setColor(new Color(120, 240, 255));
        g.drawString(count, x, 410);

        g.setFont(new Font("Monospaced", Font.PLAIN, 18));
        g.setColor(new Color(255, 230, 120));
        String prepare = "Hide near blocks, dodge bullets, prepare laser fire.";
        x = (BOARD_WIDTH - g.getFontMetrics().stringWidth(prepare)) / 2;
        g.drawString(prepare, x, 470);
    }

    protected void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        g.setColor(new Color(0, 32, 48));
        g.fillRoundRect(95, 260, BOARD_WIDTH - 190, 130, 18, 18);
        g.setColor(Color.WHITE);
        g.drawRoundRect(95, 260, BOARD_WIDTH - 190, 130, 18, 18);
        g.setFont(new Font("Monospaced", Font.BOLD, 30));
        int w = g.getFontMetrics().stringWidth(message);
        g.drawString(message, (BOARD_WIDTH - w) / 2, 315);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
        String text = "Press ENTER to return to title";
        w = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (BOARD_WIDTH - w) / 2, 355);
    }

    protected void update() {
        if (!inGame) {
            return;
        }

        stageFrame = Math.max(0, frame - PREPARE_FRAMES);
        if (playerInvincibleFrames > 0) {
            playerInvincibleFrames--;
        }

        int oldX = player.getX();
        int oldY = player.getY();
        player.act();
        resolvePlayerBlockCollision(oldX, oldY);

        if (frame < PREPARE_FRAMES) {
            return;
        }

        if (bossWarningActive) {
            updateBossWarning();
            updateExplosions();
            removeDeadSprites();
            return;
        }

        if (!bossSpawned && !bossDefeated) {
            List<SpawnDetails> spawns = spawnMap.get(stageFrame);
            if (spawns != null) {
                for (SpawnDetails sd : spawns) {
                    spawn(sd);
                }
            }
            updateDynamicStageSpawns();
        }

        updatePowerUps();
        updateEnemies();
        updateEnemyShots();
        updateShots();
        updateExplosions();
        removeDeadSprites();
        checkBossTrigger();
        checkStageEnd();
    }

    protected void resolvePlayerBlockCollision(int oldX, int oldY) {
        if (!collidesWithBlock(player)) {
            return;
        }
        int newX = player.getX();
        int newY = player.getY();

        player.setY(oldY);
        if (!collidesWithBlock(player)) {
            return;
        }

        player.setY(newY);
        player.setX(oldX);
        if (!collidesWithBlock(player)) {
            return;
        }

        player.setX(oldX);
        player.setY(oldY);

        Rectangle block = getBlockCollision(player);
        if (block != null) {
            if (player.getX() + player.getWidth() / 2 < block.x + block.width / 2) {
                player.setX(Math.max(2, block.x - player.getWidth() - 2));
            } else {
                player.setX(Math.min(BOARD_WIDTH - player.getWidth() - 8, block.x + block.width + 2));
            }
        }
    }

    protected void updateBossWarning() {
        bossWarningFrame++;
        if (bossWarningFrame >= BOSS_WARNING_FRAMES) {
            spawnBossNow();
        }
    }

    protected void checkBossTrigger() {
        if (bossSpawned || bossWarningActive || bossDefeated) {
            return;
        }
        if (stageFrame >= getBossWarningStartFrame()) {
            startBossWarning();
        }
    }

    protected void updatePowerUps() {
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collideWithOther(player)) {
                    powerup.upgrade(player);
                    Factory.playSfx("powerup");
                }
            }
        }
    }

    protected void updateEnemies() {
        for (Enemy enemy : enemies) {
            if (!enemy.isVisible()) {
                continue;
            }
            enemy.act();
            if (enemy instanceof Alien2 && stageFrame % 150 == 0 && enemy.getX() < BOARD_WIDTH - 80) {
                enemyShots.add(new EnemyShot(enemy.getX(), enemy.getY() + enemy.getHeight() / 2, 0));
            }
            if (enemy instanceof CentipedeEnemy) {
                CentipedeEnemy centipede = (CentipedeEnemy) enemy;
                if (centipede.canFire()) {
                    enemyShots.add(new EnemyShot(enemy.getX() + 18, enemy.getY() + enemy.getHeight() / 2, -2));
                    enemyShots.add(new EnemyShot(enemy.getX() + 18, enemy.getY() + enemy.getHeight() / 2, 2));
                }
            }
            if (enemy instanceof BossEnemy) {
                BossEnemy boss = (BossEnemy) enemy;
                if (boss.canFire()) {
                    int bx = boss.getX() + 18;
                    int by = boss.getY() + boss.getHeight() / 2;
                    if (boss.getBossStage() == 1) {
                        enemyShots.add(new EnemyShot(bx, by - 44, -3));
                        enemyShots.add(new EnemyShot(bx, by, 0));
                        enemyShots.add(new EnemyShot(bx, by + 44, 3));
                    } else {
                        enemyShots.add(new EnemyShot(bx, by - 72, -4));
                        enemyShots.add(new EnemyShot(bx, by - 36, -2));
                        enemyShots.add(new EnemyShot(bx, by, 0));
                        enemyShots.add(new EnemyShot(bx, by + 36, 2));
                        enemyShots.add(new EnemyShot(bx, by + 72, 4));
                    }
                }
            }
            if (enemy.collideWithOther(player)) {
                handlePlayerHit();
                explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                if (!(enemy instanceof BossEnemy)) {
                    enemy.die();
                }
            }
            if (enemy.touchesLeftSide() && !(enemy instanceof BossEnemy)) {
                handlePlayerHit();
                enemy.die();
            }
        }
    }

    protected void updateEnemyShots() {
        for (EnemyShot enemyShot : enemyShots) {
            if (enemyShot.isVisible()) {
                enemyShot.act();
                Rectangle block = getBlockCollision(enemyShot);
                if (block != null) {
                    enemyShot.die();
                    explosions.add(new Explosion(block.x, block.y));
                    continue;
                }
                if (enemyShot.collideWithOther(player)) {
                    enemyShot.die();
                    handlePlayerHit();
                }
            }
        }
    }

    protected void updateShots() {
        for (Shot shot : shots) {
            if (!shot.isVisible()) {
                continue;
            }
            shot.act();
            Rectangle block = getBlockCollision(shot);
            if (block != null) {
                shot.die();
                explosions.add(new Explosion(block.x, block.y));
                Factory.playSfx("explosion");
                continue;
            }
            for (Enemy enemy : enemies) {
                if (!enemy.isVisible() || !shot.isVisible()) {
                    continue;
                }
                if (shot.collideWithOther(enemy)) {
                    shot.die();
                    boolean killed = enemy.takeHit(shot.getDamage());
                    explosions.add(new Explosion(shot.getX(), shot.getY() - 20));
                    if (killed) {
                        enemy.die();
                        explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                        game.addScore(enemy.getScoreValue());
                        Factory.playSfx("explosion");
                        if (enemy instanceof BossEnemy) {
                            bossDefeated = true;
                            enemyShots.clear();
                        } else {
                            enemyKills++;
                        }
                    }
                }
            }
        }
    }

    protected void updateExplosions() {
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                explosion.act();
            }
        }
    }

    protected void removeDeadSprites() {
        powerups.removeIf(p -> !p.isVisible());
        enemies.removeIf(e -> !e.isVisible());
        enemyShots.removeIf(s -> !s.isVisible());
        explosions.removeIf(e -> !e.isVisible());
        shots.removeIf(s -> !s.isVisible());
    }

    protected void handlePlayerHit() {
        if (playerInvincibleFrames > 0) {
            return;
        }
        if (player.useShield()) {
            playerInvincibleFrames = 75;
            explosions.add(new Explosion(player.getX(), player.getY()));
            Factory.playSfx("powerup");
            return;
        }
        player.loseLife();
        playerInvincibleFrames = 120;
        explosions.add(new Explosion(player.getX(), player.getY()));
        Factory.playSfx("explosion");
        if (player.getLives() <= 0) {
            inGame = false;
            message = "GAME OVER";
        }
    }

    protected void fireShots() {
        if (!inGame || frame < PREPARE_FRAMES || bossWarningActive) {
            return;
        }
        int maxShots = 6 + player.getShotLevel() * 4;
        if (shots.size() >= maxShots) {
            return;
        }

        int x = player.getX();
        int y = player.getY();
        int level = player.getShotLevel();
        boolean laser = player.hasLaserShot();
        int damage = player.getDamageLevel() + (laser ? 1 : 0);
        if (level == 1) {
            shots.add(new Shot(x, y, 0, damage, laser));
        } else if (level == 2) {
            shots.add(new Shot(x, y - 8, 0, damage, laser));
            shots.add(new Shot(x, y + 8, 0, damage, laser));
        } else if (level == 3) {
            shots.add(new Shot(x, y, -3, damage, laser));
            shots.add(new Shot(x, y, 0, damage, laser));
            shots.add(new Shot(x, y, 3, damage, laser));
        } else {
            shots.add(new Shot(x, y, -4, damage, laser));
            shots.add(new Shot(x, y, -1, damage, laser));
            shots.add(new Shot(x, y, 1, damage, laser));
            shots.add(new Shot(x, y, 4, damage, laser));
        }
        Factory.playSfx("shot");
    }

    protected Rectangle getBlockCollision(Sprite sprite) {
        if (blockMap == null || blockMap.length == 0 || sprite == null || !sprite.isVisible()) {
            return null;
        }
        Rectangle spriteRect = new Rectangle(sprite.getX() + 4, sprite.getY() + 4, Math.max(1, sprite.getWidth() - 8), Math.max(1, sprite.getHeight() - 8));
        int cols = blockMap[0].length;
        int mapWidth = cols * BLOCK_WIDTH;
        int offset = Math.floorMod(stageFrame * getBackgroundScrollSpeed(), mapWidth);
        int baseX = -offset;
        for (int repeatX = baseX; repeatX < BOARD_WIDTH; repeatX += mapWidth) {
            for (int col = 0; col < cols; col++) {
                int x = repeatX + col * BLOCK_WIDTH;
                if (x > spriteRect.x + spriteRect.width || x + BLOCK_WIDTH < spriteRect.x) {
                    continue;
                }
                for (int row = 0; row < blockMap.length; row++) {
                    int cell = blockMap[row][col];
                    if (cell <= 0) {
                        continue;
                    }
                    int y = DASHBOARD_HEIGHT + row * BLOCK_HEIGHT;
                    Rectangle blockRect = new Rectangle(x + 2, y + 2, BLOCK_WIDTH - 4, BLOCK_HEIGHT - 4);
                    if (spriteRect.intersects(blockRect)) {
                        return blockRect;
                    }
                }
            }
        }
        return null;
    }

    protected boolean collidesWithBlock(Sprite sprite) {
        return getBlockCollision(sprite) != null;
    }

    protected BossEnemy getBoss() {
        for (Enemy enemy : enemies) {
            if (enemy instanceof BossEnemy && enemy.isVisible()) {
                return (BossEnemy) enemy;
            }
        }
        return null;
    }

    protected void checkStageEnd() {
        if (bossDefeated) {
            onStageClear();
        }
    }

    protected void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    abstract protected void onStageClear();

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if (player != null) {
                player.keyReleased(e);
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (!inGame && key == KeyEvent.VK_ENTER) {
                game.loadTitle();
                return;
            }
            if (player != null) {
                player.keyPressed(e);
            }
            if (key == KeyEvent.VK_SPACE) {
                fireShots();
            }
        }
    }
}
