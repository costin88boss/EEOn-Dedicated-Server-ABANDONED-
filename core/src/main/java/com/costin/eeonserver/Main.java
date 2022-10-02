package com.costin.eeonserver;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.costin.eeonserver.game.players.Player;
import com.costin.eeonserver.game.players.PlayerManager;
import com.costin.eeonserver.game.world.BlockGroup;
import com.costin.eeonserver.game.world.WorldManager;
import com.costin.eeonserver.net.GameServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ConcurrentModificationException;
import java.util.Map;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class  Main implements ApplicationListener {

    @Override
    public void create() {
        // Prepare your application here.
        System.out.println("Starting server..");
        GameServer.start();
        // create a collision world
        for (BlockGroup group :
                WorldManager.getInstance().EEWorld.blocks) {
            for (int i = 0; i < group.xPositions.size(); i++) {
                WorldManager.getInstance().collWorld.add(group.perBlockItems.get(i), group.xPositions.get(i) * 16, 480 - group.yPositions.get(i) * 16, 16, 16);
            }//640, 480
        }
    }

    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
    }

    @Override
    public void render() {
        // Draw your application here.
        try {
            for (Player player :
                    PlayerManager.getInstance().players.values()) {
                player.update();
            }
        } catch (ConcurrentModificationException ignored) {

        }
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}