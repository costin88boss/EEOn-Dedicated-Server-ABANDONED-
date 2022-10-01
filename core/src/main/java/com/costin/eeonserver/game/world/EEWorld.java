package com.costin.eeonserver.game.world;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class EEWorld {
    public final List<BlockGroup> blocks;
    public String owner;
    public String title;
    public int worldWidth;
    public int worldHeight;
    public float gravityMultiplier;
    public Color bgColor;
    public String description;
    public boolean isCampaign;
    public String crewId;
    public String crewName;
    public int crewStatus;
    public boolean minimapEnabled;
    public String ownerId;

    public EEWorld() {
        this.owner = "Unknown";
        this.title = "Untitled";
        this.worldWidth = 3;
        this.worldHeight = 3;
        this.gravityMultiplier = 1f;
        this.bgColor = new Color();
        this.description = "description";
        this.isCampaign = false;
        this.crewId = "";
        this.crewName = "";
        this.crewStatus = 0;
        this.minimapEnabled = true;
        this.ownerId = "";
        blocks = new ArrayList<>();
    }

    public EEWorld(String owner, String title, int worldWidth, int worldHeight, float gravityMultiplier, Color bgColor, String description, boolean isCampaign, String crewId, String crewName, int crewStatus, boolean minimapEnabled, String ownerId) {
        this.owner = owner;
        this.title = title;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.gravityMultiplier = gravityMultiplier;
        this.bgColor = bgColor;
        this.description = description;
        this.isCampaign = isCampaign;
        this.crewId = crewId;
        this.crewName = crewName;
        this.crewStatus = crewStatus;
        this.minimapEnabled = minimapEnabled;
        this.ownerId = ownerId;
        blocks = new ArrayList<>();
    }

    public void addBlockGroup(BlockGroup block) {
        if (blocks.stream().noneMatch(lBlock -> lBlock.blockId == block.blockId && lBlock.layer == block.layer)) {
            blocks.add(block);
        }
    }

    public void printInfo() {
        System.out.println("Owner '" + owner + "', Title '" + title + "', Size " + worldWidth + "x" + worldHeight +
                ", Gravity Mp " + gravityMultiplier + ", Bgcolor " + bgColor + ", Description '" + description +
                "', Is Campaign? " + isCampaign + ", CrewID '" + crewId + "', Crew Name '" + crewName +
                "', CrewStatus " + crewStatus + ", Is Minimap Enabled? " + minimapEnabled + ", ownerId '" + ownerId + "'");
    }
}
