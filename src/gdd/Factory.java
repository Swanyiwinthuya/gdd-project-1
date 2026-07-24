package gdd;

import static gdd.Global.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Factory {
    private static final Map<String, Image[]> frameCache = new HashMap<>();
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final Map<String, String> audioCache = new HashMap<>();

    private Factory() {}

    public static void preload() {
        imageCache.put("title", loadImage(IMG_TITLE));
        imageCache.put("stage1bg", loadImage(IMG_STAGE1_BG));
        imageCache.put("stage2bg", loadImage(IMG_STAGE2_BG));
        frameCache.put("player", loadFrames(IMG_PLAYER_SHEET, 72, 48));
        frameCache.put("alien1", loadFrames(IMG_ALIEN1_SHEET, 56, 42));
        frameCache.put("alien2", loadFrames(IMG_ALIEN2_SHEET, 60, 54));
        frameCache.put("centipede", loadFrames(IMG_CENTIPEDE_SHEET, 176, 56));
        frameCache.put("boss", loadFrames(IMG_BOSS_SHEET, 280, 300));
        frameCache.put("shot", loadFrames(IMG_SHOT_SHEET, 22, 8));
        frameCache.put("laserShot", loadFrames(IMG_LASER_SHOT_SHEET, 54, 14));
        frameCache.put("enemyShot", loadFrames(IMG_ENEMY_SHOT_SHEET, 18, 10));
        frameCache.put("explosion", loadFrames(IMG_EXPLOSION_SHEET, 48, 48));
        frameCache.put("speedUp", loadFrames(IMG_POWERUP_SPEEDUP_SHEET, 32, 32));
        frameCache.put("multiShot", loadFrames(IMG_POWERUP_MULTISHOT_SHEET, 32, 32));
        frameCache.put("shieldUp", loadFrames(IMG_POWERUP_SHIELD_SHEET, 32, 32));
        frameCache.put("damageUp", loadFrames(IMG_POWERUP_DAMAGE_SHEET, 32, 32));
        frameCache.put("laserUp", loadFrames(IMG_POWERUP_LASER_SHEET, 32, 32));

        audioCache.put("title", AUDIO_TITLE);
        audioCache.put("stage1", AUDIO_STAGE1);
        audioCache.put("stage2", AUDIO_STAGE2);
        audioCache.put("ending", AUDIO_ENDING);
        audioCache.put("victory", AUDIO_VICTORY);
        audioCache.put("warning", AUDIO_WARNING);
        audioCache.put("shot", AUDIO_SHOT);
        audioCache.put("explosion", AUDIO_EXPLOSION);
        audioCache.put("powerup", AUDIO_POWERUP);
    }

    public static Image getImage(String key) {
        return imageCache.get(key);
    }

    public static Image[] getFrames(String key) {
        Image[] frames = frameCache.get(key);
        if (frames == null || frames.length == 0) {
            return createFallbackFrames(32, 32, Color.MAGENTA);
        }
        return frames;
    }

    public static String getAudio(String key) {
        return audioCache.get(key);
    }

    private static Image loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            return createFallbackImage(BOARD_WIDTH, BOARD_HEIGHT, Color.BLACK);
        }
    }

    private static Image[] loadFrames(String path, int frameWidth, int frameHeight) {
        try {
            BufferedImage sheet = ImageIO.read(new File(path));
            int count = Math.max(1, sheet.getWidth() / frameWidth);
            Image[] frames = new Image[count];
            for (int i = 0; i < count; i++) {
                frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            }
            return frames;
        } catch (Exception e) {
            return createFallbackFrames(frameWidth, frameHeight, Color.MAGENTA);
        }
    }

    private static Image[] createFallbackFrames(int w, int h, Color color) {
        Image[] frames = new Image[2];
        frames[0] = createFallbackImage(w, h, color);
        frames[1] = createFallbackImage(w, h, color.brighter());
        return frames;
    }

    private static Image createFallbackImage(int w, int h, Color color) {
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.WHITE);
        g.drawRect(0, 0, w - 1, h - 1);
        g.dispose();
        return image;
    }

    public static void playSfx(String key) {
        String path = audioCache.get(key);
        if (path == null) {
            return;
        }
        new Thread(() -> {
            try {
                File audioFile = new File(path);
                AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile.getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(stream);
                clip.start();
            } catch (Exception e) {
            }
        }).start();
    }

    public static int[][] loadCsvMap(String filePath) {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(",");
                int[] row = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    row[i] = Integer.parseInt(parts[i].trim());
                }
                rows.add(row);
            }
        } catch (Exception e) {
            return defaultBackgroundMap();
        }
        return rows.toArray(new int[0][]);
    }

    private static int[][] defaultBackgroundMap() {
        int[][] map = new int[14][160];
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                map[r][c] = 0;
            }
        }
        return map;
    }
}
