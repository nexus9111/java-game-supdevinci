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
    private final float CHARACTER_SPEED = 150f;
    private final float CHARACTER_ANIM_SPEED = .1f;
    private final int DIRECTIONS = 4;
    private final float GRAVITY = -300f;
    private final float JUMP_SPEED = 300f;
    private final float FAST_FALL_FORCE = -10_000f;

    private final int LEFT_KEY = Input.Keys.LEFT;
    private final int RIGHT_KEY = Input.Keys.RIGHT;
    private final int UP_KEY = Input.Keys.UP;
    private final int DOWN_KEY = Input.Keys.DOWN;
    SpriteBatch batch;
    Texture img;
    private Texture txCharacter;
    private TextureRegion[][] txrCharacterTiles;
    private Animation<TextureRegion>[] animCharacter;
    private float characterX, characterY;
    private int stop = 0;
    private float velocityY = 0;
    private boolean isJumping = false;
    private float time = .0f;

    public static int toMapX(float x) {
        return (int) x / (MAP_CELL_WIDTH / 2);
    }

    public static int toMapY(float y) {
        return (int) y / (MAP_CELL_HEIGHT / 2);
    }

    public static float fromMapX(int x) {
        return (float) x * (float) (MAP_CELL_WIDTH / 2);
    }

    public static float fromMapY(int y) {
        return (float) y * (float) (MAP_CELL_HEIGHT / 2);
    }

    @Override
    public void create() {
        txCharacter = new Texture("character.png");
        txrCharacterTiles = TextureRegion.split(txCharacter, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        img = new Texture("badlogic.jpg");

        animCharacter = new Animation[DIRECTIONS];
        for (int i = 0; i < animCharacter.length; i++) {
            animCharacter[i] = new Animation<TextureRegion>(
                    CHARACTER_ANIM_SPEED,
                    txrCharacterTiles[i]
            );
        }
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        int dx = 0;

        if (Gdx.input.isKeyPressed(LEFT_KEY) || Gdx.input.isKeyPressed(RIGHT_KEY))
            dx = Gdx.input.isKeyPressed(LEFT_KEY) ? -1 : 1;

        if (!isJumping && Gdx.input.isKeyPressed(UP_KEY)) {
            isJumping = true;
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

        batch.begin();
        TextureRegion txrCharacter = anim<0
                ? txrCharacterTiles[stop][0]
                : animCharacter[stop = anim].getKeyFrame(time, true);

        batch.draw(txrCharacter, characterX-BACKGROUND_TILE_WIDTH/2, characterY);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}
