package com.costin.eeonserver.net.webapp;

public class CommandHandler {
    public static String handleCommand(String cmdName) {
        String output;
        System.out.println("Received command: " + cmdName);
        String[] args = cmdName.split(" ");
        if(args.length == 0) return "Command is null!";

        switch (args[0]) {
            case "nick": {
                if(args.length <= 2) return "First argument must be a player's name, and Second argument must be the new nickname!";
                if(args.length > 3) return "Only two arguments are allowed!";
                System.out.println("Nicknaming " + args[1] + " to " + args[2]);
                output = "Nicknaming " + args[1] + " to " + args[2];
                break;
            }
            case "op": { // give admin
                if(args.length == 1) return "First argument must be a player's name!";
                if(args.length > 2) return "Only one argument is allowed!";
                System.out.println("Giving Admin to " + args[1]);
                output = "Giving Admin to " + args[1];
                break;
            }
            case "deop": { // give admin
                if(args.length == 1) return "First argument must be a player's name!";
                if(args.length > 2) return "Only one argument is allowed!";
                System.out.println("Revoking Admin from " + args[1]);
                output = "Revoking Admin from " + args[1];
                break;
            }
            case "kick": {
                if(args.length == 1) return "First argument must be a player's name!";
                if(args.length > 2) return "Only one argument is allowed!";
                System.out.println("Kicking " + args[1]);
                output = "Kicking " + args[1];
                break;
            }
            case "ban": { // ip ban
                if(args.length == 1) return "First argument must be a player's name!";
                if(args.length > 2) return "Only one argument is allowed!";
                System.out.println("Banning " + args[1]);
                output = "Banning " + args[1];
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
}
