package com.costin.eeonserver.game.smileys;

import java.util.HashMap;

public class  SmileyManager {

    private static SmileyManager singleton;
    private HashMap<Integer, Smiley> smileyTypes;

    public SmileyManager() {
        singleton = this;
    }

    public static SmileyManager getInstance() {
        return singleton;
    }
}