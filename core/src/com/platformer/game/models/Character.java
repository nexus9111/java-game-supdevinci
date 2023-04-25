package com.platformer.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Character {
    private final Texture txCharacter;
    private final TextureRegion[][] txrCharacterTiles;
    private final Animation<TextureRegion>[] animCharacter;
    private final int characterWidth;
    private final int characterHeight;
    private final float characterSpeed;
    private final float characterAnimationSpeed;
    private final int MAX_JUMP_COUNT = 2;
    private final float GRAVITY = -300f;
    private final float JUMP_SPEED = 300f;
    private final float FAST_FALL_FORCE = -10_000f;
    private final int directions;
    private final int jumpKey;
    private final int leftKey;
    private final int rightKey;
    private final int downKey;
    private float characterX;
    private float characterY;
    private float velocityY = 0;
    private boolean isJumping = false;
    private float time = .0f;
    private int jumpCount = 0;
    private int anim = -1;
    private int stop = 0;
    private int offsetX = 0;
    private int offsetY = 0;

    public Character(String fileName, int characterWidth, int characterHeight, float characterSpeed, float characterAnimationSpeed, float characterX, float characterY, int directions, int jumpKey, int leftKey, int rightKey, int downKey, float scale, int offsetX, int offsetY) {
        this.characterWidth = (int) (characterWidth / scale);
        this.characterHeight = (int) (characterHeight / scale);
        this.characterSpeed = characterSpeed;
        this.characterAnimationSpeed = characterAnimationSpeed;
        this.characterX = characterX;
        this.characterY = characterY;
        this.directions = directions;
        this.jumpKey = jumpKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.downKey = downKey;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        this.txCharacter = new Texture(fileName);
        this.txrCharacterTiles = TextureRegion.split(this.txCharacter, characterWidth, characterHeight);

        this.animCharacter = new Animation[this.directions];
        for (int i = 0; i < this.animCharacter.length; i++) {
            this.animCharacter[i] = new Animation<>(this.characterAnimationSpeed, this.txrCharacterTiles[i]);
        }
    }

    private void updateCharacter(float dt, Platform[] platforms) {
        int dx = 0;

        if (Gdx.input.isKeyPressed(this.leftKey) || Gdx.input.isKeyPressed(this.rightKey))
            dx = Gdx.input.isKeyPressed(this.leftKey) ? -1 : 1;

        if (jumpCount < MAX_JUMP_COUNT && Gdx.input.isKeyJustPressed(this.jumpKey)) {
            if (isJumping) {
                jumpCount++;
            } else {
                isJumping = true;
                jumpCount = 1;
            }
            velocityY = JUMP_SPEED;
        }

        anim = -1;

        // Apply gravity and fast fall force
        float gravityForce = GRAVITY;
        if (isJumping && Gdx.input.isKeyPressed(this.downKey)) {
            gravityForce += FAST_FALL_FORCE;
        }
        velocityY += gravityForce * dt;
        this.characterY += velocityY * dt;

        // Check if the character is on the ground
        if (characterY <= 0) {
            this.characterY = 0;
            isJumping = false;
            jumpCount = 0;
            velocityY = 0;
        }

        // Update character's horizontal position
        characterX += dx * this.characterSpeed * dt;

        // Determine animation index
        if (dx < 0) {
            anim = 1;
        } else if (dx > 0) {
            anim = 2;
        }

        time += dt;

        boolean isGoingDown = velocityY < 0;
        for (Platform p : platforms) {
            if (p.isCharacterOnIt(characterX, characterY, this.characterWidth + this.offsetX, this.characterHeight - this.offsetY) && isGoingDown) {
                this.characterY = p.getPercentToBottom() + p.getHeight();
                isJumping = false;
                velocityY = 0;
                jumpCount = 0;
            }
        }
    }

    private void renderCharacter(SpriteBatch batch, float bgWidth) {
        TextureRegion txrCharacter = anim < 0 ? txrCharacterTiles[stop][0] : animCharacter[stop = anim].getKeyFrame(this.time, true);
        batch.draw(txrCharacter, this.characterX - bgWidth / 2, this.characterY, this.characterWidth, this.characterHeight);
    }

    public void render(SpriteBatch batch, float bgWidth) {
        renderCharacter(batch, bgWidth);
    }

    public void update (float dt, Platform[] platforms) {
        updateCharacter(dt, platforms);
    }

    public void setSpawn(float x, float y) {
        this.characterX = x;
        this.characterY = y;
    }

}
