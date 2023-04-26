package com.platformer.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.platformer.game.models.*;
import com.platformer.game.models.Character;

public class MyGame extends ApplicationAdapter {
    private final static float CHARACTER_SPEED = 150f;
    private final static int CHARACTER_WIDTH = 64;
    private final static int PLATFORM_WIDTH = 1998;
    private final static int PLATFORM_HEIGHT = 917;
    private final static int BACKGROUND_COUNT = 6;
    private final static boolean FUN_MUSIC = false;

    private final Character[] characters = new Character[2];
    private final Platform[] platforms = new Platform[5];

    private SpriteBatch batch;
    private float elapsed;
    private Explode explode;
    private boolean gameStarted;
    private float platformAnimTime = 0f;
    private Skin skin;
    private Stage stage;
    private int currentBackgroundIndex = 0;

    private Texture menuButtonShape;
    private Texture player1controls;
    private Texture player2controls;
    private TextButton playButton;
    private final Animation<TextureRegion>[] backgrounds = new Animation[BACKGROUND_COUNT];
    private Animation<TextureRegion> dynamicBackground;

    private Music menuMusic;
    private Music gameMusic;

    private void loadTextures() {
        menuButtonShape = new Texture("buttonshape.png");
        player1controls = new Texture("player1.png");
        player2controls = new Texture("player2.png");
    }

    private void loadMusic() { // Add this method to load the music files
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu_sound.wav"));
        menuMusic.setLooping(true);
        menuMusic.play();
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal(FUN_MUSIC ? "battle_sound_fun.mp3" : "battle_sound.wav"));
        gameMusic.setLooping(true);
    }

    private void setCharacters() {
        int random = (int) (Math.random() * platforms.length);
        Controles ctrlP1 = new Controles(Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D, Input.Keys.SPACE);
        characters[0] = new Character("character1.png", 420, 360, CHARACTER_SPEED, platforms[random].getSpawnX(CHARACTER_WIDTH), platforms[random].getSpawnY(), 4, ctrlP1, -30, 0, "projectile2.png");

        int random2 = (int) (Math.random() * platforms.length);
        while (random == random2) {
            random2 = (int) (Math.random() * platforms.length);
        }
        Controles ctrlP2 = new Controles(Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.ALT_RIGHT);
        characters[1] = new Character("character2.png", 80, 60, CHARACTER_SPEED, platforms[random2].getSpawnX(CHARACTER_WIDTH), platforms[random2].getSpawnY(), 0.8, ctrlP2, 0, 0, "projectile.png");
    }

    private void setPlatforms() {
        platforms[0] = new Platform("platform.png", PLATFORM_WIDTH, PLATFORM_HEIGHT, 5, 50, 50, 5);
        platforms[1] = new Platform("small_platform.png", 1578, 201, 10, 25, 15, 1);
        platforms[2] = new Platform("small_platform.png", 1578, 201, 10, 25, 85, 1);
        platforms[3] = new Platform("small_platform.png", 1578, 201, 10, 75, 85, 1);
        platforms[4] = new Platform("small_platform.png", 1578, 201, 10, 75, 15, 1);
    }

    private void setBackground() {
        for (int i = 0; i < BACKGROUND_COUNT; i++) {
            backgrounds[i] = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("background" + (i + 1) + ".gif").read());
        }
        dynamicBackground = backgrounds[currentBackgroundIndex];
    }

    private void setPlayButton() {
        stage = new Stage(new ScreenViewport());
        createPlayButton();
        Gdx.input.setInputProcessor(stage);
    }

    private void setBackgroundButtons() {
        TextButton prevBackgroundButton = createTriangleButton(true);
        prevBackgroundButton.setPosition(playButton.getX() - prevBackgroundButton.getWidth() - 30, playButton.getY());
        TextButton nextBackgroundButton = createTriangleButton(false);
        nextBackgroundButton.setPosition(playButton.getX() + playButton.getWidth() + 30, playButton.getY());

        prevBackgroundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentBackgroundIndex--;
                if (currentBackgroundIndex < 0) {
                    currentBackgroundIndex = backgrounds.length - 1;
                }
                dynamicBackground = backgrounds[currentBackgroundIndex];
            }
        });

        nextBackgroundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentBackgroundIndex++;
                if (currentBackgroundIndex >= backgrounds.length) {
                    currentBackgroundIndex = 0;
                }
                dynamicBackground = backgrounds[currentBackgroundIndex];
            }
        });

        stage.addActor(prevBackgroundButton);
        stage.addActor(nextBackgroundButton);
    }

    private TextButton createTriangleButton(boolean isLeft) {
        TextButtonStyle textButtonStyle = createTextButtonStyle();

        String buttonText = isLeft ? "<" : ">";
        return new TextButton(buttonText, textButtonStyle);
    }

    private void createPlayButton() {
        TextButtonStyle textButtonStyle = createTextButtonStyle();

        playButton = new TextButton("Jouer", textButtonStyle);
        playButton.setPosition((float) Gdx.graphics.getWidth() / 2 - playButton.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2 - playButton.getHeight() / 2);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameStarted = true;
                menuMusic.stop();
                gameMusic.play();
                reset();
                playButton.remove();
            }
        });

        stage.addActor(playButton);
    }

    private TextButtonStyle createTextButtonStyle() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default", font);

        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.overFontColor = Color.LIGHT_GRAY;
        textButtonStyle.downFontColor = Color.DARK_GRAY;

        return textButtonStyle;
    }

    private void reset() {
        setCharacters();
        createPlayButton();
        if (explode != null && explode.isActive()) {
            explode = null;
        }
    }


    private void renderBackground(float dt) {
        elapsed += dt * 0.8;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        batch.draw(dynamicBackground.getKeyFrame(elapsed), 0f, 0f, screenWidth, screenHeight);
    }

    private void renderBackgroundMenu(float dt) {
        renderBackground(dt);
        renderPlayButton();
        renderPlayerControls(player1controls, false);
        renderPlayerControls(player2controls, true);
    }

    private void renderPlayButton() {
        float height = (float) menuButtonShape.getHeight() / 8;
        float width = (float) menuButtonShape.getWidth() / 8;
        batch.draw(menuButtonShape, ((float) Gdx.graphics.getWidth() / 2) - (width / 2), ((float) Gdx.graphics.getHeight() / 2) - (height / 2), width, height);
    }

    private void renderPlayerControls(Texture playercontrols, boolean isLeft) {
        float height = (float) playercontrols.getHeight() * 3;
        float width = (float) playercontrols.getWidth() * 3;
        float posX = isLeft ? Gdx.graphics.getWidth() - width - 30 : 30;
        float posY = Gdx.graphics.getHeight() - height - 30;
        batch.draw(playercontrols, posX, posY, width, height);
    }


    private void renderPlatforms(float dt) {
        platformAnimTime += dt;
        for (Platform p : platforms) {
            p.render(batch, platformAnimTime);
        }
    }

    private void killPlayersHitByProjectiles() {
        States state = new States(characters);
        for (Character hc : state.getCharactersHitByProjectile()) {
            hc.kill();
            explode = new Explode((int) hc.getPositionX(), (int) hc.getPositionY());
            explode.activate();
            if (hc.getLives() <= 0) {
                gameStarted = false;
                gameMusic.stop();
                menuMusic.play();
                return;
            }
            int random = (int) (Math.random() * 5);
            hc.setSpawn(platforms[random].getSpawnX(CHARACTER_WIDTH), platforms[random].getSpawnY());
        }
        // also clear projectiles that hit together
        state.clear();
    }

    private void killPlayersWhoFelt() {
        for (Character c : characters) {
            if (c.getPositionY() <= 0) {
                c.kill();
                explode = new Explode((int) c.getPositionX(), (int) c.getPositionY());
                explode.activate();
                if (c.getLives() <= 0) {
                    gameStarted = false;
                    return;
                }
                int random = (int) (Math.random() * 5);
                c.setSpawn(platforms[random].getSpawnX(CHARACTER_WIDTH), platforms[random].getSpawnY());
            }
        }
    }

    private void updateCharacters(float dt) {
        for (Character c : characters) {
            c.update(dt, platforms);
        }

        killPlayersHitByProjectiles();
        killPlayersWhoFelt();
    }

    private void renderCharacters() {
        for (Character c : characters) {
            c.render(batch, Gdx.graphics.getWidth());
        }
    }

    private void updateExplosion(float dt) {
        if (explode != null && explode.isActive()) {
            explode.update(dt);
        }
    }

    private void renderExplosion() {
        if (explode != null && explode.isActive()) {
            explode.render(batch);
        }
    }

    @Override
    public void create() {
        loadTextures();
        setPlatforms();
        setCharacters();
        setBackground();
        setPlayButton();
        setBackgroundButtons();
        loadMusic();

        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float dt = Gdx.graphics.getDeltaTime();

        if (gameStarted) {
            updateExplosion(dt);
            updateCharacters(dt);

            batch.begin();
            renderBackground(dt);
            renderPlatforms(dt);
            renderExplosion();
            renderCharacters();

            batch.end();
            return;
        }
        batch.begin();
        renderBackgroundMenu(dt);
        batch.end();
        stage.act(dt);
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        skin.dispose();
        menuButtonShape.dispose();
    }
}