package com.platformer.game.models;

public class Controles {
    private final int keyDown;
    private final int keyJump;
    private final int keyLeft;
    private final int keyRight;
    private final int keyShoot;

    public Controles(int keyJump, int keyDown, int keyLeft, int keyRight, int keyShoot) {
        this.keyDown = keyDown;
        this.keyJump = keyJump;
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyShoot = keyShoot;
    }

    public int getKeyDown() {
        return keyDown;
    }

    public int getKeyJump() {
        return keyJump;
    }

    public int getKeyLeft() {
        return keyLeft;
    }

    public int getKeyRight() {
        return keyRight;
    }

    public int getKeyShoot() {
        return keyShoot;
    }
}
