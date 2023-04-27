package com.platformer.game.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public interface ICharacter {
    void render(SpriteBatch batch, float size);
    void update(float dt, Platform[] platforms);

    void kill();
    void removeProjectile(Projectile projectile);
    void setSpawn(float x, float y);

    int getHeight();
    int getLives();
    int getOffsetX();
    float getPositionX();
    float getPositionY();
    List<Projectile> getProjectiles();
    int getRemainingProjectiles();
    int getWidth();
    boolean hasProjectile(Projectile projectile);
}