package com.platformer.game.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Projectile {
    private final Animation<TextureRegion>[] animProjectile;
    private final Character character;
    private final float y;
    private final float width;
    private final float height;
    private final float speed;
    private final boolean isLeft;
    private final Texture txProjectile;
    private final TextureRegion[][] txrProjectileTile;
    private float x;
    private float time = .0f;

    public Projectile(Character character, float width, float height, float speed, boolean isLeft, String fileName) {
        this.character = character;
        this.x = this.character.getDistanceToLeft();
        this.y = this.character.getDistanceToBottom() + 30;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.isLeft = isLeft;
        this.txProjectile = new Texture(fileName);
        this.txrProjectileTile = TextureRegion.split(this.txProjectile, (int) this.width, (int) this.height);
        this.animProjectile = new Animation[2]; // left and write
        this.animProjectile[0] = new Animation<>(0.1f, this.txrProjectileTile[0]);
        this.animProjectile[1] = new Animation<>(0.1f, this.txrProjectileTile[1]);
    }

    public void update(float deltaTime) {
        float movement = this.speed * deltaTime;
        time += deltaTime;
        this.x += this.isLeft ? -movement : movement;
    }

    public void render(SpriteBatch batch) {
        batch.draw(this.animProjectile[this.isLeft ? 0 : 1].getKeyFrame(time, true), this.x, this.y, this.width, this.height);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public boolean isOut(float bgWidth) {
        return this.x < 0 || this.x > bgWidth;
    }
}
