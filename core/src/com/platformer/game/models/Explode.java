package com.platformer.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explode {

    private final Texture txExplosion;
    private final TextureRegion[][] txrExplosionTile;
    private final Animation<TextureRegion>[] animExplosion;
    private final double EXPOLSION_REDUCTION_RATIO = 0.6;

    private boolean isVisible = false;
    private float time = .0f;
    private int x, y;
    private int width = 480;
    private int height = 480;

    public Explode(int x, int y) {
        this.x = x;
        this.y = y;

        this.txExplosion = new Texture("explosion.png");
        this.txrExplosionTile = TextureRegion.split(this.txExplosion, width, height);
        this.animExplosion = new Animation[1];
        this.animExplosion[0] = new Animation<>(0.1f, this.txrExplosionTile[0]);

    }

    public int getWidth() {
        return this.width;
    }

    public void activate() {
        this.isVisible = true;
        this.time = .0f;
    }

    public boolean isActive() {
        return this.isVisible;
    }

    public void update(float deltaTime) {
        time += deltaTime;
    }

    public void render(SpriteBatch batch) {
        if (this.isVisible) {
            batch.draw(this.animExplosion[0].getKeyFrame(time, false), this.x - ((float) (int) (this.width * EXPOLSION_REDUCTION_RATIO) / 2), this.y - 20, (int) (this.width * EXPOLSION_REDUCTION_RATIO), (int) (this.height * EXPOLSION_REDUCTION_RATIO));
            // if no more frames, hide
            if (this.animExplosion[0].isAnimationFinished(time)) {
                this.isVisible = false;
            }
        }
    }
}
