package com.costin.eeonserver.net.listeners;

import com.badlogic.gdx.graphics.Color;
import com.costin.eeonserver.game.players.Player;
import com.costin.eeonserver.game.players.PlayerManager;
import com.costin.eeonserver.net.GameServer;
import com.costin.eeonserver.net.packets.player.updates.clientside.PlayerMovePacket;
import com.costin.eeonserver.net.packets.player.updates.clientside.PlayerUpdatePacket;
import com.costin.eeonserver.net.packets.player.updates.serverside.ServerPlyUpdatePacket;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class PlayerUpdateListener implements Listener {
    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof PlayerMovePacket) {
            Player ply = PlayerManager.getInstance().players.get(connection.getID());
            if (ply == null) return;
            PlayerMovePacket packet = (PlayerMovePacket) object;
            ply.updatePacket(packet, connection);
        }
        if (object instanceof PlayerUpdatePacket) {
            Player ply = PlayerManager.getInstance().players.get(connection.getID());
            if (ply == null) return;
            PlayerUpdatePacket packet = (PlayerUpdatePacket) object;
            ply.setGodMode(packet.hasGodMode);
            ServerPlyUpdatePacket newPacket = new ServerPlyUpdatePacket();
            newPacket.newSmiley = -1;
            if (packet.newSmiley >= 0 && packet.newSmiley < 189) {
                ply.setSmiley(packet.newSmiley);
                newPacket.newSmiley = packet.newSmiley;
            }
            newPacket.newAura = -1;
            if (packet.newAura >= 0 && packet.newAura <= 17) {
                newPacket.newAura = packet.newAura;
                ply.setAura(packet.newAura);
            }
            newPacket.newAuraColor = packet.newAuraColor;
            ply.setAuraColor(newPacket.newAuraColor);
            Color color = new Color();
            Color.rgba8888ToColor(color, packet.newAuraColor);
            newPacket.id = connection.getID();
            newPacket.hasGodMode = packet.hasGodMode;
            GameServer.server.sendToAllUDP(newPacket);
        }
    }
}
