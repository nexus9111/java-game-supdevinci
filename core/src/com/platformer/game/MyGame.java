package com.platformer.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.platformer.game.models.Character;
import com.platformer.game.models.Platform;

public class MyGame extends ApplicationAdapter {

    public final static int CHARACTER_WIDTH = 64;
    public final static int CHARACTER_HEIGHT = 64;
    public final static int BACKGROUND_TILE_WIDTH = 16;
    private final static int PLATFORM_WIDTH = 1978;
    private final static int PLATFORM_HEIGHT = 897;
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
    private final int MAX_JUMP_COUNT = 2;
    private final Platform[] platforms = new Platform[5];
    SpriteBatch batch;
    private Texture txCharacter;
    private TextureRegion[][] txrCharacterTiles;
    private Animation<TextureRegion>[] animCharacter;
    private float characterX, characterY;
    private int stop = 0;
    private float velocityY = 0;
    private boolean isJumping = false;
    private float time = .0f;
    private int jumpCount = 0;
    private float platformAnimTime = 0f;
    private Texture backgroundTexture;
    private int anim = -1;

    private void loadTextures() {
        txCharacter = new Texture("character.png");
        txrCharacterTiles = TextureRegion.split(txCharacter, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        backgroundTexture = new Texture("background.png");
    }

    private void createAnimations() {
        animCharacter = new Animation[DIRECTIONS];
        for (int i = 0; i < animCharacter.length; i++) {
            animCharacter[i] = new Animation<>(CHARACTER_ANIM_SPEED, txrCharacterTiles[i]);
        }
    }

    @Override
    public void create() {
        loadTextures();
        createAnimations();
        platforms[0] = new Platform("platform.png", PLATFORM_WIDTH, PLATFORM_HEIGHT, 10, 25, 25);
        platforms[1] = new Platform("platform.png", PLATFORM_WIDTH, PLATFORM_HEIGHT, 6, 50, 50);
        platforms[2] = new Platform("platform.png", PLATFORM_WIDTH, PLATFORM_HEIGHT, 10, 25, 75);
        platforms[3] = new Platform("platform.png", PLATFORM_WIDTH, PLATFORM_HEIGHT, 10, 75, 75);
        platforms[4] = new Platform("platform.png", PLATFORM_WIDTH, PLATFORM_HEIGHT, 10, 75, 25);

        int random = (int) (Math.random() * 5);
        characterX = platforms[random].getSpawnX(CHARACTER_WIDTH);
        characterY = platforms[random].getSpawnY();

        Character c = new Character("character.png", CHARACTER_WIDTH, CHARACTER_HEIGHT, CHARACTER_SPEED, CHARACTER_ANIM_SPEED, characterX, characterY, DIRECTIONS);

        batch = new SpriteBatch();
    }

    private void updateCharacter(float dt) {
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

        anim = -1;

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

        boolean isGoingDown = velocityY < 0;
        for (Platform p : platforms) {
            if (p.isCharacterOnIt(characterX, characterY, CHARACTER_WIDTH, CHARACTER_HEIGHT) && isGoingDown) {
                characterY = p.getPercentToBottom() + p.getHeight();
                isJumping = false;
                velocityY = 0;
                jumpCount = 0;
            }
        }
    }


    private void renderBackground() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
    }

    private void renderPlatforms(float dt) {
        platformAnimTime += dt;
        for (Platform p : platforms) {
            p.render(batch, platformAnimTime);
        }
    }

    private void renderCharacter() {
        TextureRegion txrCharacter = anim < 0 ? txrCharacterTiles[stop][0] : animCharacter[stop = anim].getKeyFrame(time, true);
        batch.draw(txrCharacter, characterX - (float) BACKGROUND_TILE_WIDTH / 2, characterY);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float dt = Gdx.graphics.getDeltaTime();

        updateCharacter(dt);

        batch.begin();

        renderBackground();
        renderPlatforms(dt);
        renderCharacter();

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
    }
}
