# 🚀 Side-Scroll Shooter

A retro-style side-scrolling space shooter game built with **Java Swing**.  
This project is extended from the provided **GDD Space Invaders starter codebase** while keeping the original structure and coding style.

The game is inspired by classic **1990s and 2000s arcade space shooters**, where the player controls a spaceship, fights enemies, collects power-ups, avoids stone blocks, and defeats bosses across two long stages.

---

## 👥 Team Members

| Name | Student ID |
|---|---|
| Arkar Phyo | 6520052 |
| Swan Yi Win Thu Ya | 6540200 |
| Zwe Khant Lin | 6632710 |

---

## 🎮 Game Overview

The player controls a spaceship in a horizontal side-scrolling space battlefield.  
Enemies attack from the right side of the screen while the player moves, shoots, collects upgrades, and survives until the boss fight.

Each stage is designed to last around **5 minutes**, giving the full game an average playtime of around **10 minutes**.

---

## ✨ Main Features

### 🛸 Side-Scrolling Gameplay

The game is a **horizontal side-scroll shooter**, not a vertical shooter.  
The background scrolls from right to left while the player battles enemies.

### 🖼️ Title Scene

The game includes a custom title scene with:

- Custom title image
- Team member names
- Difficulty selection
- Start instruction

### ⏳ Loading Scene

After pressing start, a short loading scene appears before the first stage begins.

### 🎚️ Difficulty Selection

Players can choose difficulty from the title screen:

| Difficulty | Description |
|---|---|
| Easy | More lives, more power-ups, easier to beat |
| Normal | Balanced gameplay |
| Hard | Faster enemies and fewer power-ups |

### 🌌 Two Game Stages

The game includes two playable stages:

| Stage | Name | Description |
|---|---|---|
| Stage 1 | Nebula Run | First side-scrolling battle stage |
| Stage 2 | Asteroid Boss | Final stage with stronger enemies and boss fight |

Each stage includes:

- Scrolling background
- Enemy waves
- Power-ups
- Stone block obstacles
- Boss fight

### 👾 Enemy Types

The game includes multiple enemy types:

- UFO Enemy
- Alien Enemy
- Centipede Enemy
- Boss Enemy

The centipede enemy appears once in each stage for variety and challenge.

### 🔥 Boss Fight

Each stage includes a boss battle.  
Before the boss appears, the game shows a warning countdown so the player can prepare.

Boss fights include:

- Boss health bar
- Stronger attacks
- Stage-based difficulty
- Victory progression after defeating the boss

### ⚡ Power-Ups

The game includes the following power-ups:

| Power-Up | Effect |
|---|---|
| Speed Up | Increases player movement speed |
| Shield Up | Protects the player from damage |
| Multi Shot | Increases number of shots |
| Damage Up | Makes bullets stronger |
| Laser Shot | Upgrades the player weapon to laser shots |

Easy mode gives more power-ups, while Normal and Hard give fewer power-ups.

---

## 🪨 Stone Block Map System

The game uses separate CSV files for stone block maps.

Block behavior:

- Player cannot pass through stone blocks
- Player bullets cannot pass through stone blocks
- Enemy bullets cannot pass through stone blocks
- Aliens can pass through blocks

This makes the map more strategic because the player must move carefully and use the space wisely.

Block map files:

```text
src/maps/stage1_blocks.csv
src/maps/stage2_blocks.csv