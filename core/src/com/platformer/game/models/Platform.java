package com.platformer.game.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Platform {
    private int PLATFORM_FRAME;
    private final float PLATFORM_ANIM_SPEED = .3f;

    private final float width;
    private final float height;
    private final float percentToBottom;
    private final float percentToLeft;
    private final Animation<TextureRegion>[] animPlatform;


    /**
     * Constructs a Platform object.
     *
     * @param fileName        the file name of the texture
     * @param width           the width of the platform in pixels (in the source image)
     * @param height          the height of the platform in pixels (in the source image)
     * @param scale           the scale of the platform (the size will be divided by this number)
     * @param percentToBottom the percentage of the screen to place the platform from the bottom
     * @param percentToLeft   the percentage of the screen to place the platform from the left
     * @throws IllegalArgumentException if fileName is null, or if width, height, scale, percentToBottom, or percentToLeft is less than 0
     */
    public Platform(String fileName, int width, int height, int scale, int percentToBottom, int percentToLeft, int frames) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName cannot be null");
        }

        if (width <= 0 || height <= 0 || scale <= 0 || percentToBottom < 0 || percentToLeft < 0) {
            throw new IllegalArgumentException("width, height, scale, percentToBottom, percentToLeft cannot be less than 0");
        }

        Texture txPlatform = new Texture(fileName);
        TextureRegion[][] txrPlatformTiles = TextureRegion.split(txPlatform, width, height);

        this.animPlatform = new Animation[frames];
        for (int i = 0; i < this.animPlatform.length; i++) {
            this.animPlatform[i] = new Animation<>(this.PLATFORM_ANIM_SPEED, txrPlatformTiles[i]);
        }

        this.width = ((float) width / scale);
        this.height = ((float) height / scale);
        this.PLATFORM_FRAME = frames;
        this.percentToBottom = (float) (Gdx.graphics.getHeight() / 100 * percentToBottom) - (this.height / 2);
        this.percentToLeft = (Gdx.graphics.getWidth() - this.width) / 100 * percentToLeft;
    }

    public float getHeight() {
        return this.height;
    }

    public float getPercentToLeft() {
        return this.percentToLeft;
    }

    public void render(SpriteBatch batch, float time) {
        TextureRegion txrCurrentPlatform = this.animPlatform[(int) (time / this.PLATFORM_ANIM_SPEED) % this.PLATFORM_FRAME].getKeyFrame(time, true);
        batch.draw(txrCurrentPlatform, this.percentToLeft, this.percentToBottom, this.width, this.height);
    }


    public float getPercentToBottom() {
        return percentToBottom;
    }

    public boolean isCharacterOnIt(float characterX, float characterY, float characterWidth, float characterHeight) {
        return characterX + (characterWidth / 2) > this.percentToLeft &&
                characterX < this.percentToLeft + this.width &&
                characterY >= this.percentToBottom + this.height - 5 &&
                characterY <= this.percentToBottom + this.height + 5;
    }

    public float getSpawnX(int width) {
        return (this.percentToLeft + this.width / 2) - ((float) width / 2);
    }

    public float getSpawnY() {
        return this.percentToBottom + this.height;
    }
}
