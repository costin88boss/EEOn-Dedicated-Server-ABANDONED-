package com.costin.eeonserver.net.webapp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.costin.eeonserver.game.players.PlayerManager;
import com.costin.eeonserver.game.world.WorldManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class RootHandler implements HttpHandler {

    private final String notFound = Gdx.files.internal("notFound.html").readString();

    @Override
    public void handle(HttpExchange he) throws IOException {
        // TODO: change this with a user and pass login
        // TODO: HUGE SECURITY LEAK HERE

        // (for tomorrow)
        // TODO: above
        // TODO: chat
        // TODO: admin
        // (for tomorrow)

        //if (!he.getRemoteAddress().getAddress().getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())) return;
        FileHandle handle = Gdx.files.internal("website/" + he.getRequestURI().getPath());
        FileHandle index = handle.child("index.html");
        StringBuilder response;
        if (handle.exists() && index.exists()) {
            response = new StringBuilder(index.readString());
            he.sendResponseHeaders(200, response.length());
        } else if (Objects.equals(handle.name(), "data")) {
            response = new StringBuilder();
            if (Objects.equals(he.getRequestMethod(), "GET")) {
                response = new StringBuilder("{\"playerCount\": \"" + PlayerManager.getInstance().players.size() + "\"," +
                        "\"worldName\": \"" + WorldManager.getInstance().EEWorld.title + "\"," +
                        "\"worldOwner\": \"" + WorldManager.getInstance().EEWorld.owner + "\"," +
                        "\"players\": [");
                for (int i = 0; i < PlayerManager.getInstance().playerNames.size(); i++) {
                    response.append("\"").append(PlayerManager.getInstance().playerNames.get(i)).append("\"");
                    if (i < PlayerManager.getInstance().playerNames.size() - 1) {
                        response.append(",");
                    }
                }
                response.append("]}");
            } else if (Objects.equals(he.getRequestMethod(), "POST")) {
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(he.getRequestBody()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
                response = new StringBuilder(CommandHandler.handleCommand(sb.toString()));
            }
            he.sendResponseHeaders(200, response.length());
        } else {
            response = new StringBuilder(notFound);
            he.sendResponseHeaders(404, response.length());
        }
        try (OutputStream os = he.getResponseBody()) {
            os.write(response.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
