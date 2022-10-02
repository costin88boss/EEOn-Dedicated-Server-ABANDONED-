package com.costin.eeonserver.game.players;

import com.badlogic.gdx.graphics.Color;
import com.costin.eeonserver.game.GameObject;
import com.costin.eeonserver.game.Laws;
import com.costin.eeonserver.game.world.WorldManager;
import com.costin.eeonserver.game.world.items.ItemId;
import com.costin.eeonserver.net.GameServer;
import com.costin.eeonserver.net.packets.player.updates.clientside.PlayerMovePacket;
import com.costin.eeonserver.net.packets.player.updates.serverside.ServerMovePacket;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.esotericsoftware.kryonet.Connection;

public class  Player extends GameObject {
    private static final int oX = 5, oY = 5;
    public Item<GameObject> actionCollision;
    public Item<GameObject> innerCollision;
    protected float x, y, vY, vX;
    protected Connection connection;
    protected String username;
    private PlayerMovePacket movePacket;
    private boolean sentStop;
    private float diffX, diffY;
    private boolean isGrounded;
    private boolean hitCeiling;
    private boolean hasGodMode, isGolden;
    private Color auraColor;
    private int smileyID, auraID;

    public Player(float x, float y, float vX, float vY, String username, int smileyID, boolean smileyIsGolden, Connection connection) {
        super();
        movePacket = new PlayerMovePacket();
        actionCollision = new Item<>(this);
        innerCollision = new Item<>(this);
        //setAura
        setSmiley(smileyID);
        setVelocity(vX, vY);
        setPosition(x, y);
        setUsername(username);
        auraColor = new Color(1, 1, 1, 1);
        this.connection = connection;
    }

    public void setSmiley(int newSmiley) {
        smileyID = newSmiley;
    }

    public void setAura(int newAura) {
        auraID = newAura;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setVelocity(float x, float y) {
        this.vX = x;
        this.vY = y;
    }

    public void setGodMode(boolean godMode) {
        if (isGrounded && !hasGodMode) {
            vY += Laws.gravity / 5;
            y += 1;
        }
        isGrounded = false;
        hasGodMode = godMode;
    }

    public boolean isHasGodMode() {
        return hasGodMode;
    }

    public int getAuraColor() {
        return Color.rgba8888(auraColor);
    }

    public void setAuraColor(int newColor) {
        Color color = new Color();
        Color.rgba8888ToColor(color, newColor);
        if (color.a > 0) {
            //rainbowMode = false;
            auraColor = color;
        } else {
            auraColor.set(1, 1, 1, 0);
            //rainbowMode = true;
        }
    }

    public float getCorrectY() {
        return Math.abs(y - 480);
    }

    public int getAuraID() {
        return auraID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String newName) {
        username = newName;
    }

    public int getSmileyID() {
        return smileyID;
    }

    public boolean isGolden() {
        return isGolden;
    }

    public void updatePacket(PlayerMovePacket packet, Connection connection) {
        movePacket = packet;
    }

    private void move() {
        try {
            if (movePacket.xAction == 1) diffX = Laws.playerForce / 100;
            if (movePacket.xAction == -1) diffX = -(Laws.playerForce / 100);
            if (hasGodMode) {
                if (movePacket.yAction == 1) diffY = Laws.playerForce / 100;
                if (movePacket.yAction == -1) diffY = -(Laws.playerForce / 100);
            }

            if (!hasGodMode) {
                if (isGrounded || hitCeiling) {
                    hitCeiling = false;
                    diffY = 0;
                    vY = 0;
                }

                diffY -= Laws.gravity / 5;

                if (movePacket.spaced && isGrounded) {
                    diffY = Laws.jumpHeight / 4;
                    isGrounded = false;
                }
            }

            vX += diffX;
            vY += diffY;

            if (hasGodMode) {
                if (movePacket.xAction == 0) vX *= Laws.baseDrag;
                if (movePacket.yAction == 0) vY *= Laws.baseDrag;
            } else {
                if (movePacket.xAction != 0) {
                    vX *= Laws.baseDrag;
                } else vX *= Laws.noModDrag;
            }

            if (vX > 16) {
                vX = 16;
            } else if (vX < -16) {
                vX = -16;
            } else if (vX < 0.0001 && vX > -0.0001) {
                vX = 0;
            }

            if (vY > 16) {
                vY = 16;
            } else if (vY < -16) {
                vY = -16;
            } else if (vY < 0.0001 && vY > -0.0001) {
                vY = 0;
            }

            boolean stuckInBlock = false;
            if (!hasGodMode) {
                Response.Result res = WorldManager.getInstance().collWorld.move(this, x + vX + 1, y + vY + 1, CollFilter.getInstance().blockFilter);
                boolean canGround = false;
                for (int i = 0; i < res.projectedCollisions.size(); i++) {
                    Collision coll = res.projectedCollisions.get(i);
                    if (coll.normal.y == 1) {
                        canGround = true;
                    }
                    if (coll.normal.y == -1) {
                        hitCeiling = true;
                    }
                    if (coll.normal.x != 0) {
                        vX = coll.normal.x / 1000f;
                    }
                    if(coll.overlaps) {
                        stuckInBlock = true;
                        break;
                    }
                }
                isGrounded = canGround;
            } else { //640, 480
                x += vX;
                y += vY;
                WorldManager.getInstance().collWorld.update(this, x, y);
            }
            if(stuckInBlock) {
                WorldManager.getInstance().collWorld.update(this, x, y);
                vX = 0;
                vY = 0;
            }
            Rect rect = WorldManager.getInstance().collWorld.getRect(this);
            float _x, _y;
            if (!hasGodMode && !stuckInBlock) {
                _x = rect.x - 1;
                _y = rect.y - 1;
            } else {
                _x = x;
                _y = y;
            }
            if (_x >= 0) {
                if (_x + 16 <= WorldManager.getInstance().EEWorld.worldWidth * 16) {
                    x = _x;
                } else {
                    vX = 0;
                    x -= 1;
                }
            } else {
                vX = 0;
                x += 1;
            }
            if (_y <= 480) {
                if (_y + 16 >= -(WorldManager.getInstance().EEWorld.worldHeight * 16 - 480 - 32)) {
                    y = _y;
                } else {
                    vY = 0;
                    y += 1;
                }
            } else {
                vY = 0;
                y -= 1;
            }

            //Auto align to grid. (do not autocorrect in liquid)
            int imx = Math.round(vX) << 8;
            int imy = Math.round(vY) << 8;

            boolean moving = false;

            if(imx != 0 || (ItemId.isLiquid(0) && !hasGodMode)){
                moving = true;
            }else if(diffX < 0.1 && diffX > -0.1){
                float tx = x % 16;
                if(tx < 2){
                    if(tx < .2){
                        x = (int)x;
                    } else x -= tx/15;
                }else if(tx > 14){
                    if(tx > 15.8){
                        x = (int)x;
                        x ++;
                    }else x+= (tx-14)/15;
                }

            }

            if(imy != 0 || (ItemId.isLiquid(0) && !hasGodMode)){
                moving = true;
            }else if(diffY < 0.1 && diffY > -0.1 ){
                float ty = getCorrectY() % 16;

                if(ty < 2){
                    if(ty < .2){
                        y = (int)y;
                    }else y -= ty/15;
                }else if(ty > 14){

                    if(ty > 15.8){
                        y = (int)y;
                        y ++;
                    }else y+= (ty-14)/15;
                }
            }

            diffX = 0;
            diffY = 0;

            if (vX != 0 || vY != 0) {
                sentStop = false;
                sendShit(diffX, diffY, movePacket.spaced);
            } else if (!sentStop) {
                // to avoid useless packets
                sentStop = true;
                sendShit(diffX, diffY, movePacket.spaced);
            }
        } catch (NullPointerException ignored) {
            // try-catch is for errors due to user disconnect.
        }
    }

    private void sendShit(float vXDiff, float vYDiff, boolean spaced) {
        ServerMovePacket newMovePacket = new ServerMovePacket();
        newMovePacket.id = connection.getID();
        newMovePacket.vX = vX;
        newMovePacket.vY = vY;
        newMovePacket.vXDiff = vXDiff;
        newMovePacket.vYDiff = vYDiff;
        newMovePacket.x = x;
        newMovePacket.y = y;
        newMovePacket.spaced = spaced;
        GameServer.server.sendToAllUDP(newMovePacket);
    }

    public void update() {
        move();
    }
}