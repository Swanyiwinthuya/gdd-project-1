# Star Viper - Retro Side-Scroll Shooter

## Project Overview

Star Viper is a retro-style side-scrolling space shooter made with Java Swing. The project extends the provided GDD Space Invaders starter codebase and keeps the original project structure.

The player controls a spaceship, shoots enemies, collects power-ups, avoids stone blocks, survives long scrolling stages, and defeats bosses.

## Team Members

- Arkar Phyo
- Zwe Khant Lin

## Main Requirements Implemented

- Side-scrolling gameplay, not vertical shooting
- Extended from the provided codebase
- Custom title scene with team member names
- Difficulty selection
- Loading scene before gameplay
- Two stages
- Each stage scrolls for around 5 minutes
- Total play time is around 10 minutes before boss fights and ending
- Boss fight in Stage 1 and Stage 2
- Multiple enemy types
- Centipede enemy appears once in each stage
- Animated sprites
- Retro-style dashboard
- CSV spawn maps
- Separate CSV block maps
- Factory class preloads images, audio, maps, and sprite sheets
- Ending / victory scene

## Difficulty Balance

### Easy Mode

Easy mode is designed so the player can beat the game more comfortably.

- 6 lives
- Slower enemies
- More frequent power-ups
- More shields and upgrades

### Normal Mode

Normal mode is balanced for regular gameplay.

- 4 lives
- Medium enemy speed
- Fewer power-ups than Easy mode

### Hard Mode

Hard mode is more challenging.

- 3 lives
- Faster enemies
- Fewer power-ups

## Power-Ups

The game uses these power-ups only:

- Speed Up
- Shield Up
- Multi Shot
- Damage Up
- Laser Shot

## Stage Length

The stage length is controlled in:

```text
src/gdd/Global.java
```

This line makes each stage about 5 minutes long at 60 FPS:

```java
public static final int STAGE_LENGTH_FRAMES = 60 * 60 * 5;
```

Explanation:

```text
60 frames = 1 second
60 * 60 = 1 minute
60 * 60 * 5 = 5 minutes
```

The boss warning starts near the end of the 5-minute stage.

## Controls

| Key | Action |
|---|---|
| Arrow Keys | Move player |
| W A S D | Move player |
| Space | Shoot / Start game |
| 1 | Select Easy difficulty |
| 2 | Select Normal difficulty |
| 3 | Select Hard difficulty |
| Enter | Return to title after ending |

## How to Run

Open Terminal inside the project folder and run:

```bash
find src/gdd -name "*.java" > sources.txt
javac @sources.txt
java -cp src gdd.Main
```

## Clean Before Pushing to GitHub

```bash
find src -name "*.class" -delete
find . -name ".DS_Store" -delete
rm -f sources.txt
```

## GitHub Push Commands

```bash
git add .
git commit -m "Make stages 5 minutes and balance difficulty"
git push
```

If Git rejects because the remote already has files:

```bash
git push --force
```
