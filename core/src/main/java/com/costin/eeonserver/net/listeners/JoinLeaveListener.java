package com.costin.eeonserver.net.listeners;

import com.costin.eeonserver.game.Laws;
import com.costin.eeonserver.game.players.Player;
import com.costin.eeonserver.game.players.PlayerManager;
import com.costin.eeonserver.game.world.BlockGroup;
import com.costin.eeonserver.game.world.WorldManager;
import com.costin.eeonserver.net.GameServer;
import com.costin.eeonserver.net.packets.info.BlockGroupPacket;
import com.costin.eeonserver.net.packets.info.WorldPacket;
import com.costin.eeonserver.net.packets.player.*;
import com.costin.eeonserver.net.packets.player.updates.clientside.PlayerMovePacket;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class  JoinLeaveListener implements Listener {
    @Override
    public void connected(Connection connection) {
        connection.setTimeout(5000);
        connection.setKeepAliveTCP(0);
    }

    @Override
    public void disconnected(Connection connection) {
        Player player = PlayerManager.getInstance().players.remove(connection.getID());
        if (player == null) return;
        WorldManager.getInstance().collWorld.remove(player);
        WorldManager.getInstance().collWorld.remove(player.innerCollision);
        WorldManager.getInstance().collWorld.remove(player.actionCollision);
        Log.info("eeon", player.getUsername() + " has left!");
        PlayerLeftPacket packet = new PlayerLeftPacket();
        packet.playerID = connection.getID();
        GameServer.server.sendToAllExceptTCP(connection.getID(), packet);
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof JoinRequestPacket) {
            JoinRequestPacket oldPacket = (JoinRequestPacket) object;
            if (PlayerManager.getInstance().players.get(connection.getID()) != null) return;
            if (!Objects.equals(oldPacket.clientVersion, Laws.clientVersion)) {
                int major, minor, revision;
                ConnectionDenyPacket kickPacket = new ConnectionDenyPacket();
                try {
                    String[] version = oldPacket.clientVersion.split("\\.");
                    major = Integer.parseInt(version[0]);
                    minor = Integer.parseInt(version[1]);
                    revision = Integer.parseInt(version[2]);

                    int[] newVersion = Laws.getVersion();

                    if (newVersion[0] >= major) {
                        if (newVersion[1] >= minor) {
                            if (newVersion[2] >= revision) {
                                if (newVersion[2] == revision) {
                                    //wtf?
                                    System.out.println("WTF??");
                                } else {
                                    System.out.println("connection kicked for having an old version.");
                                    kickPacket.reason = PacketEnums.ConnectionDenyReason.OLD_VERSION;
                                }
                            } else {
                                System.out.println("connection kicked for having a newer version than the server.");
                                kickPacket.reason = PacketEnums.ConnectionDenyReason.NEW_VERSION;
                            }
                        } else {
                            System.out.println("connection kicked for having a newer version than the server.");
                            kickPacket.reason = PacketEnums.ConnectionDenyReason.NEW_VERSION;
                        }
                    } else {
                        System.out.println("connection kicked for having a newer version than the server.");
                        kickPacket.reason = PacketEnums.ConnectionDenyReason.NEW_VERSION;
                    }
                } catch (Exception ignored) {
                    System.out.println("connection kicked for having an unknown version.");
                    kickPacket.reason = PacketEnums.ConnectionDenyReason.UNKNOWN_VERSION;
                }
                connection.sendTCP(kickPacket);
                connection.close();
                return;
            }
            if (oldPacket.desiredSmiley < 0 || oldPacket.desiredSmiley >= 189
                    || oldPacket.desiredAura < 0 || oldPacket.desiredAura > 13) {
                AutoKickPacket kickPacket = new AutoKickPacket();
                kickPacket.reason = PacketEnums.AutoKickReason.CLIENTSIDE_ERROR;
                connection.sendTCP(kickPacket);
                connection.close();
                return;
            }
            connection.setTimeout(12000);
            connection.setKeepAliveTCP(5000);
            RequestAcceptedPacket packet = new RequestAcceptedPacket();
            packet.newUsername = oldPacket.desiredUsername + PlayerManager.getInstance().players.size();
            packet.x = 16;
            packet.y = 480 - 16 - 16 * 5; //640, 480;
            WorldPacket world = new WorldPacket();
            List<BlockGroupPacket> blockGroupPackets = new ArrayList<>();
            for (BlockGroup group :
                    WorldManager.getInstance().EEWorld.blocks) {
                BlockGroupPacket blockGroupPacket = new BlockGroupPacket();
                blockGroupPacket.blockId = group.blockId;
                blockGroupPacket.layer = group.layer;
                blockGroupPacket.xPositions = group.xPositions;
                blockGroupPacket.yPositions = group.yPositions;
                blockGroupPackets.add(blockGroupPacket);
            }
            world.blocks = blockGroupPackets;
            world.owner = WorldManager.getInstance().EEWorld.owner;
            world.title = WorldManager.getInstance().EEWorld.title;
            world.worldWidth = WorldManager.getInstance().EEWorld.worldWidth;
            world.worldHeight = WorldManager.getInstance().EEWorld.worldHeight;
            world.gravityMultiplier = WorldManager.getInstance().EEWorld.gravityMultiplier;
            world.bgColor = WorldManager.getInstance().EEWorld.bgColor;
            world.description = WorldManager.getInstance().EEWorld.description;
            world.isCampaign = WorldManager.getInstance().EEWorld.isCampaign;
            world.crewId = WorldManager.getInstance().EEWorld.crewId;
            world.crewName = WorldManager.getInstance().EEWorld.crewName;
            world.crewStatus = WorldManager.getInstance().EEWorld.crewStatus;
            world.minimapEnabled = WorldManager.getInstance().EEWorld.minimapEnabled;
            world.ownerId = WorldManager.getInstance().EEWorld.ownerId;

            packet.EEWorld = world;
            packet.players = PlayerManager.getInstance().getPlayersAsPackets();
            PlayerJoinPacket joinPacket = new PlayerJoinPacket();
            joinPacket.username = packet.newUsername;
            joinPacket.x = packet.x;
            joinPacket.y = packet.y;
            joinPacket.playerID = connection.getID();
            joinPacket.smiley = oldPacket.desiredSmiley;
            joinPacket.golden = oldPacket.desiredGolden;
            joinPacket.aura = oldPacket.desiredAura;
            //joinPacket.god = oldPacket.

            Player player = new Player(joinPacket.x, joinPacket.y, 0, 0, joinPacket.username, joinPacket.smiley, joinPacket.golden);
            player.updatePacket(new PlayerMovePacket(), connection);
            connection.sendTCP(packet);
            WorldManager.getInstance().collWorld.add(player, packet.x + 1, packet.y + 1, 14, 14);
            WorldManager.getInstance().collWorld.add(player.innerCollision, packet.x, packet.y + 1, 16, 15);
            WorldManager.getInstance().collWorld.add(player.actionCollision, packet.x - 6, packet.y - 6, 12, 12);
            PlayerManager.getInstance().players.put(connection.getID(), player);
            GameServer.server.sendToAllExceptTCP(connection.getID(), joinPacket);
            Log.info("eeon", player.getUsername() + " has joined!");
        }

    }
}
