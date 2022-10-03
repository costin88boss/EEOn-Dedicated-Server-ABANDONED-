package com.costin.eeonserver.net.webapp;

import com.costin.eeonserver.game.players.Player;
import com.costin.eeonserver.game.players.PlayerManager;
import com.costin.eeonserver.net.packets.player.KickPacket;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class CommandHandler {
    public static String handleCommand(String cmdName) {
        String output;
        System.out.println("Received command: " + cmdName);
        String[] args = cmdName.trim().split(" ");
        if (args.length == 0) return "Command is null!";

        switch (args[0]) {
            case "nick": {
                if (args.length <= 2)
                    return "First argument must be a player's name, and Second argument must be the new nickname!";
                if (args.length > 3) return "Only two arguments are allowed!";
                boolean could = nickUser(args[1], args[2]);
                if(could) {
                    System.out.println("Nicknaming " + args[1] + " to " + args[2]);
                    output = "Nicknaming " + args[1] + " to " + args[2];
                } else {
                    System.out.println("Could not rename " + args[1]);
                    output = "Could not rename " + args[1];
                }
                break;
            }
            case "op": { // give admin
                if (args.length == 1) return "First argument must be a player's name!";
                if (args.length > 2) return "Only one argument is allowed!";
                System.out.println("Giving Admin to " + args[1]);
                output = "Giving Admin to " + args[1];
                break;
            }
            case "deop": { // give admin
                if (args.length == 1) return "First argument must be a player's name!";
                if (args.length > 2) return "Only one argument is allowed!";
                output = "Revoking Admin from " + args[1];
                break;
            }
            case "kick": {
                if (args.length == 1) return "First argument must be a player's name!";
                StringBuilder _reason = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    _reason.append(args[i]).append(" ");
                }
                String reason = _reason.toString().trim();
                if (reason.equals("")) reason = "Kicked by an admin with no reason";
                boolean kicked = kickUser(args[1], reason);
                if (kicked) {
                    System.out.println("Kicked " + args[1]);
                    output = "Kicked " + args[1];
                } else {
                    System.out.println("Could not kick " + args[1]);
                    output = "Could not kick " + args[1];
                }
                break;
            }
            case "ban": { // ip ban
                if (args.length == 1) return "First argument must be a player's name!";
                StringBuilder _reason = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    _reason.append(args[i]).append(" ");
                }
                String reason = _reason.toString().trim();
                if (reason.equals("")) reason = "Banned by an admin with no reason";
                boolean banned = banUser(args[1], reason);
                if (banned) {
                    System.out.println("Banned " + args[1]);
                    output = "Banned " + args[1];
                } else {
                    System.out.println("Could not ban " + args[1]);
                    output = "Could not ban " + args[1];
                }
                break;
            }
            case "ping": {
                output = "pong!";
                break;
            }
            default: {
                output = "Unknown command '" + cmdName + "'";
                break;
            }

        }

        return output;
    }

    public static boolean nickUser(String user, String newName) {
        Optional<Player> oPly = PlayerManager.getInstance().players.values().stream().filter(player -> Objects.equals(player.getUsername(), user)).findFirst();
        if (!oPly.isPresent()) return false;
        Player ply = oPly.get();
        String ip = ply.getConnection().getRemoteAddressTCP().getAddress().getHostAddress();
        return DataManager.getInstance().setNickname(newName, ip);
    }

    public static boolean kickUser(String user, String... reason) {
        Optional<Player> oPly = PlayerManager.getInstance().players.values().stream().filter(player -> Objects.equals(player.getUsername(), user)).findFirst();
        if (!oPly.isPresent()) return false;
        Player ply = oPly.get();
        KickPacket packet = new KickPacket();
        packet.reason = "Kicked: " + Arrays.toString(reason);
        ply.getConnection().sendTCP(packet);
        ply.getConnection().close(); // in the face
        return true;
    }

    public static boolean banUser(String user, String... reason) {
        Optional<Player> oPly = PlayerManager.getInstance().players.values().stream().filter(player -> Objects.equals(player.getUsername(), user)).findFirst();
        if (!oPly.isPresent()) return false;
        Player ply = oPly.get();
        KickPacket packet = new KickPacket();
        packet.reason = "Banned: " + Arrays.toString(reason);
        packet.permBanned = true;
        String ip = ply.getConnection().getRemoteAddressTCP().getAddress().getHostAddress();
        return DataManager.getInstance().addBlacklist(ip, packet);
    }
}
