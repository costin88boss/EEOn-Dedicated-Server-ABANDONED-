package com.costin.eeonserver.game.world.items;

import java.util.ArrayList;

public class ItemBrickPackage {

    private final String name;
    private final String description;
    private final String[] tags;
    private final ArrayList<ItemBrick> bricks;

    public ItemBrickPackage(String name, String description, String[] tags) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        bricks = new ArrayList<>();
    }

    public ItemBrickPackage(String name, String description) {
        this(name, description, new String[]{});
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getTags() {
        return tags;
    }

    public ArrayList<ItemBrick> getBricks() {
        return bricks;
    }

    public void addBrick(ItemBrick item) {
        bricks.add(item);
    }
}
