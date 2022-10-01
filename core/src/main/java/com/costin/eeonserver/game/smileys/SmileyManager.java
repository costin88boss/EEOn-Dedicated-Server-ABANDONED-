package com.costin.eeonserver.game.smileys;

import java.util.HashMap;

public class SmileyManager {

    private static SmileyManager singleton;
    private HashMap<Integer, Smiley> smileyTypes;

    public SmileyManager() {
        generateSmileys();
        singleton = this;
    }

    public static SmileyManager getInstance() {
        return singleton;
    }

    private void generateSmileys() {

        smileyTypes = new HashMap<>();
        for (int i = 0; i <= 2; i++) {
            Smiley smiley = new Smiley();
            smiley.texture = null;
            smiley.goldenTexture = null;

            // 0-15 (air, arrows, dot, crown, bricks)
            switch (i) {
                case 0:
                    smiley.codename = "CALM";
                    smiley.tags = new String[]{"tag, test1, hey!"};
                case 1:
                    smiley.codename = "GRIN";
                    smiley.tags = new String[]{"tag, test1, hey!"};
                case 2:
                    smiley.codename = "TONGUE OUTSIDE";
                    smiley.tags = new String[]{"tag, test1, hey!"};
            }
            smileyTypes.put(i, smiley);
        }
    }

    public Smiley getSmileyByID(int id) {
        return smileyTypes.get(id);
    }
}