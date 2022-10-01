package com.costin.eeonserver.game.players;

import com.costin.eeonserver.net.packets.player.PlayerPacket;

import java.util.HashMap;

public class  PlayerManager {

    private static PlayerManager singleton;
    public final HashMap<Integer, Player> players;

    public PlayerManager() {
        singleton = this;
        players = new HashMap<>();
    }

    public static PlayerManager getInstance() {
        return singleton;
    }

    public HashMap<Integer, PlayerPacket> getPlayersAsPackets() {
        HashMap<Integer, PlayerPacket> plyPackets = new HashMap<>();
        players.forEach((integer, playerPacket) -> {
            PlayerPacket ply = new PlayerPacket();
            ply.x = playerPacket.x;
            ply.y = playerPacket.y;
            ply.vX = playerPacket.vX;
            ply.vY = playerPacket.vY;
            ply.username = playerPacket.username;
            ply.god = playerPacket.isHasGodMode();
            ply.golden = playerPacket.isGolden();
            ply.smiley = playerPacket.getSmileyID();
            ply.aura = playerPacket.getAuraID();
            ply.auraColor = playerPacket.getAuraColor();

            plyPackets.put(integer, ply);
        });
        return plyPackets;
    }
}
