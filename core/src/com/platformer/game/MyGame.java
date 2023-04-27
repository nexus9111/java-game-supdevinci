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
import com.platformer.game.models.Character;
import com.platformer.game.models.*;

public class MyGame extends ApplicationAdapter {
    private final static float CHARACTER_SPEED = 150f;
    private final static int CHARACTER_WIDTH = 64;
    private final static int PLATFORM_WIDTH = 1998;
    private final static int PLATFORM_HEIGHT = 917;
    private final static int BACKGROUND_COUNT = 6;
    private final static boolean FUN_MUSIC = false;

    private final Character[] characters = new Character[2];
    private final Platform[] platforms = new Platform[5];
    private final Animation<TextureRegion>[] backgrounds = new Animation[BACKGROUND_COUNT];
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
    private Texture winnerCup;
    private TextButton playButton;
    private TextButton homeButton;
    private TextButton prevBackgroundButton;
    private TextButton nextBackgroundButton;
    private Animation<TextureRegion> dynamicBackground;

    private Character winner = null;

    private Music menuMusic;
    private Music gameMusic;


    /* -------------------------------------------------------------------------- */
    /*                               INITIALIZATION                               */
    /* -------------------------------------------------------------------------- */

    @Override
    public void create() {
        loadTextures();
        setPlatforms();
        setCharacters();
        setBackground();
        setMenuButtons();
        loadMusic();
        batch = new SpriteBatch();
    }

    private void loadTextures() {
        menuButtonShape = new Texture("buttonshape.png");
        player1controls = new Texture("player1.png");
        player2controls = new Texture("player2.png");
        winnerCup = new Texture("winner_cup.png");
    }

    private void loadMusic() { // Add this method to load the music files
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("menu_sound.wav"));
        menuMusic.setLooping(true);
        menuMusic.play();
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal(FUN_MUSIC ? "battle_sound_fun.mp3" : "battle_sound.wav"));
        gameMusic.setLooping(true);
    }


    /* -------------------------------------------------------------------------- */
    /*                                GAME SETTERS                                */
    /* -------------------------------------------------------------------------- */

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

    /* -------------------------------------------------------------------------- */
    /*                                   BUTTONS                                  */
    /* -------------------------------------------------------------------------- */

    private void setInputProcessorEnabled(boolean enabled) {
        if (enabled) {
            Gdx.input.setInputProcessor(stage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
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

    private void setMenuButtons() {
        stage = new Stage(new ScreenViewport());
        createPlayButton();
        setBackgroundSelectorButtons();
        Gdx.input.setInputProcessor(stage);
    }

    /* ------------------------------- PLAY BUTTON ------------------------------ */
    
    private void createPlayButton() {
        if (gameStarted || winner != null) {
            return;
        }
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
                prevBackgroundButton.remove();
                nextBackgroundButton.remove();
                setInputProcessorEnabled(false);
            }
        });

        stage.addActor(playButton);
    }

    private void renderPlayButton() {
        float height = (float) menuButtonShape.getHeight() / 8;
        float width = (float) menuButtonShape.getWidth() / 8;
        batch.draw(menuButtonShape, ((float) Gdx.graphics.getWidth() / 2) - (width / 2), ((float) Gdx.graphics.getHeight() / 2) - (height / 2), width, height);
    }

    /* ---------------------------- BACKGROUND BUTTON --------------------------- */

    private TextButton createSelectorButton(boolean isLeft) {
        TextButtonStyle textButtonStyle = createTextButtonStyle();

        String buttonText = isLeft ? "<" : ">";
        return new TextButton(buttonText, textButtonStyle);
    }

    private void setBackgroundSelectorButtons() {
        if (gameStarted || winner != null) {
            return;
        }
        prevBackgroundButton = createSelectorButton(true);
        prevBackgroundButton.setPosition(playButton.getX() - prevBackgroundButton.getWidth() - 30, playButton.getY());
        nextBackgroundButton = createSelectorButton(false);
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

    /* ------------------------------ RESET BUTTON ------------------------------ */

    private void createResetButton() {
        if (gameStarted || winner == null) {
            return;
        }

        TextButtonStyle textButtonStyle = createTextButtonStyle();
        homeButton = new TextButton("Menu", textButtonStyle);
        homeButton.setPosition((float) Gdx.graphics.getWidth() / 2 - homeButton.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2 - homeButton.getHeight() / 2);

        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameStarted = false;
                winner = null;
                menuMusic.stop();
                gameMusic.play();
                reset();
                homeButton.remove();
            }
        });

        stage.addActor(homeButton);
    }

    /* -------------------------------------------------------------------------- */
    /*                                GAME UPDATES                                */
    /* -------------------------------------------------------------------------- */

    // setWinner determines the winner of the game from the loser
    private void setWinner(Character loser) {
        this.winner = loser == characters[0] ? characters[1] : characters[0];
        setInputProcessorEnabled(true);
        createResetButton();
    }

    private void kill(Character character) {
        character.kill();
        explode = new Explode((int) character.getPositionX(), (int) character.getPositionY());
        explode.activate();
        if (character.getLives() <= 0) {
            gameStarted = false;
            setWinner(character);
            gameMusic.stop();
            menuMusic.play();
        }
    }

    private void killPlayersHitByProjectiles() {
        States state = new States(characters);
        for (Character hc : state.getCharactersHitByProjectile()) {
            kill(hc);
            int random = (int) (Math.random() * 5);
            hc.setSpawn(platforms[random].getSpawnX(CHARACTER_WIDTH), platforms[random].getSpawnY());
        }
        // also clear projectiles that hit together
        state.clear();
    }

    private void killPlayersWhoFelt() {
        for (Character c : characters) {
            if (c.getPositionY() <= 0) {
                kill(c);
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

    private void reset() {
        setCharacters();
        createPlayButton();
        setBackgroundSelectorButtons();
        if (explode != null && explode.isActive()) {
            explode = null;
        }
    }

    private void updateGamePage(float dt) {
        updateExplosion(dt);
        updateCharacters(dt);
    }

    private void updateExplosion(float dt) {
        if (explode != null && explode.isActive()) {
            explode.update(dt);
        }
    }

    /* -------------------------------------------------------------------------- */
    /*                                   RENDERS                                  */
    /* -------------------------------------------------------------------------- */

    private void renderMenuPage(float dt) {
        renderBackground(dt);
        renderPlayButton();
        renderPlayerControls(player1controls, false);
        renderPlayerControls(player2controls, true);
    }

    private void displayWinner() {
        if (winner == null) {
            return;
        }

        batch.draw(winnerCup, ((float) Gdx.graphics.getWidth() / 2) - (winnerCup.getWidth() / 2), ((float) Gdx.graphics.getHeight() / 2) + homeButton.getHeight() + 50 + winner.getHeight(), winnerCup.getWidth(), winnerCup.getHeight());
        batch.draw(winner.getTexture(), ((float) Gdx.graphics.getWidth() / 2) - (winner.getWidth() / 2), ((float) Gdx.graphics.getHeight() / 2) + homeButton.getHeight() + 20, winner.getWidth(), winner.getHeight());
    }

    private void renderEndPage(float dt) {
        renderBackground(dt);
        displayWinner();
        stage.act(dt);
        stage.draw();
    }

    private void renderGamePage(float dt) {
        renderBackground(dt);
        renderPlatforms(dt);
        renderExplosion();
        renderCharacters();
    }

    private void renderCharacters() {
        for (Character c : characters) {
            c.render(batch, Gdx.graphics.getWidth());
        }
    }

    private void renderBackground(float dt) {
        elapsed += dt * 0.8;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        batch.draw(dynamicBackground.getKeyFrame(elapsed), 0f, 0f, screenWidth, screenHeight);
    }

    private void renderExplosion() {
        if (explode != null && explode.isActive()) {
            explode.render(batch);
        }
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

    /* -------------------------------------------------------------------------- */
    /*                                 GAME RENDER                                */
    /* -------------------------------------------------------------------------- */

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float dt = Gdx.graphics.getDeltaTime();

        if (gameStarted) {
            updateGamePage(dt);
            batch.begin();
            renderGamePage(dt);
            batch.end();
            return;
        }

        if (winner != null) {
            batch.begin();
            renderEndPage(dt);
            batch.end();
            return;
        }

        batch.begin();
        renderMenuPage(dt);
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