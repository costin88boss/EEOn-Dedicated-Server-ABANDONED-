package com.costin.eeonserver.net.webapp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RootHandler implements HttpHandler {

    private final String notFound = Gdx.files.internal("assets/notFound.html").readString();

    @Override
    public void handle(HttpExchange he) throws IOException {
        FileHandle handle = Gdx.files.internal("assets/website/" + he.getRequestURI().getPath());
        FileHandle index = handle.child("index.html");
        System.out.println(handle.path());
        String response = "";
        if (!handle.exists() || !index.exists()) {
            response = notFound;
            he.sendResponseHeaders(404, response.length());
        } else {
            response = index.readString();
            he.sendResponseHeaders(200, response.length());
        }
        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));

        }
    }
}
