package com.costin.eeonserver.net.packets.player.updates.serverside;

public class ServerPlyUpdatePacket {
    public int id;
    public boolean hasGodMode;
    public int newSmiley, newAura; // -1 = disable
    public int newAuraColor;
}
