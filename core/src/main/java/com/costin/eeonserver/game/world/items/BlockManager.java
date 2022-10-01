package com.costin.eeonserver.game.world.items;

import java.util.HashMap;

public class  BlockManager {

    private static BlockManager singleton;
    private HashMap<Integer, BlockType> blockTypes;

    public BlockManager() {
        singleton = this;
    }

    public static BlockManager getInstance() {
        return singleton;
    }

}
