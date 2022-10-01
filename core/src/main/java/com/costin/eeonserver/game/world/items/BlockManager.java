package com.costin.eeonserver.game.world.items;

import java.util.HashMap;

public class BlockManager {

    private static BlockManager singleton;
    private HashMap<Integer, BlockType> blockTypes;

    public BlockManager() {
        generateBlocks();
        singleton = this;
    }

    public static BlockManager getInstance() {
        return singleton;
    }

    private void generateBlocks() {

        blockTypes = new HashMap<>();
        for (int i = 0; i <= 97; i++) {
            BlockType block = new BlockType();
            block.texture = null;

            // 0-15 (air, arrows, dot, crown, bricks)
            switch (i) {
                case 0:
                    block.codename = "AIR";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 1:
                    block.codename = "LEFT ARROW";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 2:
                    block.codename = "UP ARROW";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 3:
                    block.codename = "RIGHT ARROW";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 4:
                    block.codename = "GRAVITY DOT";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 5:
                    block.codename = "CROWN";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 6:
                    block.codename = "RED KEY";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 7:
                    block.codename = "GREEN KEY";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 8:
                    block.codename = "BLUE KEY";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 9:
                    block.codename = "GRAY BRICK";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 10:
                    block.codename = "BLUE BRICK";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 11:
                    block.codename = "PINK BRICK";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 12:
                    block.codename = "RED BRICK";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 13:
                    block.codename = "YELLOW BRICK";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 14:
                    block.codename = "GREEN BRICK";
                    block.tags = new String[]{"tag, test1, hey!"};
                case 15:
                    block.codename = "CYAN BRICK";
                    block.tags = new String[]{"tag, test1, hey!"};
            }
            blockTypes.put(i, block);
        }
    }

    public BlockType getBlockTypeByID(int id) {
        return blockTypes.get(id);
    }
}
