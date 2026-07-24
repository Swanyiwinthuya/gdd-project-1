package gdd.scene;

import gdd.Game;
import static gdd.Global.*;

public class Scene2 extends StageScene {
    public Scene2(Game game) {
        super(game, 2, "ASTEROID BOSS", MAP_STAGE2_BG, MAP_STAGE2_SPAWN, MAP_STAGE2_BLOCKS, "stage2");
    }

    @Override
    protected void onStageClear() {
        game.loadEnding(true);
    }
}
