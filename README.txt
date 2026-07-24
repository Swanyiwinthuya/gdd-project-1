Side-Scroll Shooter

## Project Overview

It is a retro-style side-scrolling space shooter game developed using Java Swing. The project is extended from the provided GDD Space Invaders starter codebase and keeps the original structure without fully restructuring the project.

The game is inspired by classic 1990s and 2000s arcade space shooters. The player controls a spaceship, fights enemies, collects power-ups, avoids stone blocks, and defeats bosses across multiple stages.

---

## Team Members

-Arkar Phyo ID-6520052
-Swan Yi Win Thu Ya ID-6540200
-Zwe Khant Lin ID-6632710

---

## Main Features

### Side-Scrolling Gameplay

The game is a horizontal side-scroll shooter, not a vertical shooter. The background scrolls from right to left while the player moves and shoots enemies.

### Title Scene

The game includes a custom title screen with:

- Game title image
- Team member name
- Difficulty selection
- Start instruction

### Loading Scene

After pressing start, a short loading scene appears before the gameplay begins.

### Difficulty Selection

Players can choose difficulty from the title screen:

- Easy
- Normal
- Hard

Difficulty affects player lives and enemy behavior.

### Two Game Stages

The game includes two playable stages:

- Stage 1: Nebula Run
- Stage 2: Asteroid Boss

Each stage has a scrolling background, enemies, power-ups, blocks, and a boss fight.

### Boss Fight

Each stage includes a boss fight. The boss appears after the player defeats enough enemies. Before the boss appears, a warning message and countdown are shown so the player can prepare.

### Enemy Types

The game includes multiple enemy types:

- UFO enemy
- Alien enemy
- Centipede enemy
- Boss enemy

The centipede enemy appears once in each stage.

### Power-Ups

The game includes several power-ups:

- Speed Up
- Multi Shot
- Shield Up
- Damage Up
- Repair Up


### Stone Block Map

The game includes a block map system using separate CSV files. Blocks are used as obstacles in the stage.

Block behavior:

- Player cannot pass through blocks
- Player bullets cannot pass through blocks
- Enemy bullets cannot pass through blocks
- Aliens can pass through blocks

The block maps are stored separately to keep the codebase clean.

### Dashboard

The game includes a retro arcade-style dashboard showing:

- Score
- High score
- Stage
- Lives
- Difficulty
- Speed upgrade
- Shot upgrade
- Shield status
- Damage level
- Boss status


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