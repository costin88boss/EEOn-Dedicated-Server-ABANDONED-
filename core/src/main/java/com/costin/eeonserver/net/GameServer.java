package com.costin.eeonserver.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.costin.eeonserver.game.players.CollFilter;
import com.costin.eeonserver.game.players.PlayerManager;
import com.costin.eeonserver.game.smileys.SmileyManager;
import com.costin.eeonserver.game.world.BlockGroup;
import com.costin.eeonserver.game.world.EELevel;
import com.costin.eeonserver.game.world.EEWorld;
import com.costin.eeonserver.game.world.WorldManager;
import com.costin.eeonserver.game.world.items.BlockManager;
import com.costin.eeonserver.net.listeners.JoinLeaveListener;
import com.costin.eeonserver.net.listeners.PlayerUpdateListener;
import com.costin.eeonserver.net.packets.info.BlockGroupPacket;
import com.costin.eeonserver.net.packets.info.WorldPacket;
import com.costin.eeonserver.net.packets.player.*;
import com.costin.eeonserver.net.packets.player.updates.clientside.PlayerMovePacket;
import com.costin.eeonserver.net.packets.player.updates.clientside.PlayerUpdatePacket;
import com.costin.eeonserver.net.packets.player.updates.serverside.ServerMovePacket;
import com.costin.eeonserver.net.packets.player.updates.serverside.ServerPlyUpdatePacket;
import com.costin.eeonserver.net.webapp.DataManager;
import com.costin.eeonserver.net.webapp.RootHandler;
import com.dongbat.jbump.Item;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class GameServer {
    static final int port = 20600;
    public static Server server = null;
    public static HttpServer website = null;

    private static void registerPackets() {
        server.getKryo().register(JoinRequestPacket.class);
        server.getKryo().register(PlayerJoinPacket.class);
        server.getKryo().register(PlayerLeftPacket.class);
        server.getKryo().register(RequestAcceptedPacket.class);
        server.getKryo().register(PlayerMovePacket.class);
        server.getKryo().register(ServerMovePacket.class);
        server.getKryo().register(ConnectionDenyPacket.class);
        server.getKryo().register(PacketEnums.ConnectionDenyReason.class);
        server.getKryo().register(PlayerUpdatePacket.class);
        server.getKryo().register(ServerPlyUpdatePacket.class);
        server.getKryo().register(AutoKickPacket.class);
        server.getKryo().register(PacketEnums.AutoKickReason.class);
        server.getKryo().register(KickPacket.class);

        server.getKryo().register(PlayerPacket.class);
        server.getKryo().register(WorldPacket.class);
        server.getKryo().register(BlockGroupPacket.class);

        server.getKryo().register(HashMap.class);
        server.getKryo().register(ArrayList.class);

        server.getKryo().register(EEWorld.class);
        server.getKryo().register(Color.class);
        server.getKryo().register(BlockGroup.class);
        server.getKryo().register(Item.class);
    }

    public static void start() {
        if (server != null) return;
        server = new Server();
        server.addListener(new JoinLeaveListener());
        server.addListener(new PlayerUpdateListener());
        registerPackets();

        try {
            server.bind(port, port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        new BlockManager();
        new WorldManager();
        new SmileyManager();
        new PlayerManager();
        new CollFilter();
        new DataManager();
        if (DataManager.getInstance().failed) return;

        WorldManager.getInstance().EEWorld = EELevel.read("EEOn");

        try {
            website = HttpServer.create(new InetSocketAddress(80), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        website.createContext("/", new RootHandler());
        website.createContext("/favicon.ico", t -> {
            byte[] bytes = Gdx.files.internal("website/favicon.ico").readBytes();
            t.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = t.getResponseBody()) {
                os.write(bytes);
            }
        });
        website.setExecutor(null);

        Log.info("eeon", "Loaded game resources, starting server and webpage.");
        website.start();
        server.start();
        Log.info("eeon", "Server is on!");
    }
}
