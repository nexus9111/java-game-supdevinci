package com.platformer.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Platform {
    private final int PLATFORME_FRAME = 5;
    private final float PLATFORM_ANIM_SPEED = .3f;
    private final float width;
    private final float height;
    private final float percentToBottom;
    private final float percentToLeft;
    private TextureRegion txrCurrentPlatform;
    private final Animation<TextureRegion>[] animPlatform;
    private final Texture txPlatform;
    private final TextureRegion[][] txrPlatformTiles;

    public Platform(String fileName, int width, int height, int scale, int x, int y) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null");
        }

        if (width <= 0 || height <= 0 || scale <= 0 || x < 0 || y < 0) {
            throw new IllegalArgumentException("width, height, scale, x, y cannot be less than 0");
        }

        txPlatform = new Texture(fileName);
        txrPlatformTiles = TextureRegion.split(txPlatform, width, height);

        this.animPlatform = new Animation[PLATFORME_FRAME];
        for (int i = 0; i < animPlatform.length; i++) {
            animPlatform[i] = new Animation<>(PLATFORM_ANIM_SPEED, txrPlatformTiles[i]);
        }

        this.width = ((float) width / scale);
        this.height = ((float) height / scale);
        this.percentToBottom = (Gdx.graphics.getWidth() - this.width) / 100 * x;
        this.percentToLeft = (float) (Gdx.graphics.getHeight() / 100 * y) - this.height;
    }

    public void render(SpriteBatch batch, float time) {
        TextureRegion txrCurrentPlatform = animPlatform[(int) (time / PLATFORM_ANIM_SPEED) % PLATFORME_FRAME].getKeyFrame(time, true);
        batch.draw(txrCurrentPlatform, this.percentToBottom, this.percentToLeft, this.width, this.height);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getPercentToBottom() {
        return percentToBottom;
    }

    public float getPercentToLeft() {
        return percentToLeft;
    }

    public boolean isCharacterOnIt(float characterX, float characterY, float characterWidth, float characterHeight) {
        return characterX + characterWidth > this.percentToBottom + 20 && characterX < this.percentToBottom + this.width - 10 && characterY <= this.percentToLeft + this.height && characterY + characterHeight >= this.percentToLeft;
    }
}
