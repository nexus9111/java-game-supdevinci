package com.platformer.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGame extends ApplicationAdapter {

    public final static int CHARACTER_WIDTH = 64;
    public final static int CHARACTER_HEIGHT = 64;
    // Game constants
    public final static int MAP_SIDE = 19;
    public final static int MAP_CENTER = MAP_SIDE / 2;
    public final static int BACKGROUND_TILE_WIDTH = 16;
    public final static int BACKGROUND_TILE_HEIGHT = 16;
    public final static int MAP_CELL_TILE_COUNT_X = 4;
    public final static int MAP_CELL_TILE_COUNT_Y = 3;
    public final static int MAP_CELL_WIDTH = MAP_CELL_TILE_COUNT_X * BACKGROUND_TILE_WIDTH;
    public final static int MAP_CELL_HEIGHT = MAP_CELL_TILE_COUNT_Y * BACKGROUND_TILE_HEIGHT;
    private final static int PLATFORM_WIDTH = 465;
    private final static int PLATFORM_HEIGHT = 172;
    private final float CHARACTER_SPEED = 150f;
    private final float CHARACTER_ANIM_SPEED = .1f;
    private final int DIRECTIONS = 4;
    private final int PLATFORME_FRAME = 5;
    private final float GRAVITY = -300f;
    private final float JUMP_SPEED = 300f;
    private final float FAST_FALL_FORCE = -10_000f;

    private final int LEFT_KEY = Input.Keys.LEFT;
    private final int RIGHT_KEY = Input.Keys.RIGHT;
    private final int UP_KEY = Input.Keys.UP;
    private final int DOWN_KEY = Input.Keys.DOWN;
    private final float PLATFORM_ANIM_SPEED = .3f;
    private final int MAX_JUMP_COUNT = 2;
    SpriteBatch batch;
    Texture img;
    private Texture txCharacter;
    private TextureRegion[][] txrCharacterTiles;
    private Animation<TextureRegion>[] animCharacter;
    private Animation<TextureRegion>[] animPlatform;
    private float characterX, characterY;
    private int stop = 0;
    private float velocityY = 0;
    private boolean isJumping = false;
    private float time = .0f;
    private Texture txPlatform;
    private TextureRegion[][] txrPlatformTiles;
    private float platformX, platformY;
    private float platformAnimTime = 0f;
    private int jumpCount = 0;

    private Texture backgroundTexture;

    @Override
    public void create() {
        txCharacter = new Texture("character.png");
        txrCharacterTiles = TextureRegion.split(txCharacter, CHARACTER_WIDTH, CHARACTER_HEIGHT);

        animCharacter = new Animation[DIRECTIONS];
        for (int i = 0; i < animCharacter.length; i++) {
            animCharacter[i] = new Animation<TextureRegion>(CHARACTER_ANIM_SPEED, txrCharacterTiles[i]);
        }

        txPlatform = new Texture("platforme.png");
        txrPlatformTiles = TextureRegion.split(txPlatform, PLATFORM_WIDTH, PLATFORM_HEIGHT);
        animPlatform = new Animation[PLATFORME_FRAME];
        for (int i = 0; i < animPlatform.length; i++) {
            animPlatform[i] = new Animation<TextureRegion>(PLATFORM_ANIM_SPEED, txrPlatformTiles[i]);
        }

        backgroundTexture = new Texture("background.png");

        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        int dx = 0;

        if (Gdx.input.isKeyPressed(LEFT_KEY) || Gdx.input.isKeyPressed(RIGHT_KEY))
            dx = Gdx.input.isKeyPressed(LEFT_KEY) ? -1 : 1;

        if (jumpCount < MAX_JUMP_COUNT && Gdx.input.isKeyJustPressed(UP_KEY)) {
            if (isJumping) {
                jumpCount++;
            } else {
                isJumping = true;
                jumpCount = 1;
            }
            velocityY = JUMP_SPEED;
        }

        int anim = -1;
        float dt = Gdx.graphics.getDeltaTime();

        // Apply gravity and fast fall force
        float gravityForce = GRAVITY;
        if (isJumping && Gdx.input.isKeyPressed(DOWN_KEY)) {
            gravityForce += FAST_FALL_FORCE;
        }
        velocityY += gravityForce * dt;
        characterY += velocityY * dt;

        // Check if the character is on the ground
        if (characterY <= 0) {
            characterY = 0;
            isJumping = false;
            jumpCount = 0;
            velocityY = 0;
        }

        // Update character's horizontal position
        characterX += dx * CHARACTER_SPEED * dt;

        // Determine animation index
        if (dx < 0) {
            anim = 1;
        } else if (dx > 0) {
            anim = 2;
        }

        time += dt;

        // Update platform animation state
        platformAnimTime += dt;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        batch.begin();

        batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);

        // Render the animated platform
        TextureRegion txrCurrentPlatform = animPlatform[(int) (platformAnimTime / PLATFORM_ANIM_SPEED) % PLATFORME_FRAME].getKeyFrame(platformAnimTime, true);
        float newPlatformWidth = (float) PLATFORM_WIDTH / 2;
        float newPlatformHeight = (float) PLATFORM_HEIGHT / 2;
        platformX = (Gdx.graphics.getWidth() - newPlatformWidth) / 2;
        platformY = (float) Gdx.graphics.getHeight() / 2 - newPlatformHeight;
        batch.draw(txrCurrentPlatform, platformX, platformY, newPlatformWidth, newPlatformHeight);

        // Render the character
        TextureRegion txrCharacter = anim < 0 ? txrCharacterTiles[stop][0] : animCharacter[stop = anim].getKeyFrame(time, true);

        batch.draw(txrCharacter, characterX - (float) BACKGROUND_TILE_WIDTH / 2, characterY);

        batch.end();

        // Check if the character is on the platform
        boolean isCharRightOfPlatformLeftEdge = characterX + CHARACTER_WIDTH > platformX + 20;
        boolean isCharLeftOfPlatformRightEdge = characterX < platformX + newPlatformWidth - 10;
        boolean isCharAbovePlatformBottomEdge = characterY <= platformY + newPlatformHeight;
        boolean isCharBelowPlatformTopEdge = characterY + CHARACTER_HEIGHT >= platformY;
        boolean wasCharAbovePlatformTopEdge = characterY - Math.abs(velocityY * dt) + CHARACTER_HEIGHT < platformY;

        if (isCharRightOfPlatformLeftEdge &&
                isCharLeftOfPlatformRightEdge &&
                isCharAbovePlatformBottomEdge &&
                isCharBelowPlatformTopEdge &&
                velocityY <= 0) {
            characterY = platformY + newPlatformHeight;
            isJumping = false;
            velocityY = 0;
            jumpCount = 0;
        }

    }


    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        backgroundTexture.dispose();
    }
}
