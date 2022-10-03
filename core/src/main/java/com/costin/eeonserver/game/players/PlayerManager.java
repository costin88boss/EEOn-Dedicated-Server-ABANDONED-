package com.costin.eeonserver.game.players;

import com.costin.eeonserver.net.packets.player.PlayerPacket;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerManager {

    private static PlayerManager singleton;
    public final HashMap<Integer, Player> players;
    public final ArrayList<String> playerNames;

    public PlayerManager() {
        singleton = this;
        players = new HashMap<>();
        playerNames = new ArrayList<>();
    }

    public static PlayerManager getInstance() {
        return singleton;
    }

    public HashMap<Integer, PlayerPacket> getPlayersAsPackets() {
        HashMap<Integer, PlayerPacket> plyPackets = new HashMap<>();
        players.forEach((id, playerPacket) -> {
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

            plyPackets.put(id, ply);
        });
        return plyPackets;
    }
}
