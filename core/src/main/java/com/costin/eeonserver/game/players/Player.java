package com.costin.eeonserver.game.players;

import com.badlogic.gdx.graphics.Color;
import com.costin.eeonserver.game.GameObject;
import com.costin.eeonserver.game.Laws;
import com.costin.eeonserver.game.world.BlockGroup;
import com.costin.eeonserver.game.world.WorldManager;
import com.costin.eeonserver.game.world.items.ItemId;
import com.costin.eeonserver.net.GameServer;
import com.costin.eeonserver.net.packets.player.updates.clientside.PlayerMovePacket;
import com.costin.eeonserver.net.packets.player.updates.serverside.ServerMovePacket;
import com.dongbat.jbump.*;
import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    public Item<GameObject> actionCollision;
    private float x, y, vY, vX;
    private final Connection connection;
    private String username;
    private PlayerMovePacket movePacket;
    private boolean sentStop;
    private float diffX, diffY;
    private boolean stuckInBlock;
    // collBottom = isGrounded
    private boolean collBottom, collLeft, collRight, collTop;
    private Rect rect;
    private boolean downLeftAir, downRightAir, upLeftAir, upRightAir;
    private boolean leftDownAir, leftUpAir, rightDownAir, rightUpAir;
    // NOTE: downLeft = under player's leftest edge
    // NOTE: leftDown = before player's leftest edge
    private boolean hasGodMode, isGolden;
    private Color auraColor;
    private int smileyID, auraID;

    public Player(float x, float y, float vX, float vY, String username, int smileyID, boolean smileyIsGolden, Connection connection) {
        super();
        movePacket = new PlayerMovePacket();
        actionCollision = new Item<>(this);
        //setAura
        setSmiley(smileyID);
        setVelocity(vX, vY);
        setPosition(x, y);
        setUsername(username);
        auraColor = new Color(1, 1, 1, 1);
        this.connection = connection;
        rect = new Rect(x, y, 16, 16);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getvY() {
        return vY;
    }

    public float getvX() {
        return vX;
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
        if (collBottom && !hasGodMode && !stuckInBlock) {
            vY += Laws.gravity / 5;
        }
        collBottom = false;
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
        int test = 0;
        return Math.round(y);
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
                if (collBottom || collTop) {
                    //collTop = false;
                    diffY = 0;
                    vY = 0;
                }

                diffY -= Laws.gravity / 5;

                if (movePacket.spaced && collBottom) {
                    diffY = Laws.jumpHeight / 4;
                    collBottom = false;
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

            if(!hasGodMode && !stuckInBlock) {
                ArrayList<Item> downLeft = new ArrayList<>();
                ArrayList<Item> downRight = new ArrayList<>();
                ArrayList<Item> upLeft = new ArrayList<>();
                ArrayList<Item> upRight = new ArrayList<>();
                ArrayList<Item> leftUp = new ArrayList<>();
                ArrayList<Item> leftDown  = new ArrayList<>();
                ArrayList<Item> rightUp = new ArrayList<>();
                ArrayList<Item> rightDown  = new ArrayList<>();

                WorldManager.getInstance().collWorld.queryPoint(rect.x + 0.0001f,      rect.y - 1 + 0.0001f, CollFilter.getInstance().blockFilter, downLeft);
                WorldManager.getInstance().collWorld.queryPoint(rect.x + 16 - 0.0001f, rect.y - 1 + 0.0001f, CollFilter.getInstance().blockFilter, downRight);
                WorldManager.getInstance().collWorld.queryPoint(rect.x + 0.0001f,      rect.y + 17 - 0.0001f, CollFilter.getInstance().blockFilter, upLeft);
                WorldManager.getInstance().collWorld.queryPoint(rect.x + 16 - 0.0001f, rect.y + 17 - 0.0001f, CollFilter.getInstance().blockFilter, upRight);
                //Sides
                WorldManager.getInstance().collWorld.queryPoint(rect.x - 1 + 0.0001f,  rect.y + 0.0001f, CollFilter.getInstance().blockFilter, leftDown);
                WorldManager.getInstance().collWorld.queryPoint(rect.x - 1 + 0.0001f,  rect.y + 16 - 0.0001f, CollFilter.getInstance().blockFilter, leftUp);
                WorldManager.getInstance().collWorld.queryPoint(rect.x + 17 - 0.0001f, rect.y + 0.0001f, CollFilter.getInstance().blockFilter, rightDown);
                WorldManager.getInstance().collWorld.queryPoint(rect.x + 17 - 0.0001f, rect.y + 16 - 0.0001f, CollFilter.getInstance().blockFilter, rightUp);

                if(downLeft.isEmpty()) downLeftAir = true;
                if(downRight.isEmpty()) downRightAir = true;
                if(upLeft.isEmpty()) upLeftAir = true;
                if(upRight.isEmpty()) upRightAir = true;
                //Sides
                if(leftUp.isEmpty()) leftUpAir = true;
                if(leftDown.isEmpty()) leftDownAir = true;
                if(rightUp.isEmpty()) rightUpAir = true;
                if(rightDown.isEmpty()) rightDownAir = true;

                if(!collBottom) {
                    downLeftAir = false;
                    downRightAir = false;
                }
                if(!collTop) {
                    upLeftAir = false;
                    upRightAir = false;
                }
                if(!collRight) {
                    rightUpAir = false;
                    rightDownAir = false;
                }
                if(!collLeft) {
                    leftUpAir = false;
                    leftDownAir = false;
                }

                System.out.println("Right:" + rightUpAir + "|" + rightDownAir +
                        " " + "Left:" + leftUpAir + "|" + leftDownAir +
                        " " + "Up:" + upRightAir + "|" + upLeftAir +
                        " " + "Down:" + downRightAir + "|" + downLeftAir);

                boolean test1 = true, test2 = true, test3 = true, test4 = true;
                //TEST CODE
                /*
                if(leftUpAir && leftDownAir ||
                  downLeftAir && downRightAir ||
                  upLeftAir && upRightAir) test1 = false;
                if(rightUpAir && rightDownAir ||
                        downLeftAir && downRightAir ||
                        upLeftAir && upRightAir) test2 = false;
                if(leftUpAir && leftDownAir ||
                        rightDownAir && rightUpAir ||
                        upLeftAir && upRightAir) test3 = false;
                if(leftUpAir && leftDownAir ||
                        downLeftAir && downRightAir ||
                        rightDownAir && rightUpAir) test4 = false;*/
                //TEST CODE
                //if(collLeft || collTop || collBottom) test1 = false;
                //if(collRight || collTop || collBottom) test2 = false;
                //if(collLeft || collRight || collTop) test3 = false;
                //if(collLeft || collBottom || collRight) test4 = false;


                if(rightUpAir && rightDownAir && test1) {
                    rightUpAir = false;
                    rightDownAir = false;
                    x += 1;
                    y = Math.round(y/16)*16;
                    WorldManager.getInstance().collWorld.update(this, x ,y);
                }

                if(leftUpAir && leftDownAir && test2) {
                    leftUpAir = false;
                    leftDownAir = false;
                    x -= 1;
                    y = Math.round(y/16)*16;
                    WorldManager.getInstance().collWorld.update(this, x ,y);
                }

                if(downLeftAir && downRightAir && test3) {
                    downLeftAir = false;
                    downRightAir = false;
                    float fY = y - 1;
                    float fX = Math.round(x/16)*16;
                    Response.Result res = WorldManager.getInstance().collWorld.check(this, fX, fY, CollFilter.getInstance().blockFilter);
                    if(res.projectedCollisions.isEmpty()) {
                        y -= 1;
                        x = Math.round(x / 16) * 16;
                        WorldManager.getInstance().collWorld.update(this, x, y);
                    }
                }

                if(upLeftAir && upRightAir && test4) {
                    System.out.println("Hey");
                    upLeftAir = false;
                    upRightAir = false;
                    y += 1;
                    x = Math.round(x/16)*16;
                    WorldManager.getInstance().collWorld.update(this, x, y);
                }

                if(!downLeft.isEmpty()) downLeftAir = false;
                if(!downRight.isEmpty()) downRightAir = false;
                if(!upLeft.isEmpty()) upLeftAir = false;
                if(!upRight.isEmpty()) upRightAir = false;
                if(!leftUp.isEmpty()) leftUpAir = false;
                if(!leftDown.isEmpty()) leftDownAir = false;
                if(!rightUp.isEmpty()) rightUpAir = false;
                if(!rightDown.isEmpty()) rightDownAir = false;
            } else {
                downLeftAir = false;
                downRightAir = false;
                upLeftAir = false;
                upRightAir = false;
                leftUpAir = false;
                leftDownAir = false;
                rightUpAir = false;
                rightDownAir = false;
            }

            stuckInBlock = false;
            if (!hasGodMode) {
                Response.Result res = WorldManager.getInstance().collWorld.move(this, x + vX, y + vY, CollFilter.getInstance().blockFilter);
                collBottom = false;
                collTop = false;
                collLeft = false;
                collRight = false;
                for (int i = 0; i < res.projectedCollisions.size(); i++) {
                    Collision coll = res.projectedCollisions.get(i);
                    if (coll.normal.y == 1) {
                        collBottom = true;
                    }
                    if (coll.normal.y == -1) {
                        collTop = true;
                    }
                    if(coll.normal.x == -1) {
                        collRight = true;
                    }
                    if(coll.normal.x == 1) {
                        collLeft = true;
                    }
                    if (coll.normal.x != 0) {
                        vX = 0;
                    }
                    if (coll.overlaps) {
                        stuckInBlock = true;
                        break;
                    }
                }
            } else { //640, 480
                x += vX;
                y += vY;
                WorldManager.getInstance().collWorld.update(this, x, y);
            }
            if (stuckInBlock) {
                WorldManager.getInstance().collWorld.update(this, x, y);
                vX = 0;
                vY = 0;
            }

            float _x, _y;
            if (!hasGodMode && !stuckInBlock) {
                _x = rect.x;
                _y = rect.y;
            } else {
                _x = x;
                _y = y;
            }
            if (_x >= 0) {
                if (_x + 16 <= WorldManager.getInstance().EEWorld.worldWidth * 16) {
                    x = _x;
                } else {
                    vX = 0;
                    x = (WorldManager.getInstance().EEWorld.worldWidth * 16) - 16;
                }
            } else {
                vX = 0;
                x = 0;
            }
            if (_y <= 480) {
                if (_y + 16 >= -(WorldManager.getInstance().EEWorld.worldHeight * 16 - 480 - 32)) {
                    y = _y;
                } else {
                    vY = 0;
                    y = -(WorldManager.getInstance().EEWorld.worldHeight * 16)+496;
                }
            } else {
                vY = 0;
                y = 480;
            }

            //Auto align to grid. (do not autocorrect in liquid)
            int imx = Math.round(vX) << 8;
            int imy = Math.round(vY) << 8;

            boolean moving = false;

            if (imx != 0 || (ItemId.isLiquid(0) && !hasGodMode)) {
                moving = true;
            } else if (diffX < 0.1 && diffX > -0.1) {
                float tx = x % 16;
                if (tx < 2) {
                    if (tx < .2) {
                        x = (int) x;
                    } else x -= tx / 15;
                } else if (tx > 14) {
                    if (tx > 15.8) {
                        x = (int) x;
                        x++;
                    } else x += (tx - 14) / 15;
                }

            }

            y = Math.abs(480-y); // temp invert y to comply with EE's code

            if(imy != 0 || (ItemId.isLiquid(0) && !hasGodMode)){
                moving = true;
            }else if(diffY < 0.1 && diffY > -0.1 ){
                float ty = (y-16) % 16;

                if(ty < 2){
                    if(ty < .2){
                        y = (int) y;
                    }else y -= ty/15;
                }else if(ty > 14){

                    if(ty > 15.8){
                        y = (int) y;
                        y++;
                    }else y+= (ty-14)/15;
                }
            }

            y = (480-y); // re-invert the y.
            // (better idea was to make y inverted all the time, and only invert at drawing but eh)

            rect = WorldManager.getInstance().collWorld.getRect(this);
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