package com.platformer.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.platformer.game.generators.PlatformsGenerator;
import com.platformer.game.models.Character;
import com.platformer.game.models.*;

public class MyGame extends ApplicationAdapter {
    private final static float CHARACTER_SPEED = 150f;
    private final static int CHARACTER_WIDTH = 64;
    private final static int BACKGROUND_COUNT = 6;
    private final static boolean FUN_MUSIC = false;

    private final static int MENU_PAGE = 0;
    private final static int GAME_PAGE = 1;
    private final static int WINNER_PAGE = 2;

    private final Character[] characters = new Character[2];
    private Platform[] platforms;
    private int currentPage = MENU_PAGE;
    private SpriteBatch batch;
    private float elapsed;
    private Explode explode;
    private boolean gameStarted;
    private float platformAnimTime = 0f;
    private Skin skin;
    private Stage stage;
    private Character winner = null;
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
    private final Animation<TextureRegion>[] backgrounds = new Animation[BACKGROUND_COUNT];

    private Music menuMusic;
    private Music gameMusic;
    private Sound explosionSound;

    private final PlatformsGenerator pGenerator = new PlatformsGenerator();

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
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
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
        platforms = pGenerator.getAllPlatforms(currentBackgroundIndex);
        if (platforms.length == 0) {
            platforms = pGenerator.getAllPlatforms(0);
        }
    }

    private void setBackground() {
        for (int i = 0; i < BACKGROUND_COUNT; i++) {
            String lowCostStr = String.format("background%d.gif", i + 1); // use instead of "a" + "b" + "c"
            backgrounds[i] = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal(lowCostStr).read());
        }
        dynamicBackground = backgrounds[currentBackgroundIndex];
    }

    /* -------------------------------------------------------------------------- */
    /*                                   BUTTONS                                  */
    /* -------------------------------------------------------------------------- */

    private void setMenuButtons() {
        stage = new Stage(new ScreenViewport());
        createPlayButton();
        createBackgroundSelectorButtons();
        Gdx.input.setInputProcessor(stage);
    }

    private void setInputProcessorEnabled(boolean enabled) {
//        if (enabled) {
//            Gdx.input.setInputProcessor(stage);
//            return;
//        }
//        Gdx.input.setInputProcessor(null);
        Gdx.input.setInputProcessor(enabled ? stage : null);
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
                currentPage = GAME_PAGE;
            }
        });

        stage.addActor(playButton);
    }

    private void renderPlayButtonBox() {
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

    private void createBackgroundSelectorButtons() {
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
                reset();
                homeButton.remove();
                currentPage = MENU_PAGE;
            }
        });

        stage.addActor(homeButton);
    }

    // todo: change with cb function

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
        explosionSound.play(1.0f);
        explode = new Explode((int) character.getPositionX(), (int) character.getPositionY());
        explode.activate();
        if (character.getLives() <= 0) {
            gameStarted = false;
            setWinner(character);
            gameMusic.stop();
            menuMusic.play();
            this.currentPage = WINNER_PAGE;
        }
    }

    private void killPlayersHitByProjectiles() {
        States state = new States(characters);
        for (Character hittedCharacter : state.getCharactersHitByProjectile()) {
            kill(hittedCharacter);
            int random = (int) (Math.random() * platforms.length);
            hittedCharacter.setSpawn(platforms[random].getSpawnX(CHARACTER_WIDTH), platforms[random].getSpawnY());
        }
        // also clear projectiles that hit together
        state.clear();
    }

    private void killPlayersWhoFelt() {
        for (Character character : characters) {
            if (character.getPositionY() <= 0) {
                kill(character);
                int random = (int) (Math.random() * platforms.length);
                character.setSpawn(platforms[random].getSpawnX(CHARACTER_WIDTH), platforms[random].getSpawnY());
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
        setPlatforms();
        setCharacters();
        createPlayButton();
        createBackgroundSelectorButtons();
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
        batch.begin();
        renderBackground(dt);
        renderPlayButtonBox();
        renderPlayerControls(player1controls, false);
        renderPlayerControls(player2controls, true);
        stage.act(dt);
        stage.draw();
        batch.end();
    }

    private void displayWinner() {
        if (winner == null) {
            return;
        }

        batch.draw(winnerCup, ((float) Gdx.graphics.getWidth() / 2) - (winnerCup.getWidth() / 2), ((float) Gdx.graphics.getHeight() / 2) + homeButton.getHeight() + 50 + winner.getHeight(), winnerCup.getWidth(), winnerCup.getHeight());
        batch.draw(winner.getTexture(), ((float) Gdx.graphics.getWidth() / 2) - (winner.getWidth() / 2), ((float) Gdx.graphics.getHeight() / 2) + homeButton.getHeight() + 20, winner.getWidth(), winner.getHeight());
    }

    private void renderEndPage(float dt) {
        batch.begin();
        renderBackground(dt);
        displayWinner();
        stage.act(dt);
        stage.draw();
        batch.end();
    }

    private void renderGamePage(float dt) {
        updateGamePage(dt);
        batch.begin();
        renderBackground(dt);
        renderPlatforms(dt);
        renderExplosion();
        renderCharacters();
        batch.end();
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

        switch (this.currentPage) {
            case MENU_PAGE:
                renderMenuPage(dt);
                break;
            case GAME_PAGE:
                renderGamePage(dt);
                break;
            case WINNER_PAGE:
                renderEndPage(dt);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.currentPage);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        skin.dispose();
        menuButtonShape.dispose();
    }
}