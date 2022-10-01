package com.costin.eeonserver.game;

public class Laws {

    public static final String clientVersion = "0.0.1";

    public static int[] getVersion(){
        String[] version = clientVersion.split("\\.");
        return new int[] {Integer.parseInt(version[0]), Integer.parseInt(version[1]), Integer.parseInt(version[2])};
    }

    public static final int tps = 60;
    public static final double physicsMSPerTick = 10;

    public static final double baseDrag = Math.pow(.9981, physicsMSPerTick) * 1.00016093f;
    public static final double noModDrag = Math.pow(.9900,physicsMSPerTick) * 1.00016093;

    public static final float playerForce = 50;
    public static final float jumpHeight = 26f;
    public static final float gravity = 2f;

}
