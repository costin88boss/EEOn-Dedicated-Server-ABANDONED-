package com.costin.eeonserver.net.webapp;

public class CommandHandler {
    public static String handleCommand(String cmdName) {
        System.out.println(cmdName);

        return "Received: " + cmdName;
    }
}
