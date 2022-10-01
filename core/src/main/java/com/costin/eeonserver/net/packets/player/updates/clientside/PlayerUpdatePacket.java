package com.costin.eeonserver.net.packets.player.updates.clientside;

public class PlayerUpdatePacket {
    public boolean hasGodMode;
    public int newSmiley = -1, newAura = -1; // -1 = disable
    public int newAuraColor;
}
