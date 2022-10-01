package com.costin.eeonserver.net.packets.player;

public class PacketEnums {
    public enum ConnectionDenyReason {
        UNKNOWN_VERSION,
        OLD_VERSION,
        NEW_VERSION
    }
    public enum AutoKickReason {
        CLIENTSIDE_ERROR,
        SERVERSIDE_ERROR
    }
}
