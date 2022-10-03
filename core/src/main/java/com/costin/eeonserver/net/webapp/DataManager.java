package com.costin.eeonserver.net.webapp;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.costin.eeonserver.game.players.PlayerManager;
import com.costin.eeonserver.net.packets.player.KickPacket;
import com.esotericsoftware.kryonet.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class DataManager {
    private static DataManager instance;
    public boolean failed;
    private final Json json;
    private FileHandle blacklistFile;

    public DataManager() {
        instance = this;
        json = new Json();
        try {
            generateFiles();
            JSONWrapper.singleton = json.fromJson(JSONWrapper.class, blacklistFile.readString());
            if(JSONWrapper.singleton == null) JSONWrapper.singleton = new JSONWrapper();
        } catch (IOException | SerializationException e) {
            e.printStackTrace();
            System.out.println("An error occurred. Please report the issue to the EEOn developers.");
            failed = true;
        }
    }

    public static DataManager getInstance() {
        return instance;
    }

    public ArrayList<BlacklistObject> getBlacklist() {
        return JSONWrapper.singleton.blacklist;
    }

    public boolean addBlacklist(String name, String ip, KickPacket packet) {
        try {
            BlacklistObject obj = new BlacklistObject();
            obj.name = name;
            obj.ip = ip;
            obj.reason = packet.reason;
            JSONWrapper.singleton = json.fromJson(JSONWrapper.class, blacklistFile.readString());
            if (JSONWrapper.singleton == null) {
                JSONWrapper.singleton = new JSONWrapper();
            } else if (JSONWrapper.singleton.blacklist.stream().anyMatch(blacklistObject -> Objects.equals(blacklistObject.ip, ip)))
                return false;
            JSONWrapper.singleton.blacklist.add(obj);
            blacklistFile.write(false).write(json.toJson(JSONWrapper.singleton).getBytes(StandardCharsets.UTF_8));
            LinkedList<Connection> toClose = new LinkedList<>();
            // toClose list created because cOnCurReNt MoDiFiCaTiOn
            // Java sucks
            PlayerManager.getInstance().players.forEach((integer, player) -> {
                if (player.getConnection().getRemoteAddressTCP().getAddress().getHostAddress().equals(ip)) {
                    toClose.add(player.getConnection());
                }
            });
            toClose.forEach(connection -> {
                connection.sendTCP(packet);
                connection.close();
            });
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof SerializationException) {
                System.out.println("A parsing error has occurred.");
                System.out.println("The blacklist file will be cleared.");
                blacklistFile.write(false);
            } else {
                System.out.println("An error has occurred.");
                System.out.println("Please report this error to EEOn's developers.");
            }
            return false;
        }
    }

    private void generateFiles() throws IOException {
        blacklistFile = new FileHandle("blacklist.json");
        if (!blacklistFile.exists()) {
            System.out.println("Could not find blacklist file. creating..");
            blacklistFile.write(false);
        }
    }

    public static class BlacklistObject {
        private String name;
        private String ip;
        private String reason;

        public String getName() {
            return name;
        }

        public String getIp() {
            return ip;
        }

        public String getReason() {
            return reason;
        }
    }

    private static class JSONWrapper {
        public static JSONWrapper singleton;

        public ArrayList<BlacklistObject> blacklist;

        public JSONWrapper() {
            blacklist = new ArrayList<>();
        }
    }

}
