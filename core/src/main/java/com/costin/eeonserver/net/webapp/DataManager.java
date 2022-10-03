package com.costin.eeonserver.net.webapp;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.costin.eeonserver.game.players.PlayerManager;
import com.costin.eeonserver.net.GameServer;
import com.costin.eeonserver.net.packets.info.PlayerChangePacket;
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
    private FileHandle file;

    public DataManager() {
        instance = this;
        json = new Json();
        try {
            generateFiles();
            JSONWrapper.singleton = json.fromJson(JSONWrapper.class, file.readString());
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

    public ArrayList<AccountObject> getAccounts() {
        return JSONWrapper.singleton.accounts;
    }

    public boolean setNickname(String name, String ip) {
        try {
            JSONWrapper.singleton = json.fromJson(JSONWrapper.class, file.readString());
            if (JSONWrapper.singleton == null) {
                JSONWrapper.singleton = new JSONWrapper();
            } else if (JSONWrapper.singleton.blacklist.stream().anyMatch(blacklistObject -> Objects.equals(blacklistObject.ip, ip)))
                return false;
            AccountObject obj = new AccountObject();
            obj.ip = ip;
            obj.name = name;
            JSONWrapper.singleton.accounts.add(obj);
            file.write(false).write(json.toJson(JSONWrapper.singleton).getBytes(StandardCharsets.UTF_8));
            ArrayList<String> names = new ArrayList<>();
            PlayerManager.getInstance().players.forEach((integer, player) -> {
                if(Objects.equals(player.getConnection().getRemoteAddressTCP().getAddress().getHostAddress(), ip)) {
                    names.add(player.getUsername());
                    player.setUsername(name);
                }
            });
            PlayerChangePacket packet = new PlayerChangePacket();
            packet.isRenamed = true;
            packet.names = names.toArray(new String[0]);
            for (int i = 1; i < names.size(); i++) {
                names.set(i, names.get(i) + (i + 1));
            }
            packet.newNames = names.toArray(new String[0]);
            GameServer.server.sendToAllTCP(packet);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof SerializationException) {
                System.out.println("A parsing error has occurred.");
                System.out.println("The file will be cleared.");
                file.write(false);
            } else {
                System.out.println("An error has occurred.");
                System.out.println("Please report this error to EEOn's developers.");
            }
            return false;
        }
    }
    public boolean addBlacklist(String ip, KickPacket packet) {
        try {
            BlacklistObject obj = new BlacklistObject();
            obj.ip = ip;
            obj.reason = packet.reason;
            JSONWrapper.singleton = json.fromJson(JSONWrapper.class, file.readString());
            if (JSONWrapper.singleton == null) {
                JSONWrapper.singleton = new JSONWrapper();
            } else if (JSONWrapper.singleton.blacklist.stream().anyMatch(blacklistObject -> Objects.equals(blacklistObject.ip, ip)))
                return false;
            JSONWrapper.singleton.blacklist.add(obj);
            file.write(false).write(json.toJson(JSONWrapper.singleton).getBytes(StandardCharsets.UTF_8));
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
                System.out.println("The file will be cleared.");
                file.write(false);
            } else {
                System.out.println("An error has occurred.");
                System.out.println("Please report this error to EEOn's developers.");
            }
            return false;
        }
    }

    private void generateFiles() throws IOException {
        file = new FileHandle("data.json");
        if (!file.exists()) {
            System.out.println("Could not find data file. creating..");
            file.write(false);
        }
    }

    public static class BlacklistObject {
        private String ip;
        private String reason;

        public String getIp() {
            return ip;
        }

        public String getReason() {
            return reason;
        }
    }

    public static class AccountObject {
        private String name;
        private String ip;

        public String getName() {
            return name;
        }

        public String getIp() {
            return ip;
        }
    }


    private static class JSONWrapper {
        public static JSONWrapper singleton;

        public ArrayList<BlacklistObject> blacklist;
        public ArrayList<AccountObject> accounts;

        public JSONWrapper() {
            blacklist = new ArrayList<>();
            accounts = new ArrayList<>();
        }
    }

}
