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
import com.platformer.game.models.GifDecoder;
import com.platformer.game.models.Platform;

public class MyGame extends ApplicationAdapter {

    public final static int CHARACTER_WIDTH = 64;
    public final static int CHARACTER_HEIGHT = 64;
    public final static int BACKGROUND_TILE_WIDTH = 16;
    private final static int PLATFORM_WIDTH = 1998;
    private final static int PLATFORM_HEIGHT = 917;
    private final float CHARACTER_SPEED = 150f;
    private final float CHARACTER_ANIM_SPEED = .1f;
    private final int DIRECTIONS = 4;
    private final Platform[] platforms = new Platform[5];
    private final Character[] characters = new Character[2];
    SpriteBatch batch;
    private Animation<TextureRegion>[] animCharacter;
    private float characterX, characterY;
    private float platformAnimTime = 0f;
    private Texture backgroundTexture;
    Animation<TextureRegion> c;
    float elapsed;

    private void loadTextures() {
        backgroundTexture = new Texture("background1.png");
    }

    @Override
    public void create() {
        loadTextures();
        platforms[0] = new Platform("platform.png", PLATFORM_WIDTH, PLATFORM_HEIGHT, 5, 50, 50, 5);
        platforms[1] = new Platform("small_platform.png", 1578, 201, 10, 25, 15, 1);
        platforms[2] = new Platform("small_platform.png", 1578, 201, 10, 25, 85, 1);
        platforms[3] = new Platform("small_platform.png", 1578, 201, 10, 75, 85, 1);
        platforms[4] = new Platform("small_platform.png", 1578, 201, 10, 75, 15, 1);

        characters[0] = new Character("character1.png",
                420,
                360,
                CHARACTER_SPEED,
                CHARACTER_ANIM_SPEED,
                characterX,
                characterY,
                4,
                Input.Keys.W,
                Input.Keys.A,
                Input.Keys.D,
                Input.Keys.S,
                -20,
                0);

        int random1 = (int) (Math.random() * 5);
        characters[0].setSpawn(platforms[random1].getSpawnX(CHARACTER_WIDTH), platforms[random1].getSpawnY());

        characters[1] = new Character("character2.png",
                80,
                60,
                CHARACTER_SPEED,
                CHARACTER_ANIM_SPEED,
                characterX,
                characterY,
                0.8,
                Input.Keys.UP,
                Input.Keys.LEFT,
                Input.Keys.RIGHT,
                Input.Keys.DOWN,
                0,
                0);

        int random2 = (int) (Math.random() * 5);
        while (random1 == random2) {
            random2 = (int) (Math.random() * 5);
        }
        characters[1].setSpawn(platforms[random2].getSpawnX(CHARACTER_WIDTH), platforms[random2].getSpawnY());

        batch = new SpriteBatch();
        c = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("background2.gif").read());
    }

    private void renderBackground(float dt) {
        elapsed += dt * 0.5;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        batch.draw(c.getKeyFrame(elapsed), 0f, 0f, screenWidth, screenHeight);
        // batch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
    }

    private void renderPlatforms(float dt) {
        platformAnimTime += dt;
        for (Platform p : platforms) {
            p.render(batch, platformAnimTime);
        }
    }

    private void updateCharacters(float dt) {
        for (Character c : characters) {
            c.update(dt, platforms);
        }
    }

    private void renderCharacters() {
        for (Character c : characters) {
            c.render(batch, BACKGROUND_TILE_WIDTH);
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float dt = Gdx.graphics.getDeltaTime();

        updateCharacters(dt);

        batch.begin();

        renderBackground(dt);
        // batch.draw(c.getKeyFrame(elapsed), 20.0f, 20.0f);
        renderPlatforms(dt);
        renderCharacters();

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
    }
}
