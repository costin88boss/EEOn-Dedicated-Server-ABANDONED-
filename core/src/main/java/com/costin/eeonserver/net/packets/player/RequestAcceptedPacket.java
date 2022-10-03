package com.costin.eeonserver.net.packets.player;

import com.costin.eeonserver.net.packets.info.WorldPacket;

import java.util.HashMap;

public class RequestAcceptedPacket {
    public String newUsername;
    public float x, y;
    public WorldPacket EEWorld;
    public HashMap<Integer, PlayerPacket> players;
}
