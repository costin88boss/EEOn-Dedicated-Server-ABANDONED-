package com.costin.eeonserver.game.world;

import com.costin.eeonserver.game.GameObject;
import com.dongbat.jbump.World;

public class  WorldManager {
    private static WorldManager singleton;
    public EEWorld EEWorld;
    public World<GameObject> collWorld;

    public WorldManager() {
        singleton = this;
        EEWorld = new EEWorld();
        collWorld = new World<>(16);
    }

    public static WorldManager getInstance() {
        return singleton;
    }
}
