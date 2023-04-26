package com.platformer.game.models;

import com.badlogic.gdx.Gdx;

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

    public boolean goLeft() {
        return Gdx.input.isKeyPressed(this.keyLeft);
    }

    public boolean goRight() {
        return Gdx.input.isKeyPressed(this.keyRight);
    }

    public boolean jump() {
        return Gdx.input.isKeyJustPressed(this.keyJump);
    }

    public boolean shoot() {
        return Gdx.input.isKeyJustPressed(this.keyShoot);
    }

    public boolean goDown() {
        return Gdx.input.isKeyPressed(this.keyDown);
    }
}
