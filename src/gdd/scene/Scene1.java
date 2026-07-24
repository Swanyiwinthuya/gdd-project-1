package gdd.scene;

import gdd.Game;
import static gdd.Global.*;

public class Scene1 extends StageScene {
    public Scene1(Game game) {
        super(game, 1, "NEBULA RUN", MAP_STAGE1_BG, MAP_STAGE1_SPAWN, MAP_STAGE1_BLOCKS, "stage1");
    }

    @Override
    protected void onStageClear() {
        game.loadScene2();
    }
}
